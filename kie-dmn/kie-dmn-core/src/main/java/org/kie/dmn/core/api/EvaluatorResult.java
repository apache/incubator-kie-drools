package org.kie.dmn.core.api;

public interface EvaluatorResult {
    
    enum ResultType {
        SUCCESS, FAILURE;
    }
    
    ResultType getResultType();

    Object getResult();

}
