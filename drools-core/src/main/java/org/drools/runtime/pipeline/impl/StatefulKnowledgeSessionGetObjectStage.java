package org.drools.runtime.pipeline.impl;

import java.util.Map;
import java.util.Map.Entry;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Receiver;
import org.drools.runtime.pipeline.StatefulKnowledgeSessionPipelineContext;
import org.drools.runtime.rule.FactHandle;

public class StatefulKnowledgeSessionGetObjectStage extends BaseEmitter
    implements
    Receiver {

    public StatefulKnowledgeSessionGetObjectStage() {
    }

    public void receive(Object object,
                        PipelineContext context) {        
        StatefulKnowledgeSessionPipelineContext kContext = (StatefulKnowledgeSessionPipelineContext) context;
        StatefulKnowledgeSession ksession = kContext.getStatefulKnowledgeSession();
        Object result = ksession.getObject( (FactHandle) object );        
        
        context.setResult( result );
        
        emit( result,
              kContext );
    }

}
