package org.kie.maven.integration.embedder;

public class MavenEmbedderException extends Exception {

    public MavenEmbedderException() { }

    public MavenEmbedderException(String message) {
        super( message );
    }

    public MavenEmbedderException(Throwable cause) {
        super( cause );
    }

    public MavenEmbedderException(String message, Throwable cause) {
        super( message, cause );
    }

}
