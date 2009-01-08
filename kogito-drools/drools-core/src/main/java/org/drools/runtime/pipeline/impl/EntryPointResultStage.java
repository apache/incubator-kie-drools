/**
 * 
 */
package org.drools.runtime.pipeline.impl;

import org.drools.runtime.dataloader.impl.EntryPointPipelineContext;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Receiver;

public class EntryPointResultStage extends BaseEmitter
    implements
    Receiver {
    public void receive(Object object,
                       PipelineContext context) {
        
        EntryPointPipelineContext pContext = (EntryPointPipelineContext) context;
        pContext.getResultHandler().handleResult( pContext.getHandles() );
    }

}