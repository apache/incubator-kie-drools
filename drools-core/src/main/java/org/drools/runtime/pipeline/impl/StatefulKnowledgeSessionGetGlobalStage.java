package org.drools.runtime.pipeline.impl;

import java.util.Map;
import java.util.Map.Entry;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.pipeline.KnowledgeRuntimeCommand;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Receiver;
import org.drools.runtime.pipeline.StatefulKnowledgeSessionPipelineContext;

public class StatefulKnowledgeSessionGetGlobalStage extends BaseEmitter
    implements
    KnowledgeRuntimeCommand {

    public StatefulKnowledgeSessionGetGlobalStage() {
    }

    public void receive(Object object,
                        PipelineContext context) {        
        StatefulKnowledgeSessionPipelineContext kContext = (StatefulKnowledgeSessionPipelineContext) context;
        StatefulKnowledgeSession ksession = kContext.getStatefulKnowledgeSession();
        Object result = ksession.getGlobal( (String) object );
        
        emit( result,
              kContext );
    }

}
