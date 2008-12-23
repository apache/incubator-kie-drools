package org.drools.runtime.dataloader.impl;

import java.util.Collection;
import java.util.Map;

import org.drools.definition.pipeline.Emitter;
import org.drools.definition.pipeline.Receiver;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.dataloader.StatefulKnowledgeSessionDataLoader;

public class StatefulKnowledgeSessionDataLoaderImpl
    implements
    StatefulKnowledgeSessionDataLoader {
    private StatefulKnowledgeSession ksession;

    private Receiver                 pipeline;

    public StatefulKnowledgeSessionDataLoaderImpl(StatefulKnowledgeSession ksession,
                                                  Receiver pipeline) {
        this.ksession = ksession;
        this.pipeline = pipeline;
    }

    /* (non-Javadoc)
     * @see org.drools.dataloaders.smooks.StatefulKnowledgeSessionDataLoader#insert(java.lang.Object)
     */
    public Map insert(Object object) {
        StatefulKnowledgeSessionPipelineContext context = new StatefulKnowledgeSessionPipelineContext( this.ksession );
        this.pipeline.signal( object,
                              context );

        return context.getHandles();
    }

}
