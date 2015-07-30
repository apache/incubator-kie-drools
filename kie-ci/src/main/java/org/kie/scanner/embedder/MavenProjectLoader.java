/*
 * Copyright 2015 JBoss Inc
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

package org.kie.scanner.embedder;

import org.apache.maven.project.MavenProject;
import org.kie.api.builder.KieScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;

public class MavenProjectLoader {
    private static final Logger log = LoggerFactory.getLogger(KieScanner.class);

    private static MavenProject mavenProject;

    public static MavenProject parseMavenPom(File pomFile) {
        return parseMavenPom(pomFile, false);
    }

    public static MavenProject parseMavenPom(File pomFile, boolean offline) {
        if (!pomFile.exists()) {
            return null;
        }
        MavenRequest mavenRequest = createMavenRequest(offline);
        mavenRequest.setPom( pomFile.getAbsolutePath() );
        try {
            return new MavenEmbedder( mavenRequest ).readProject( pomFile );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static MavenProject parseMavenPom(InputStream pomStream) {
        return parseMavenPom(pomStream, false);
    }

    public static MavenProject parseMavenPom(InputStream pomStream, boolean offline) {
        MavenRequest mavenRequest = createMavenRequest(offline);
        try {
            return new MavenEmbedder( mavenRequest ).readProject( pomStream );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static MavenRequest createMavenRequest(boolean offline) {
        MavenRequest mavenRequest = new MavenRequest();
        mavenRequest.setLocalRepositoryPath( MavenSettings.getSettings().getLocalRepository() );
        // BZ-1007894: If dependency is not resolvable and maven project builder does not complain about it,
        // then a <code>java.lang.NullPointerException</code> is thrown to the client.
        // So, the user will se an exception message "null", not descriptive about the real error.
        mavenRequest.setResolveDependencies(true);
        mavenRequest.setOffline(offline);
        return mavenRequest;
    }

    public static synchronized MavenProject loadMavenProject() {
        return loadMavenProject(false);
    }

    public static synchronized MavenProject loadMavenProject(boolean offline) {
        if (mavenProject == null) {
            File pomFile = new File( "pom.xml" );
            try {
                mavenProject = parseMavenPom(pomFile, offline);
            } catch (Exception e) {
                log.warn("Unable to parse pom.xml file of the running project: " + e.getMessage());
            }
        }
        return mavenProject;
    }
}
