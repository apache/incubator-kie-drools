package org.drools.runtime.pipeline.impl;

import java.util.Map;
import java.util.Map.Entry;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Receiver;
import org.drools.runtime.pipeline.StatefulKnowledgeSessionPipelineContext;

public class StatefulKnowledgeSessionSetGlobalStage extends BaseEmitter
    implements
    Receiver {

    public StatefulKnowledgeSessionSetGlobalStage() {
    }

    public void receive(Object object,
                        PipelineContext context) {
        StatefulKnowledgeSessionPipelineContext kContext = (StatefulKnowledgeSessionPipelineContext) context;
        StatefulKnowledgeSession ksession = kContext.getStatefulKnowledgeSession();
        Map<String, Object> vars = ( Map<String, Object> ) object;        
        for ( Entry<String, Object> entry : vars.entrySet()) {
            ksession.setGlobal( entry.getKey(), entry.getValue() );
        }
        
        emit( object,
              kContext );
    }

}
