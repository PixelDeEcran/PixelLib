package fr.pixeldeecran.pixellib.boostrap;

import fr.pixeldeecran.pixellib.utils.ReflectionUtils;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * This is just an interface, which allows you to do things your plugin need to start properly (like for example
 * download a library for a specific platform)
 * <p>
 * Please here, do not reference any others classes which could be asking the classloader for a class which doesn't exist.
 */
public class PBootstrapPlugin extends JavaPlugin {

    // Got from https://api.adoptium.net/v3/info/release_names
    // We assume the user is running java 8 for obvious reasons.
    public static final String LTS_JDK_RELEASE_NAME = "jdk8u302-b08";

    public void startPlugin(String pluginYmlPath) {
        try {
            JarFile jarFile = new JarFile(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()));
            Manifest manifest = jarFile.getManifest();

            File pluginFile = File.createTempFile("plugin", ".jar");
            JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(pluginFile), manifest);

            // Copying entries
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry nextEntry = entries.nextElement();

                if (!nextEntry.getName().equals("META-INF/MANIFEST.MF") && !nextEntry.getName().equals("plugin.yml")) {
                    if (nextEntry.getName().equals(pluginYmlPath)) {
                        // We re-map the plugin.yml
                        jarOut.putNextEntry(new JarEntry("plugin.yml"));

                        jarOut.write(IOUtils.toByteArray(jarFile.getInputStream(jarFile.getJarEntry(pluginYmlPath))));
                    } else {
                        jarOut.putNextEntry(nextEntry);

                        if (!nextEntry.isDirectory()) {
                            jarOut.write(IOUtils.toByteArray(jarFile.getInputStream(nextEntry)));
                        }
                    }

                    jarOut.closeEntry();
                }
            }

            jarFile.close();
            jarOut.close();

            pluginFile.deleteOnExit();

            // We load the plugin
            this.getServer().getPluginManager().loadPlugin(pluginFile);
            Plugin plugin = this.getServer().getPluginManager().getPlugin(this.getPluginLoader().getPluginDescription(pluginFile).getName());
            plugin.onLoad();
            this.getServer().getPluginManager().enablePlugin(plugin);
        } catch (IOException | InvalidDescriptionException | InvalidPluginException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void disablePlugin(Plugin plugin) {
        PluginManager pluginManager = this.getServer().getPluginManager();

        pluginManager.disablePlugin(plugin);

        // Based on org.bukkit.plugin.SimplePluginManager

        // Remove from the plugins list
        List<Plugin> plugins = (List<Plugin>) ReflectionUtils.getFieldValue(pluginManager.getClass(), "plugins", pluginManager);
        if (plugins != null) {
            plugins.remove(plugin);
        }

        // Remove from the plugins names list
        Map<String, Plugin> names = (Map<String, Plugin>) ReflectionUtils.getFieldValue(pluginManager.getClass(), "lookupNames", pluginManager);
        if (names != null) {
            names.remove(plugin.getName());
        }

        // Remove the listeners
        HandlerList.unregisterAll(plugin);

        // Remove the commands
        SimpleCommandMap commandMap = (SimpleCommandMap) ReflectionUtils.getFieldValue(Bukkit.getServer().getClass(), "commandMap", Bukkit.getServer());
        Map<String, Command> knownCommands = (Map<String, Command>) ReflectionUtils.getFieldValue(SimpleCommandMap.class, "knownCommands", commandMap);
        if (commandMap != null && knownCommands != null) {
            for (Iterator<Map.Entry<String, Command>> iterator = knownCommands.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, Command> entry = iterator.next();

                if (entry.getValue() instanceof PluginCommand) {
                    PluginCommand command = (PluginCommand) entry.getValue();

                    if (command.getPlugin() == plugin) {
                        command.unregister(commandMap);
                        iterator.remove();
                    }
                } else if (entry.getKey().split(":")[0].equalsIgnoreCase(plugin.getName())) {
                    entry.getValue().unregister(commandMap);
                    iterator.remove();
                }
            }
        }
    }

    /**
     * This is based on https://javadoc.io/doc/net.bytebuddy/byte-buddy-agent/1.9.11/net/bytebuddy/agent/ByteBuddyAgent.html#install--
     * We are only downloading JDK and setup the tools required if the java version is 8 or lower.
     */
    public void requireByteBuddyAgent() {
        int javaVersion = this.getJavaVersion();
        this.getLogger().info("Running Java " + javaVersion);

        if (javaVersion <= 8) {
            if (ToolProvider.getSystemJavaCompiler() == null) {
                this.getLogger().info("Running JRE");

                File agentDir = new File(this.getDataFolder(), "agent/");
                if (!agentDir.exists()) {
                    agentDir.mkdirs();
                }

                File toolsFile = new File(agentDir, "tools.jar");
                File windowsLib = new File(agentDir, "attach.dll");
                File linuxLib = new File(agentDir, "libattach.so");
                File macosLib = new File(agentDir, "libattach.dylib");
                if (toolsFile.exists() && (windowsLib.exists() || linuxLib.exists() || macosLib.exists())) {
                    AgentToolsInstaller installer = new AgentToolsInstaller(null, agentDir);
                    try {
                        installer.link();
                    } catch (NoSuchMethodException | ClassNotFoundException | MalformedURLException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    JDKDownloader downloader = new JDKDownloader(this);
                    downloader.setArchitecture(System.getProperty("os.arch").contains("32") ? "x32" : "x64");

                    String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
                    if (os.contains("mac") || os.contains("darwin")) {
                        downloader.setOS("mac");
                    } else if (os.contains("win")) {
                        downloader.setOS("windows");
                    } else if (os.contains("nux")) {
                        downloader.setOS("linux");
                    } else {
                        return;
                    }

                    try (ProgressBar bar = new ProgressBarBuilder()
                        .setTaskName("Download JDK")
                        .setUnit("MiB", 1048576)
                        .setStyle(ProgressBarStyle.ASCII)
                        .showSpeed()
                        .build()) {

                        downloader.downloadJDK(PBootstrapPlugin.LTS_JDK_RELEASE_NAME, max -> {
                            bar.maxHint(max);
                            bar.stepTo(0);
                        }, bar::stepTo).ifPresent(jdkFile -> {
                            AgentToolsInstaller agentInstaller = new AgentToolsInstaller(jdkFile, agentDir);
                            try {
                                agentInstaller.extract();
                                agentInstaller.link();
                            } catch (IOException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            } else {
                this.getLogger().info("Running JDK");
            }
        }
    }

    // From https://stackoverflow.com/a/2591122
    public int getJavaVersion() {
        String version = System.getProperty("java.version");

        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");

            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }

        return Integer.parseInt(version);
    }
}
