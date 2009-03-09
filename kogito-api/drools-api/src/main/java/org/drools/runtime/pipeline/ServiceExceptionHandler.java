package org.drools.runtime.pipeline;


/**
 * 
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 *
 */
public interface ServiceExceptionHandler {
    public void handleException(Service service,
                                Object object,
                                Exception exception);
}
