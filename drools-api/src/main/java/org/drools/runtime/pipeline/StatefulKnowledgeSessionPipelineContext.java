package org.drools.runtime.pipeline;

import org.drools.runtime.StatefulKnowledgeSession;


/**
 * 
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 *
 */
public interface StatefulKnowledgeSessionPipelineContext
    extends
    PipelineContext {
    StatefulKnowledgeSession getStatefulKnowledgeSession();
}
