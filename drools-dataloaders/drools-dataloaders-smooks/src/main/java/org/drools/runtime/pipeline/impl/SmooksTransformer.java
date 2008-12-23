package org.drools.runtime.pipeline.impl;

import javax.xml.transform.Source;

import org.drools.definition.pipeline.SmooksPipelineProvider;
import org.drools.definition.pipeline.Transformer;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.impl.BaseEmitter;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaResult;

public class SmooksTransformer extends BaseEmitter
    implements
    Transformer {
    private Smooks                    smooks;
    private DroolsSmooksConfiguration configuration;

    public SmooksTransformer(Smooks smooks,
                             DroolsSmooksConfiguration configuration) {
        this.smooks = smooks;
        this.configuration = configuration;

    }

    public void signal(Object object,
                       PipelineContext context) {
        this.smooks.setClassLoader( context.getClassLoader() );
        Object result = null;
        try {
            JavaResult javaResult = new JavaResult();
            ExecutionContext executionContext = this.smooks.createExecutionContext();

            this.smooks.filter( (Source) object,
                                javaResult,
                                executionContext );

            result = javaResult.getBean( this.configuration.getRootId() );
        } catch ( Exception e ) {
            handleException( this,
                             object,
                             e );
        }
        emit( result,
              context );
    }
    
    public static class SmooksPipelineProviderImpl implements SmooksPipelineProvider {
        public Transformer newSmooksTransformer(Smooks smooks,
                                                String rootId) {
            DroolsSmooksConfiguration conf = new DroolsSmooksConfiguration( rootId );
            return new SmooksTransformer( smooks,
                                          conf );
        }
    }

}
