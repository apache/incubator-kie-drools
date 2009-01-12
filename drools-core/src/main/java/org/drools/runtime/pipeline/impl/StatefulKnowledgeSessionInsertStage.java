package org.drools.runtime.pipeline.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.common.InternalFactHandle;
import org.drools.runtime.pipeline.KnowledgeRuntimeCommand;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Receiver;
import org.drools.runtime.rule.FactHandle;

public class StatefulKnowledgeSessionInsertStage extends BaseEmitter
    implements
    KnowledgeRuntimeCommand {

    public void receive(Object object,
                        PipelineContext context) {
        StatefulKnowledgeSessionPipelineContextImpl kContext = (StatefulKnowledgeSessionPipelineContextImpl) context;
        FactHandle handle = kContext.getEntryPoint().insert( object );
        Map<FactHandle, Object> handles = (Map<FactHandle, Object>)kContext.getResult();
        if ( handles == null ) {
            handles = new HashMap<FactHandle, Object>();
            kContext.setResult( handles );
        }
        
        handles.put( handle,
                     ((InternalFactHandle) handle).getObject() );

        emit( object,
              kContext );
    }

}
