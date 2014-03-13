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
        if (!pomFile.exists()) {
            return null;
        }
        MavenRequest mavenRequest = createMavenRequest();
        mavenRequest.setPom( pomFile.getAbsolutePath() );
        try {
            MavenEmbedder mavenEmbedder = new MavenEmbedder( Thread.currentThread().getContextClassLoader(), mavenRequest );
            return mavenEmbedder.readProject( pomFile );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static MavenProject parseMavenPom(InputStream pomStream) {
        MavenRequest mavenRequest = createMavenRequest();
        try {
            MavenEmbedder mavenEmbedder = new MavenEmbedder( Thread.currentThread().getContextClassLoader(), mavenRequest );
            return mavenEmbedder.readProject( pomStream );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static MavenRequest createMavenRequest() {
        MavenRequest mavenRequest = new MavenRequest();
        mavenRequest.setLocalRepositoryPath( MavenSettings.getSettings().getLocalRepository() );
        // BZ-1007894: If dependency is not resolvable and maven project builder does not complain about it,
        // then a <code>java.lang.NullPointerException</code> is thrown to the client.
        // So, the user will se an exception message "null", not descriptive about the real error.
        mavenRequest.setResolveDependencies(true);
        return mavenRequest;
    }

    public static synchronized MavenProject loadMavenProject() {
        if (mavenProject == null) {
            File pomFile = new File( "pom.xml" );
            try {
                mavenProject = parseMavenPom(pomFile);
            } catch (Exception e) {
                log.warn("Unable to parse pom.xml file of the running project: " + e.getMessage());
            }
        }
        return mavenProject;
    }
}
