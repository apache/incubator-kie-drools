package org.jbpm.process.audit.event;

import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;

public interface AuditEventBuilder {

    AuditEvent buildEvent(ProcessStartedEvent pse);
    
    AuditEvent buildEvent(ProcessCompletedEvent pce, Object log);
    
    AuditEvent buildEvent(ProcessNodeTriggeredEvent pnte);
    
    AuditEvent buildEvent(ProcessNodeTriggeredEvent pnte, Object log);
    
    AuditEvent buildEvent(ProcessNodeLeftEvent pnle, Object log);
    
    AuditEvent buildEvent(ProcessVariableChangedEvent pvce);
}
