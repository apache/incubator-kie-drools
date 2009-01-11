package org.drools.runtime.pipeline.impl;

import java.util.Map;

import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Receiver;
import org.drools.runtime.pipeline.StatefulKnowledgeSessionPipelineContext;

public class StatefulKnowledgeSessionStartProcessStage extends BaseEmitter
    implements
    Receiver {
    private String id;

    public StatefulKnowledgeSessionStartProcessStage(String id) {
        this.id = id;
    }

    public void receive(Object object,
                        PipelineContext context) {
        StatefulKnowledgeSessionPipelineContext kContext = (StatefulKnowledgeSessionPipelineContext) context;
        long instanceId = kContext.getStatefulKnowledgeSession().startProcess( id,
                                                             (Map<String, Object>) object ).getId();
        kContext.setResult( instanceId );
        
        emit( object,
              kContext );
    }

}
