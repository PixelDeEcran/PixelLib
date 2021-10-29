package fr.pixeldeecran.pipilib.boostrap;

import fr.pixeldeecran.pipilib.utils.ReflectionUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        Map<Event, SortedSet<RegisteredListener>> listeners = (Map<Event, SortedSet<RegisteredListener>>) ReflectionUtils.getFieldValue(pluginManager.getClass(), "lookupNames", pluginManager);
        if (listeners != null) {
            for (SortedSet<RegisteredListener> set : listeners.values()) {
                set.removeIf(registeredListener -> registeredListener.getPlugin() == plugin);
            }
        }

        // Remove the commands
        SimpleCommandMap commandMap = (SimpleCommandMap) ReflectionUtils.getFieldValue(pluginManager.getClass(), "commandMap", pluginManager);
        Map<String, Command> knownCommands = (Map<String, Command>) ReflectionUtils.getFieldValue(pluginManager.getClass(), "knownCommands", pluginManager);
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
}
