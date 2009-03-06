package org.drools.runtime.pipeline;

import org.drools.runtime.StatelessKnowledgeSession;

public interface StatelessKnowledgeSessionPipelineContext
    extends
    PipelineContext {

    StatelessKnowledgeSession getStatelessKnowledgeSession();


}