package org.drools.runtime.pipeline.impl;

import java.util.Map;
import java.util.Map.Entry;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.pipeline.KnowledgeRuntimeCommand;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Receiver;
import org.drools.runtime.pipeline.StatefulKnowledgeSessionPipelineContext;

public class StatefulKnowledgeSessionSetGlobalStage extends BaseEmitter
    implements
    KnowledgeRuntimeCommand {
    private String key;
    
    public StatefulKnowledgeSessionSetGlobalStage() {
        
    }
    
    public StatefulKnowledgeSessionSetGlobalStage(String key) {
        this.key = key;
    }

    public void receive(Object object,
                        PipelineContext context) {
        StatefulKnowledgeSessionPipelineContext kContext = (StatefulKnowledgeSessionPipelineContext) context;
        StatefulKnowledgeSession ksession = kContext.getStatefulKnowledgeSession();
        if ( key == null ) {
            if ( !(object instanceof Map) ) {
                throw new IllegalArgumentException( "SetGlobalStage must either declare a key or be an instanceof a Map");
            } else {
                Map<String, Object> vars = ( Map<String, Object> ) object;        
                for ( Entry<String, Object> entry : vars.entrySet()) {
                    ksession.setGlobal( entry.getKey(), entry.getValue() );
                }
            }
        } else {
            ksession.setGlobal( this.key, object );
        }

        
        emit( object,
              kContext );        
    }

}
