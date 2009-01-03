/**
 * 
 */
package org.drools.runtime.dataloader.impl;

import org.drools.runtime.pipeline.Adapter;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.impl.BaseStage;

public class StatelessKnowledgeSessionReceiverAdapter extends BaseStage
    implements
    Adapter {
    public void receive(Object object,
                       PipelineContext context) {
        StatelessKnowledgeSessionPipelineContext pContext = (StatelessKnowledgeSessionPipelineContext) context;
        pContext.addResult( object );
    }

}