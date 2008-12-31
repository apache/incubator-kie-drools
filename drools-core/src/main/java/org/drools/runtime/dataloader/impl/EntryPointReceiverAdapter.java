/**
 * 
 */
package org.drools.runtime.dataloader.impl;

import org.drools.runtime.pipeline.Adapter;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.impl.BaseStage;
import org.drools.runtime.rule.FactHandle;

public class EntryPointReceiverAdapter extends BaseStage
    implements
    Adapter {
    public void signal(Object object,
                       PipelineContext context) {
        EntryPointPipelineContext pContext = (EntryPointPipelineContext) context;

        FactHandle handle = ((pContext).getEntryPoint()).insert( object );
        ((pContext).getHandles()).put( handle,
                                       object );
    }

}