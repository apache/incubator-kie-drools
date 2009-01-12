package org.drools.runtime.pipeline.impl;

import org.drools.common.InternalRuleBase;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.impl.StatelessKnowledgeSessionImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.pipeline.Pipeline;
import org.drools.runtime.pipeline.Receiver;
import org.drools.runtime.pipeline.ResultHandler;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public class StatelessKnowledgeSessionPipelineImpl extends BaseEmitter
    implements
    Pipeline {
    private StatelessKnowledgeSession ksession;

    public StatelessKnowledgeSessionPipelineImpl(StatelessKnowledgeSession ksession) {
        this.ksession = ksession;
    }

    public void insert(Object object,
                       ResultHandler resultHandler) {
        ClassLoader cl = ((InternalRuleBase) ((StatelessKnowledgeSessionImpl) this.ksession).getRuleBase()).getRootClassLoader();                
        
        StatelessKnowledgeSessionPipelineContextImpl context = new StatelessKnowledgeSessionPipelineContextImpl(ksession, cl, resultHandler );
        
        emit( object, context );        
    }

}
