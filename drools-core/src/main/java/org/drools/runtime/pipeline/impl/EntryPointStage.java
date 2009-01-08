/**
 * 
 */
package org.drools.runtime.pipeline.impl;

import org.drools.runtime.dataloader.impl.EntryPointPipelineContext;
import org.drools.runtime.pipeline.Adapter;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Receiver;
import org.drools.runtime.pipeline.impl.BaseStage;
import org.drools.runtime.rule.FactHandle;

public class EntryPointStage extends BaseEmitter
    implements
    Receiver {
    public void receive(Object object,
                       PipelineContext context) {
        EntryPointPipelineContext pContext = (EntryPointPipelineContext) context;

        FactHandle handle = ((pContext).getEntryPoint()).insert( object );
        ((pContext).getHandles()).put( handle,
                                       object );
    }

}