package org.drools.modelcompiler.util.lambdareplace;

public class LambdaTypeNeededException extends RuntimeException {

    private final String lambda;

    public LambdaTypeNeededException(String lambda) {
        this.lambda = lambda;
    }

    @Override
    public String getMessage() {
        return "Missing argument in Lambda: " + lambda;
    }
}