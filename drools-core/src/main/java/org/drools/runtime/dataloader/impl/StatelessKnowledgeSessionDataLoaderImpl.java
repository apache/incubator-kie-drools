package org.drools.runtime.dataloader.impl;

import org.drools.runtime.Parameters;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSessionResults;
import org.drools.runtime.dataloader.StatelessKnowledgeSessionDataLoader;
import org.drools.runtime.pipeline.Receiver;

public class StatelessKnowledgeSessionDataLoaderImpl implements StatelessKnowledgeSessionDataLoader {
    private StatelessKnowledgeSession ksession;

    private Receiver                  pipeline;

    public StatelessKnowledgeSessionDataLoaderImpl(StatelessKnowledgeSession ksession,
                                               Receiver pipeline) {
        this.ksession = ksession;
        this.pipeline = pipeline;
    }

    //    public Map insert(Object object) {
    //        StatefulKnowledgeSessionPipelineContext context = new StatefulKnowledgeSessionPipelineContext( this.ksession );
    //        this.pipeline.signal( object,
    //                              context );
    //
    //        return context.getHandles();
    //    }

    /* (non-Javadoc)
     * @see org.drools.dataloaders.smooks.StatelessKnowledeSessionDataLoader#executeObject(java.lang.Object)
     */
    public void executeObject(Object object) {
        StatelessKnowledgeSessionPipelineContext context = new StatelessKnowledgeSessionPipelineContext( this.ksession );
        this.pipeline.signal( object,
                              context );
        this.ksession.executeObject( context.getResult().get( 0 ) );
    }

    /* (non-Javadoc)
     * @see org.drools.dataloaders.smooks.StatelessKnowledeSessionDataLoader#executeIterable(java.lang.Object)
     */
    public void executeIterable(Object object) {
        StatelessKnowledgeSessionPipelineContext context = new StatelessKnowledgeSessionPipelineContext( this.ksession );
        this.pipeline.signal( object,
                              context );
        this.ksession.executeIterable( (Iterable) context.getResult() );
    }

    /* (non-Javadoc)
     * @see org.drools.dataloaders.smooks.StatelessKnowledeSessionDataLoader#executeObjectWithParameters(java.lang.Object, org.drools.runtime.Parameters)
     */
    public StatelessKnowledgeSessionResults executeObjectWithParameters(Object object,
                                                                 Parameters parameters) {
        StatelessKnowledgeSessionPipelineContext context = new StatelessKnowledgeSessionPipelineContext( this.ksession );
        this.pipeline.signal( object,
                              context );
        return this.ksession.executeObjectWithParameters( context.getResult().get( 0 ),
                                                          parameters );
    }

    /* (non-Javadoc)
     * @see org.drools.dataloaders.smooks.StatelessKnowledeSessionDataLoader#executeIterableWithParameters(java.lang.Object, org.drools.runtime.Parameters)
     */
    public StatelessKnowledgeSessionResults executeIterableWithParameters(Object object,
                                                                   Parameters parameters) {
        StatelessKnowledgeSessionPipelineContext context = new StatelessKnowledgeSessionPipelineContext( this.ksession );
        this.pipeline.signal( object,
                              context );
        return this.ksession.executeIterableWithParameters( (Iterable) context.getResult(),
                                                            parameters );
    }

}
