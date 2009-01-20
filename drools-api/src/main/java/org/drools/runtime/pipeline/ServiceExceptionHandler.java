package org.drools.runtime.pipeline;

public interface ServiceExceptionHandler {
    public void handleException(Service service,
                                Object object,
                                Exception exception);
}
