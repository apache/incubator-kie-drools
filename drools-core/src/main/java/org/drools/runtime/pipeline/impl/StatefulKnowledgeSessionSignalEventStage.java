package org.drools.runtime.pipeline.impl;

import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Receiver;
import org.drools.runtime.pipeline.StatefulKnowledgeSessionPipelineContext;

public class StatefulKnowledgeSessionSignalEventStage extends BaseEmitter
    implements
    Receiver {
    private long   id;
    private String eventType;

    public StatefulKnowledgeSessionSignalEventStage(String eventType,
                                                    long id) {
        this.eventType = eventType;
        this.id = id;
    }

    public void receive(Object object,
                        PipelineContext context) {
        StatefulKnowledgeSessionPipelineContext kContext = (StatefulKnowledgeSessionPipelineContext) context;
        kContext.getStatefulKnowledgeSession().getProcessInstance( this.id ).signalEvent( this.eventType,
                                                                                          object );

        emit( object,
              kContext );
    }

}
