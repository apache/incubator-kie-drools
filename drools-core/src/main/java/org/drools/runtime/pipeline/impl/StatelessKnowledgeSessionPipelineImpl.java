package org.drools.runtime.pipeline.impl;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.dataloader.impl.EntryPointPipelineContext;
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
        StatelessKnowledgeSessionPipelineContextImpl context = new StatelessKnowledgeSessionPipelineContextImpl(ksession, Thread.currentThread().getContextClassLoader(), resultHandler );
        
        emit( object, context );        
    }

}
