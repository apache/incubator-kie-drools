package org.kie.scanner.embedder;

import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.InputStream;

public class MavenProjectLoader {
    private static class MavenProjectHolder {
        private static final MavenProject mavenProject = loadMavenProject();

        private static MavenProject loadMavenProject() {
            File pomFile = new File( "pom.xml" );
            return parseMavenPom(pomFile);
        }
    }

    public static MavenProject parseMavenPom(File pomFile) {
        if (!pomFile.exists()) {
            return null;
        }
        MavenRequest mavenRequest = new MavenRequest();
        mavenRequest.setPom( pomFile.getAbsolutePath() );
        try {
            MavenEmbedder mavenEmbedder = new MavenEmbedder( Thread.currentThread().getContextClassLoader(), mavenRequest );
            return mavenEmbedder.readProject( pomFile );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static MavenProject parseMavenPom(InputStream pomStream) {
        MavenRequest mavenRequest = new MavenRequest();
        try {
            MavenEmbedder mavenEmbedder = new MavenEmbedder( Thread.currentThread().getContextClassLoader(), mavenRequest );
            return mavenEmbedder.readProject( pomStream );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static MavenProject loadMavenProject() {
        return MavenProjectHolder.mavenProject;
    }
}
