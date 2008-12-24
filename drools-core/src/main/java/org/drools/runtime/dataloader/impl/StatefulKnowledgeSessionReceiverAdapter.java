/**
 * 
 */
package org.drools.runtime.dataloader.impl;

import org.drools.runtime.pipeline.Adapter;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.impl.BaseStage;
import org.drools.runtime.rule.FactHandle;

public class StatefulKnowledgeSessionReceiverAdapter extends BaseStage
    implements
    Adapter {
    public void signal(Object object,
                       PipelineContext context) {
        StatefulKnowledgeSessionPipelineContext pContext = (StatefulKnowledgeSessionPipelineContext) context;

        FactHandle handle = ((pContext).getKsession()).insert( object );
        ((pContext).getHandles()).put( handle,
                                       object );
    }

}