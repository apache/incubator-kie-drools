package org.jbpm.process.audit.event;

import org.kie.event.process.ProcessCompletedEvent;
import org.kie.event.process.ProcessNodeLeftEvent;
import org.kie.event.process.ProcessNodeTriggeredEvent;
import org.kie.event.process.ProcessStartedEvent;
import org.kie.event.process.ProcessVariableChangedEvent;

public interface AuditEventBuilder {

    AuditEvent buildEvent(ProcessStartedEvent pse);
    
    AuditEvent buildEvent(ProcessCompletedEvent pce, Object log);
    
    AuditEvent buildEvent(ProcessNodeTriggeredEvent pnte);
    
    AuditEvent buildEvent(ProcessNodeLeftEvent pnle, Object log);
    
    AuditEvent buildEvent(ProcessVariableChangedEvent pvce);
}
