package org.drools.scanner.embedder;

import org.apache.maven.project.MavenProject;

import java.io.File;

public class MavenProjectLoader {
    private static class MavenProjectHolder {
        private static final MavenProject mavenProject = loadMavenProject();

        private static MavenProject loadMavenProject() {
            File pomFile = new File( "pom.xml" );
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
    }

    public static MavenProject loadMavenProject() {
        return MavenProjectHolder.mavenProject;
    }
}
