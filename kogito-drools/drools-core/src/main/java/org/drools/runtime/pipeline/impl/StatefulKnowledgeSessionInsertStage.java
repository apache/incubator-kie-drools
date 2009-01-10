package org.drools.runtime.pipeline.impl;

import org.drools.common.InternalFactHandle;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Receiver;
import org.drools.runtime.rule.FactHandle;

public class StatefulKnowledgeSessionInsertStage extends BaseEmitter
    implements
    Receiver {

    public void receive(Object object,
                        PipelineContext context) {
        StatefulKnowledgeSessionPipelineContextImpl kContext = (StatefulKnowledgeSessionPipelineContextImpl) context;
        FactHandle handle = kContext.getEntryPoint().insert( object );

        kContext.getHandles().put( handle,
                                   ((InternalFactHandle) handle).getObject() );

        emit( object,
              kContext );
    }

}
