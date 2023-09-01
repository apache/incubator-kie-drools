package org.drools.ancompiler;

public class CouldNotCreateAlphaNetworkCompilerException extends RuntimeException {

    public CouldNotCreateAlphaNetworkCompilerException(Exception e) {
        super("Cannot create Compiled Alpha Network", e);
    }

    public CouldNotCreateAlphaNetworkCompilerException(String message) {
        super(message);
    }
}
