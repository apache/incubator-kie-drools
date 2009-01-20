package org.drools.runtime.pipeline;

import org.drools.runtime.Parameters;
import org.drools.runtime.StatelessKnowledgeSession;

public interface StatelessKnowledgeSessionPipelineContext
    extends
    PipelineContext {

    StatelessKnowledgeSession getStatelessKnowledgeSession();

    void setObject(Object object);

    Object getObject();

    void setIterable(Iterable iterable);

    Iterable getIterable();

    Parameters getParameters();

}