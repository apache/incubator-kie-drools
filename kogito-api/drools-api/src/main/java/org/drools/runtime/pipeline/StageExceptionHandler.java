package org.drools.runtime.pipeline;

public interface StageExceptionHandler {
    public void handleException(Stage stage,
                                Object object,
                                Exception exception);
}
