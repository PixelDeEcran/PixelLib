package fr.pixeldeecran.pixellib.boostrap;

import org.apache.commons.httpclient.util.URIUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Consumer;

public class JDKDownloader {

    private final JavaPlugin plugin;

    private String os;
    private String architecture;

    public JDKDownloader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Optional<File> downloadJDK(String releaseName, Consumer<Long> maxUpdate, Consumer<Long> progressUpdate) {
        BufferedInputStream in = null;
        FileOutputStream out = null;

        try {
            URL url = new URL("https://api.adoptium.net/v3/binary/version/" +
                URIUtil.encodePath(releaseName) + "/" +
                os + "/" +
                architecture + "/" +
                "jdk/" +
                "hotspot/" +
                "normal/" +
                "eclipse?" +
                "project=jdk");
            URLConnection connection = url.openConnection();
            int size = connection.getContentLength();

            if (size < 0) {
                System.out.println("Could not get the file size");
            } else {
                System.out.println("File size: " + size);
                maxUpdate.accept((long) size);
            }

            File jdkFile = File.createTempFile("jdk", ".zip");
            jdkFile.deleteOnExit();

            in = new BufferedInputStream(url.openStream());
            out = new FileOutputStream(jdkFile);
            byte[] data = new byte[1024];
            int count;
            double sumCount = 0.0;

            while ((count = in.read(data, 0, 1024)) != -1) {
                out.write(data, 0, count);

                sumCount += count;
                if (size > 0) {
                    progressUpdate.accept((long) sumCount);
                }
            }

            return Optional.of(jdkFile);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setOS(String os) {
        this.os = os;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getOS() {
        return os;
    }

    public String getArchitecture() {
        return architecture;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
