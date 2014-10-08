package org.jbpm.process.audit.event;

public interface AuditEvent {

    public String getExternalId();
    
    public String getProcessId();
    
    public Long getProcessInstanceId();
    
}
