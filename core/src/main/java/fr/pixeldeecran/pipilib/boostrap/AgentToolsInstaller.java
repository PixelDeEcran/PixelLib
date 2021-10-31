package fr.pixeldeecran.pipilib.boostrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AgentToolsInstaller {

    private final File jdkFile;
    private final File extractToDir;

    public AgentToolsInstaller(File jdkFile, File extractToDir) {
        this.jdkFile = jdkFile;
        this.extractToDir = extractToDir;
    }

    public void extract() throws IOException {
        ZipInputStream zin = new ZipInputStream(new FileInputStream(this.jdkFile));
        ZipEntry ze;
        while ((ze = zin.getNextEntry()) != null) {
            if (ze.getName().endsWith("attach.dll")
                || ze.getName().endsWith("libattach.so")
                || ze.getName().endsWith("libattach.dylib")
                || ze.getName().endsWith("tools.jar")) {
                String[] path = ze.getName().split("/");
                String name = path[path.length - 1];

                File file = new File(extractToDir, name);
                if (!file.exists()) {
                    file.createNewFile();

                    FileOutputStream out = new FileOutputStream(file);
                    byte[] buffer = new byte[9000];
                    int len;
                    while ((len = zin.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    out.close();
                    zin.closeEntry();
                }
            }
        }
        zin.close();
    }

    public void link() throws NoSuchMethodException, ClassNotFoundException, MalformedURLException, InvocationTargetException, IllegalAccessException {
        // Configure ByteBuddy
        File toolsFile = new File(this.extractToDir, "tools.jar");
        if (System.getProperty("net.bytebuddy.agent.toolsjar") == null) {
            System.setProperty("net.bytebuddy.agent.toolsjar", toolsFile.getAbsolutePath());
        }

        URL url = toolsFile.toURI().toURL();
        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(classLoader, url);

        // Here, we assume that the native attach lib exists
        if (System.getProperty("java.library.path") == null) {
            // We set the path
            System.setProperty("java.library.path", this.extractToDir.getAbsolutePath());
        } else {
            // We just add a path
            System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + this.extractToDir.getAbsolutePath());
        }

        if (System.getProperty("jna.library.path") == null) {
            // We set the path
            System.setProperty("jna.library.path", this.extractToDir.getAbsolutePath());
        } else {
            // We just add a path
            System.setProperty("jna.library.path", System.getProperty("jna.library.path") + File.pathSeparator + this.extractToDir.getAbsolutePath());
        }

        try {
            // Reload classpath
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    public File getJdkFile() {
        return jdkFile;
    }

    public File getExtractToDir() {
        return extractToDir;
    }
}
