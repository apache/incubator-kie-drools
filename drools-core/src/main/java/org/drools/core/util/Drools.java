/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Drools {

    private static Pattern VERSION_PAT = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)([\\.-](.*))?");

    private static String droolsFullVersion;
    private static int droolsMajorVersion;
    private static int droolsMinorVersion;
    private static int droolsRevisionVersion;
    private static String droolsRevisionClassifier;

    static {
        droolsFullVersion = Drools.class.getPackage().getImplementationVersion();
        if (droolsFullVersion == null || droolsFullVersion.equals("0.0")) {
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

        Matcher m = VERSION_PAT.matcher(droolsFullVersion);
        if( m.matches() ) {
            droolsMajorVersion = Integer.parseInt(m.group(1));
            droolsMinorVersion = Integer.parseInt(m.group(2));
            droolsRevisionVersion = Integer.parseInt(m.group(3));
            droolsRevisionClassifier = m.group(5);
        }
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

    public static String getRevisionClassifier() {
        return droolsRevisionClassifier;
    }

    public static boolean isCompatible(int major, int minor, int revision) {
        if (major != droolsMajorVersion) {
            return false;
        }
        // 6.0.x and 6.1+.x aren't compatible
        return minor == 0 ? droolsMinorVersion == 0 : droolsMinorVersion > 0;
    }
}
