package org.drools.scanner.embedder;

import org.apache.maven.project.MavenProject;

import java.io.File;

public class MavenProjectLoader {
    private static class MavenProjectHolder {
        private static final MavenProject mavenProject = loadMavenProject();

        private static MavenProject loadMavenProject() {
            MavenRequest mavenRequest = new MavenRequest();
            mavenRequest.setPom( new File( "pom.xml" ).getAbsolutePath() );
            try {
                MavenEmbedder mavenEmbedder = new MavenEmbedder( Thread.currentThread().getContextClassLoader(), mavenRequest );
                return mavenEmbedder.readProject( new File( "pom.xml" ) );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static MavenProject loadMavenProject() {
        return MavenProjectHolder.mavenProject;
    }
}
