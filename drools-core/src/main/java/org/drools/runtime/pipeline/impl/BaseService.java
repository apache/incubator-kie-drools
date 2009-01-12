package org.drools.runtime.pipeline.impl;

import org.drools.runtime.pipeline.Service;
import org.drools.runtime.pipeline.ServiceExceptionHandler;

public class BaseService {
    private ServiceExceptionHandler exceptionHandler;

    public BaseService() {
        super();
    }

    public void setServiceExceptionHandler(ServiceExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public void handleException(Service service,
                                Object object,
                                Exception exception) {
        if ( this.exceptionHandler != null ) {
            this.exceptionHandler.handleException( service,
                                                   object,
                                                   exception );
        } else {
            throw new RuntimeException( exception );
        }
    }
}
