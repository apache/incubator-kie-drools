package org.drools.modelcompiler.util.lambdareplace;

public class LambdaTypeNeededException extends DoNotConvertLambdaException {

    private final String lambda;

    public LambdaTypeNeededException(String lambda) {
        super(lambda);
        this.lambda = lambda;
    }

    @Override
    public String getMessage() {
        return "Missing argument in Lambda: " + lambda;
    }
}