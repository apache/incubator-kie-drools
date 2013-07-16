package org.jbpm.process.workitem;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.bpmn2.handler.WorkItemHandlerRuntimeException;
import org.kie.api.runtime.process.WorkItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public abstract class AbstractLogOrThrowWorkItemHandler implements WorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLogOrThrowWorkItemHandler.class);
    protected boolean logThrownException = false;

    public void setLogThrownException(boolean logException) {
        this.logThrownException = logException;
    }

    protected void handleException(Throwable cause) { 
        handleException(cause, new HashMap<String, Object>());
    }
    
    protected void handleException(Throwable cause, Map<String, Object> handlerInfoMap) { 
        String service = (String) handlerInfoMap.get("Interface");
        String operation = (String) handlerInfoMap.get("Operation");
        
        if (this.logThrownException) {
            String message;
            if( service != null ) { 
                message = this.getClass().getSimpleName() + " failed when calling " + service + "." + operation;
            } else { 
                message = this.getClass().getSimpleName() + " failed while trying to complete the task.";
            }
            logger.error(message, cause);
            
        } else {
            WorkItemHandlerRuntimeException wihRe = new WorkItemHandlerRuntimeException(cause);
            for( String key : handlerInfoMap.keySet() ) { 
                wihRe.setInformation(key, handlerInfoMap.get(key) );
            }
            wihRe.setInformation(WorkItemHandlerRuntimeException.WORKITEMHANDLERTYPE, this.getClass().getSimpleName());
            throw wihRe;
        }
    }
    
}
