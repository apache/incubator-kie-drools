package org.drools.runtime.pipeline;

import org.drools.runtime.StatefulKnowledgeSession;

public interface StatefulKnowledgeSessionPipelineContext
    extends
    PipelineContext {
    StatefulKnowledgeSession getStatefulKnowledgeSession();
}
