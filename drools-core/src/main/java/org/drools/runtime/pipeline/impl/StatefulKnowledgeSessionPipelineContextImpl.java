package org.drools.runtime.pipeline.impl;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.pipeline.ResultHandler;
import org.drools.runtime.pipeline.StatefulKnowledgeSessionPipelineContext;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public class StatefulKnowledgeSessionPipelineContextImpl extends BasePipelineContext
    implements
    StatefulKnowledgeSessionPipelineContext {
    private StatefulKnowledgeSession ksession;
    private WorkingMemoryEntryPoint  entryPoint;

    public StatefulKnowledgeSessionPipelineContextImpl(StatefulKnowledgeSession ksession,
                                                       ClassLoader classLoader,
                                                       WorkingMemoryEntryPoint entryPoint,
                                                       ResultHandler resultHandler) {
        super( classLoader,
               resultHandler );
        this.ksession = ksession;
        this.entryPoint = entryPoint;
    }

    public StatefulKnowledgeSession getStatefulKnowledgeSession() {
        return this.ksession;
    }

    public WorkingMemoryEntryPoint getEntryPoint() {
        return entryPoint;
    }

}
