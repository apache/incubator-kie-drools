package org.drools.runtime.pipeline.impl;

import org.drools.impl.ParametersImpl;
import org.drools.runtime.StatelessKnowledgeSessionResults;
import org.drools.runtime.pipeline.KnowledgeRuntimeCommand;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Receiver;
import org.drools.runtime.pipeline.StatelessKnowledgeSessionPipelineContext;

public class StatelessKnowledgeSessionExecuteStage extends BaseEmitter
    implements
    KnowledgeRuntimeCommand {

    public void receive(Object object,
                        PipelineContext context) {
        StatelessKnowledgeSessionPipelineContext kContext = (StatelessKnowledgeSessionPipelineContext) context;

        StatelessKnowledgeSessionResults results = null;
        if ( kContext.getObject() != null ) {
            if ( kContext.getParameters() == null || ((ParametersImpl) kContext.getParameters()).isEmpty() ) {
                kContext.getStatelessKnowledgeSession().executeObject( kContext.getObject() );
            } else {
                results = kContext.getStatelessKnowledgeSession().executeObjectWithParameters( kContext.getObject(),
                                                                                               kContext.getParameters() );
            }
        } else if ( kContext.getIterable() != null ) {
            if ( kContext.getParameters() == null || ((ParametersImpl) kContext.getParameters()).isEmpty()  ) {
                kContext.getStatelessKnowledgeSession().executeIterable( kContext.getIterable() );
            } else {
                results = kContext.getStatelessKnowledgeSession().executeIterableWithParameters( kContext.getIterable(),
                                                                                                 kContext.getParameters() );
            }
        } else {
            if ( kContext.getParameters() == null || ((ParametersImpl) kContext.getParameters()).isEmpty() ) {
                kContext.getStatelessKnowledgeSession().executeObject( object );
            } else {
                results = kContext.getStatelessKnowledgeSession().executeObjectWithParameters( object,
                                                                                               kContext.getParameters() );
            }
        }
        
        context.setResult( results );
        
        emit( object, context );
 
    }

}
