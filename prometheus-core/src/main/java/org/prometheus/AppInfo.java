package org.prometheus;

import java.io.IOException;
import java.util.Properties;

public class AppInfo {
    static Properties prop = new Properties();

    static {
        try {
            prop.load(AppInfo.class.getClassLoader().getResourceAsStream("build.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final String getVersion() {
        return String.format("Prometheus Engine - Versão %s.%s [%s]", //
                prop.getProperty("version"), prop.getProperty("build.number"), prop.getProperty("revision.number"));
    }

    public static void main(String[] args) {
        System.out.println(AppInfo.getVersion());
    }
}
