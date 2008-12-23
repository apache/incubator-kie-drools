/**
 * 
 */
package org.drools.runtime.dataloader.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.common.InternalRuleBase;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.impl.BasePipelineContext;
import org.drools.runtime.rule.FactHandle;

public class StatefulKnowledgeSessionPipelineContext extends BasePipelineContext
    implements
    PipelineContext {
    private Map                      handles;
    private StatefulKnowledgeSession ksession;

    public StatefulKnowledgeSessionPipelineContext(StatefulKnowledgeSession ksession) {
        super( ((InternalRuleBase)((KnowledgeBaseImpl)((StatefulKnowledgeSessionImpl)ksession).getKnowledgeBase()).getRuleBase()).getRootClassLoader() );
        this.handles = new HashMap<FactHandle, Object>();
        this.ksession = ksession;
    }

    public Map getHandles() {
        return handles;
    }

    public StatefulKnowledgeSession getKsession() {
        return ksession;
    }
}