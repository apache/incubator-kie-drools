package org.drools.runtime.pipeline.impl;

import org.drools.common.InternalFactHandle;
import org.drools.runtime.dataloader.impl.EntryPointPipelineContext;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Receiver;
import org.drools.runtime.rule.FactHandle;

public class StatefulKnowledgeSessionInsertStage extends BaseEmitter implements Receiver {

    public void receive(Object object,
                        PipelineContext context) {
        EntryPointPipelineContext epContext = ( EntryPointPipelineContext ) context;
        FactHandle handle = epContext.getEntryPoint().insert( object );
        
        epContext.getHandles().put( handle, (( InternalFactHandle ) handle ).getObject() );
        
        emit( object, epContext );
    }

}
