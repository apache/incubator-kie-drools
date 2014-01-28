package org.jbpm.process.audit;

import org.drools.core.WorkingMemory;
import org.jbpm.process.audit.event.AuditEventBuilder;
import org.jbpm.process.audit.event.DefaultAuditEventBuilderImpl;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.KieSession;

public abstract class AbstractAuditLogger implements ProcessEventListener {
    
    public static final int BEFORE_START_EVENT_TYPE = 0;
    public static final int AFTER_START_EVENT_TYPE = 1;
    public static final int BEFORE_COMPLETE_EVENT_TYPE = 2;
    public static final int AFTER_COMPLETE_EVENT_TYPE = 3;
    public static final int BEFORE_NODE_ENTER_EVENT_TYPE = 4;
    public static final int AFTER_NODE_ENTER_EVENT_TYPE = 5;
    public static final int BEFORE_NODE_LEFT_EVENT_TYPE = 6;
    public static final int AFTER_NODE_LEFT_EVENT_TYPE = 7;
    public static final int BEFORE_VAR_CHANGE_EVENT_TYPE = 8;
    public static final int AFTER_VAR_CHANGE_EVENT_TYPE = 9;
    
    protected AuditEventBuilder builder = new DefaultAuditEventBuilderImpl();
    
    /*
     * for backward compatibility
     */
    public AbstractAuditLogger(WorkingMemory workingMemory) {
        // environment is retrieved from the logged event
    }
    
    public AbstractAuditLogger(KieSession session) {
        // environment is retrieved from the logged event
    }
    /*
     * end of backward compatibility
     */
    
    public AbstractAuditLogger() {
        
    }

    public AuditEventBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(AuditEventBuilder builder) {
        this.builder = builder;
    }
    
}
