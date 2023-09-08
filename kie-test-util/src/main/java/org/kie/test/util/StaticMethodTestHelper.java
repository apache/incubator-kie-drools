/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.test.util;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class StaticMethodTestHelper {

    private static final String jarLocRegexStr = ".*-(\\d+\\.\\d+(\\.\\d+)?)(-SNAPSHOT)?.jar";
    private static final Pattern jarLocRegex = Pattern.compile(jarLocRegexStr);

    public static boolean projectVersionIsLessThan(double version) {
        String projVersionStr = getProjectVersion();
        return isLessThanProjectVersion(projVersionStr, version);
    }

    static boolean isLessThanProjectVersion( String projVersionStr, double version) {
        projVersionStr = projVersionStr.replace("-SNAPSHOT", "");

        int firstPoint = projVersionStr.indexOf('.');
        int secondPoint = projVersionStr.substring(++firstPoint).indexOf('.') + firstPoint;
        projVersionStr = projVersionStr.substring(0, secondPoint);
        return ( Double.parseDouble(projVersionStr) < version );
    }

    static String getProjectVersion() {
        URL codeLocUrl = StaticMethodTestHelper.class.getProtectionDomain().getCodeSource().getLocation();

        String projVersionStr = null;
        String codeLocStr = null;
        try {
            codeLocStr = codeLocUrl.toURI().toString();
            if( codeLocStr.endsWith(".jar") ) {
                Matcher jarLocMatcher = jarLocRegex.matcher(codeLocStr);
                assertThat(jarLocMatcher.matches() && jarLocMatcher.groupCount() >= 2).as("Regex for code (jar) location did not match location!").isTrue();
                projVersionStr = jarLocMatcher.group(1);
            } else {
                codeLocStr = codeLocStr.replace("target/classes/", "pom.xml");
                File pomFile = new File(new URI(codeLocStr));
                assertThat(pomFile.exists()).as(codeLocStr + " does not exist!").isTrue();
                Reader reader = null;
                try {
                    reader = new FileReader(pomFile);
                    MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
                    Model model = xpp3Reader.read(reader);
                    projVersionStr = model.getVersion();
                    if( projVersionStr == null ) {
                        projVersionStr = model.getParent().getVersion();
                    }
                    String projectName = model.getGroupId() + ":" + model.getArtifactId();
                    assertThat(projVersionStr).as("Unable to resolve project version for " + projectName).isNotNull();
                } catch( FileNotFoundException fnfe ) {
                    throw new RuntimeException("Unable to open " + pomFile.getAbsolutePath(), fnfe );
                } catch( IOException ioe ) {
                    throw new RuntimeException("Unable to read " + codeLocStr, ioe );
                } catch( XmlPullParserException xppe ) {
                    throw new RuntimeException("Unable to parse " + codeLocStr, xppe );
                } finally {
                    try {
                        if( reader != null ) {
                            reader.close();
                        }
                    } catch( IOException e ) {
                        // no-op
                    }
                }
            }
        } catch( URISyntaxException urise ) {
            throw new RuntimeException("Invalid URL: " + codeLocStr, urise);
        }

        return projVersionStr;
    }

}
