package org.drools.runtime.pipeline.impl;

import org.drools.impl.ParametersImpl;
import org.drools.runtime.Parameters;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.pipeline.ResultHandler;
import org.drools.runtime.pipeline.StatelessKnowledgeSessionPipelineContext;

public class StatelessKnowledgeSessionPipelineContextImpl extends BasePipelineContext
    implements
    StatelessKnowledgeSessionPipelineContext {

    private StatelessKnowledgeSession ksession;
    
    private Iterable iterable;
    
    private Object object;
    
    private Parameters parameters;

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
        this.parameters = new ParametersImpl();
    }

    public StatelessKnowledgeSession getStatelessKnowledgeSession() {
        return this.ksession;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public Iterable getIterable() {
        return iterable;
    }

    public void setIterable(Iterable iterable) {
        this.iterable = iterable;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
            
}
