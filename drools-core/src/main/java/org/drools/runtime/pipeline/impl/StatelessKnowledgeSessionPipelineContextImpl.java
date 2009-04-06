package org.drools.runtime.pipeline.impl;

import org.drools.runtime.CommandExecutor;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.pipeline.ResultHandler;
import org.drools.runtime.pipeline.StatelessKnowledgeSessionPipelineContext;

public class StatelessKnowledgeSessionPipelineContextImpl extends BasePipelineContext
    implements
    StatelessKnowledgeSessionPipelineContext {

    private StatelessKnowledgeSession ksession;

    public StatelessKnowledgeSessionPipelineContextImpl(StatelessKnowledgeSession ksession,
                                                        ClassLoader classLoader) {
        this( ksession,
              classLoader,
              null );
    }

    public StatelessKnowledgeSessionPipelineContextImpl(StatelessKnowledgeSession ksession,
                                                        ClassLoader classLoader,
                                                        ResultHandler resultHandler) {
        super( classLoader,
               resultHandler );
        this.ksession = ksession;
    }

    public StatelessKnowledgeSession getStatelessKnowledgeSession() {
        return this.ksession;
    }

    public CommandExecutor getCommandExecutor() {
        return this.ksession;
    }
            
}
