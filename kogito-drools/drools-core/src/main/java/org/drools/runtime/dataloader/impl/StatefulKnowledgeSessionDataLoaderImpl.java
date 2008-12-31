package org.drools.runtime.dataloader.impl;

import java.util.Map;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.dataloader.StatefulKnowledgeSessionDataLoader;
import org.drools.runtime.pipeline.Receiver;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public class StatefulKnowledgeSessionDataLoaderImpl
    implements
    StatefulKnowledgeSessionDataLoader {
    private StatefulKnowledgeSession ksession;
    private String                   entryPointName;
    private WorkingMemoryEntryPoint  entryPoint;

    private Receiver                 pipeline;

    public StatefulKnowledgeSessionDataLoaderImpl(StatefulKnowledgeSession ksession,
                                                  String entryPointName,
                                                  Receiver pipeline) {
        this.ksession = ksession;
        this.entryPointName = entryPointName;
        this.entryPoint = ksession.getWorkingMemoryEntryPoint( this.entryPointName );
        this.pipeline = pipeline;
    }

    public StatefulKnowledgeSessionDataLoaderImpl(StatefulKnowledgeSession ksession,
                                                  Receiver pipeline) {
        this.ksession = ksession;
        this.entryPoint = ksession;
        this.pipeline = pipeline;
    }

    /* (non-Javadoc)
     * @see org.drools.dataloaders.smooks.StatefulKnowledgeSessionDataLoader#insert(java.lang.Object)
     */
    public Map insert(Object object) {
        EntryPointPipelineContext context = new EntryPointPipelineContext( this.entryPoint );
        this.pipeline.signal( object,
                              context );

        return context.getHandles();
    }

}
