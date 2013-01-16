package org.jbpm.process.audit;

import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryLogger;
import org.drools.audit.event.LogEvent;
import org.drools.impl.StatelessKnowledgeSessionImpl;
import org.jbpm.process.audit.event.ExtendedRuleFlowLogEvent;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.event.KnowledgeRuntimeEventManager;
import org.kie.event.process.ProcessCompletedEvent;
import org.kie.event.process.ProcessStartedEvent;
import org.kie.runtime.Environment;
import org.kie.runtime.KnowledgeRuntime;

public abstract class AbstractAuditLogger extends WorkingMemoryLogger {
    
    protected Environment env;
    
    public AbstractAuditLogger(WorkingMemory workingMemory) {
        super(workingMemory);
        env = workingMemory.getEnvironment();
    }
    
    public AbstractAuditLogger(KnowledgeRuntimeEventManager session) {
        super(session);
        if (session instanceof KnowledgeRuntime) {
            env = ((KnowledgeRuntime) session).getEnvironment();
        } else if (session instanceof StatelessKnowledgeSessionImpl) {
            env = ((StatelessKnowledgeSessionImpl) session).getEnvironment();
        } else {
            throw new IllegalArgumentException(
                "Not supported session in logger: " + session.getClass());
        }
    }
    
    public void beforeProcessStarted(ProcessStartedEvent event) {
        long parentProcessInstanceId = -1;
        try {
            ProcessInstanceImpl processInstance = (ProcessInstanceImpl) event.getProcessInstance();
            parentProcessInstanceId = (Long) processInstance.getMetaData().get("ParentProcessInstanceId");
        } catch (Exception e) {
            //in case of problems with getting hold of parentProcessInstanceId don't break the operation
        }
        LogEvent logEvent =  new ExtendedRuleFlowLogEvent( LogEvent.BEFORE_RULEFLOW_CREATED,
                event.getProcessInstance().getProcessId(),
                event.getProcessInstance().getProcessName(),
                event.getProcessInstance().getId(), parentProcessInstanceId) ;
        
        // filters are not available from super class, TODO make fireLogEvent protected instead of private in WorkinMemoryLogger
        logEventCreated( logEvent );
    }

    public void afterProcessCompleted(ProcessCompletedEvent event) {
        String outcome = null;
        try {
            ProcessInstanceImpl processInstance = (ProcessInstanceImpl) event.getProcessInstance();
            outcome = processInstance.getOutcome();
        } catch (Exception e) {
            //in case of problems with getting hold of parentProcessInstanceId don't break the operation
        }
        LogEvent logEvent =  new ExtendedRuleFlowLogEvent(LogEvent.AFTER_RULEFLOW_COMPLETED,
                event.getProcessInstance().getProcessId(),
                event.getProcessInstance().getProcessName(),
                event.getProcessInstance().getId(), event.getProcessInstance().getState(), outcome) ;
        
        // filters are not available from super class, TODO make fireLogEvent protected instead of private in WorkinMemoryLogger
        logEventCreated( logEvent );
    }
}
