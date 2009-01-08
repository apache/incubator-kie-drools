package org.drools.runtime.pipeline.impl;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.dataloader.impl.EntryPointPipelineContext;
import org.drools.runtime.pipeline.Receiver;
import org.drools.runtime.pipeline.ResultHandler;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public class StatefulKnowledgeSessionInsertPipelineImpl extends BaseEmitter
    implements
    Pipeline {
    private WorkingMemoryEntryPoint entryPoint;

    public StatefulKnowledgeSessionInsertPipelineImpl(StatefulKnowledgeSession ksession,
                                                      String entryPointName) {
        this.entryPoint = ksession.getWorkingMemoryEntryPoint( entryPointName );
    }

    public StatefulKnowledgeSessionInsertPipelineImpl(StatefulKnowledgeSession ksession) {
        this.entryPoint = ksession;
    }

    public void insert(Object object,
                       ResultHandler resultHandler) {
        EntryPointPipelineContext context = new EntryPointPipelineContext( this.entryPoint,
                                                                           resultHandler );
        emit( object, context );        
    }

}
