package org.jbpm.bpmn2.handler;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.*;

public class SignallingTaskHandlerWrapper extends AbstractExceptionHandlingTaskHandler {

    
    final private KieSession ksession;
    final private String eventType;
    
    private String workItemExceptionParameterName = "jbpm.workitem.exception";
    
    public SignallingTaskHandlerWrapper(Class<? extends WorkItemHandler> originalTaskHandlerClass, String eventType, KieSession ksession) {
        super(originalTaskHandlerClass);
        this.ksession = ksession;
        this.eventType = eventType;
    }
    
    public SignallingTaskHandlerWrapper(WorkItemHandler originalTaskHandler, String eventType, KieSession ksession) {
        super(originalTaskHandler);
        this.ksession = ksession;
        this.eventType = eventType;
    }
    
    public void setWorkItemExceptionParameterName(String parameterName) { 
        this.workItemExceptionParameterName = parameterName;
    }
    
    public String getWorkItemExceptionParameterName() { 
        return this.workItemExceptionParameterName;
    }

    @Override
    public void handleExecuteException(Throwable cause, WorkItem workItem, WorkItemManager manager) {
        workItem.getParameters().put(this.workItemExceptionParameterName, cause);
        this.ksession.signalEvent(this.eventType, workItem, workItem.getProcessInstanceId());
    }

    @Override
    public void handleAbortException(Throwable cause, WorkItem workItem, WorkItemManager manager) {
        workItem.getParameters().put(this.workItemExceptionParameterName, cause);
        this.ksession.signalEvent(this.eventType, workItem, workItem.getProcessInstanceId());
    }

}
