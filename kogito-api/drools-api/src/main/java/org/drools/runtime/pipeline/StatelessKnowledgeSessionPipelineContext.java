package org.drools.runtime.pipeline;

import org.drools.runtime.StatelessKnowledgeSession;


/**
 * 
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 *
 */
public interface StatelessKnowledgeSessionPipelineContext
    extends
    PipelineContext {

    StatelessKnowledgeSession getStatelessKnowledgeSession();


}