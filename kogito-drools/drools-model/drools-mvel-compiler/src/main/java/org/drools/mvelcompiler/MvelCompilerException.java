package org.drools.mvelcompiler;

public class MvelCompilerException extends RuntimeException {

    public MvelCompilerException(String message) {
        super(message);
    }

    public MvelCompilerException(ClassNotFoundException e) {
        super(e);
    }
}
