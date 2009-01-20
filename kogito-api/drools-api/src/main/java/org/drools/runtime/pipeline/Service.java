package org.drools.runtime.pipeline;

public interface Service {
    void start();

    void stop();

    void setServiceExceptionHandler(ServiceExceptionHandler exceptionHandler);

    void handleException(Service service,
                         Object object,
                         Exception exception);

}
