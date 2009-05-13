package org.drools.runtime.pipeline.impl;

import org.drools.common.InternalRuleBase;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.pipeline.Pipeline;
import org.drools.runtime.pipeline.ResultHandler;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public class StatefulKnowledgeSessionPipelineImpl extends BaseEmitter
    implements
    Pipeline {
    private StatefulKnowledgeSession ksession;
    private WorkingMemoryEntryPoint  entryPoint;

    public StatefulKnowledgeSessionPipelineImpl(StatefulKnowledgeSession ksession) {
        this.ksession = ksession;
        this.entryPoint = ksession;
    }

    public StatefulKnowledgeSessionPipelineImpl(StatefulKnowledgeSession ksession,
                                                String entryPointName) {
        this.ksession = ksession;
        this.entryPoint = ksession.getWorkingMemoryEntryPoint( entryPointName );
    }

    public synchronized void insert(Object object,
                                    ResultHandler resultHandler) {
        ClassLoader cl = ((InternalRuleBase) (  
                                                (
                                                    (KnowledgeBaseImpl)
                                                        ((StatefulKnowledgeSession) this.ksession).getKnowledgeBase()
                                                ).getRuleBase())).getRootClassLoader();

        StatefulKnowledgeSessionPipelineContextImpl context = new StatefulKnowledgeSessionPipelineContextImpl( this.ksession,
                                                                                                               this.entryPoint,
                                                                                                               resultHandler,
                                                                                                               cl );
        emit( object,
              context );
    }

}
