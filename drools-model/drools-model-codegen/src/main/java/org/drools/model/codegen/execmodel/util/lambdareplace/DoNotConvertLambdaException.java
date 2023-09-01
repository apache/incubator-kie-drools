package org.drools.model.codegen.execmodel.util.lambdareplace;

public abstract class DoNotConvertLambdaException extends RuntimeException {

    protected final String lambda;

    public DoNotConvertLambdaException(String lambda) {
        this.lambda = lambda;
    }
}
