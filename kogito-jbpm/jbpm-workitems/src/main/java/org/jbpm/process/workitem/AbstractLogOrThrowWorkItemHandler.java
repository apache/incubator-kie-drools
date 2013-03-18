package org.jbpm.process.workitem;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.bpmn2.handler.WorkItemHandlerRuntimeException;
import org.kie.api.runtime.process.WorkItemHandler;



public abstract class AbstractLogOrThrowWorkItemHandler implements WorkItemHandler {

    protected boolean logThrownException = true;

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
            System.err.println(message);
            
            cause.printStackTrace(System.err);
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
