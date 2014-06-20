package org.drools.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Drools {

    private static String droolsFullVersion;

    private static int droolsMajorVersion;
    private static int droolsMinorVersion;
    private static int droolsRevisionVersion;

    static {
        droolsFullVersion = Drools.class.getPackage().getImplementationVersion();
        if (droolsFullVersion == null) {
            InputStream is = null;
            try {
                is = Drools.class.getClassLoader().getResourceAsStream("drools.versions.properties");
                Properties properties = new Properties();
                properties.load(is);
                droolsFullVersion = properties.get("drools.version").toString();
                is.close();
            } catch ( IOException e ) {
                throw new RuntimeException(e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        String[] versionSplit = droolsFullVersion.split("\\.");
        droolsMajorVersion = Integer.parseInt(versionSplit[0]);
        droolsMinorVersion = Integer.parseInt(versionSplit[1]);
        int pos = versionSplit[2].indexOf('-');
        droolsRevisionVersion = pos >= 0 ?
                                Integer.parseInt(versionSplit[2].substring(0, pos)) :
                                Integer.parseInt(versionSplit[2]);
    }

    public static String getFullVersion() {
        return droolsFullVersion;
    }

    public static int getMajorVersion() {
        return droolsMajorVersion;
    }

    public static int getMinorVersion() {
        return droolsMinorVersion;
    }

    public static int getRevisionVersion() {
        return droolsRevisionVersion;
    }

    public static boolean isCompatible(int major, int minor, int revision) {
        if (major != droolsMajorVersion) {
            return false;
        }
        // 6.0.x and 6.1+.x aren't compatible
        return minor == 0 ? droolsMinorVersion == 0 : droolsMinorVersion > 0;
    }
}
