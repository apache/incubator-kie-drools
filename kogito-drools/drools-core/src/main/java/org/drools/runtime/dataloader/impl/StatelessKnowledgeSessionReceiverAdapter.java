/**
 * 
 */
package org.drools.runtime.dataloader.impl;

import org.drools.definition.pipeline.Adapter;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.impl.BaseStage;

public class StatelessKnowledgeSessionReceiverAdapter extends BaseStage
    implements
    Adapter {
    public void signal(Object object,
                       PipelineContext context) {
        StatelessKnowledgeSessionPipelineContext pContext = (StatelessKnowledgeSessionPipelineContext) context;
        pContext.addResult( object );
    }

}