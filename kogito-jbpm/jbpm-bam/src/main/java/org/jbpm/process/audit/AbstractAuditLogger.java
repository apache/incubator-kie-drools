package org.jbpm.process.audit;

import org.drools.WorkingMemory;
import org.drools.impl.StatelessKnowledgeSessionImpl;
import org.jbpm.process.audit.event.AuditEventBuilder;
import org.jbpm.process.audit.event.DefaultAuditEventBuilderImpl;
import org.kie.event.process.ProcessEventListener;
import org.kie.runtime.Environment;
import org.kie.runtime.KieSession;
import org.kie.runtime.KnowledgeRuntime;

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
    
    protected Environment env;
    
    /*
     * for backward compatibility
     */
    public AbstractAuditLogger(WorkingMemory workingMemory) {
        env = workingMemory.getEnvironment();
    }
    
    public AbstractAuditLogger(KieSession session) {
        if (session instanceof KnowledgeRuntime) {
            env = ((KnowledgeRuntime) session).getEnvironment();
        } else if (session instanceof StatelessKnowledgeSessionImpl) {
            env = ((StatelessKnowledgeSessionImpl) session).getEnvironment();
        } else {
            throw new IllegalArgumentException(
                "Not supported session in logger: " + session.getClass());
        }
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
