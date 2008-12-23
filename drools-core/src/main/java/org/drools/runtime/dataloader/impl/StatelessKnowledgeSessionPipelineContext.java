/**
 * 
 */
package org.drools.runtime.dataloader.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.common.InternalRuleBase;
import org.drools.impl.StatelessKnowledgeSessionImpl;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.impl.BasePipelineContext;

public class StatelessKnowledgeSessionPipelineContext extends BasePipelineContext
    implements
    PipelineContext {
    private List results = new ArrayList();

    public StatelessKnowledgeSessionPipelineContext(StatelessKnowledgeSession ksession) {
        super( ((InternalRuleBase) ((StatelessKnowledgeSessionImpl) ksession).getRuleBase()).getRootClassLoader() );
    }

    public List getResult() {
        return this.results;
    }

    public void addResult(Object result) {
        this.results.add( result );
    }

}