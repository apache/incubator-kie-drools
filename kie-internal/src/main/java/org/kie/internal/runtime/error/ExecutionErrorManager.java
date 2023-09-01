package org.kie.internal.runtime.error;


public interface ExecutionErrorManager {

    ExecutionErrorHandler getHandler();
    
    ExecutionErrorStorage getStorage();
}
