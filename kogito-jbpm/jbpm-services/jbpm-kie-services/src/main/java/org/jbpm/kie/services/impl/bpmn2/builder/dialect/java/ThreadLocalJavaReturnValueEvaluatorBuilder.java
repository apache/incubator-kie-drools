package org.jbpm.kie.services.impl.bpmn2.builder.dialect.java;

import org.drools.compiler.compiler.ReturnValueDescr;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.jbpm.kie.services.impl.bpmn2.builder.DataServiceExpressionBuilder;
import org.jbpm.kie.services.impl.bpmn2.builder.GenericDefaultThreadLocal;
import org.jbpm.kie.services.impl.bpmn2.builder.dialect.ThreadLocalAbstractBuilderFacade;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.jbpm.process.builder.dialect.java.JavaReturnValueEvaluatorBuilder;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;

/**
 * @see {@link ThreadLocalAbstractBuilderFacade}
 */
class ThreadLocalJavaReturnValueEvaluatorBuilder extends ThreadLocalAbstractBuilderFacade<ReturnValueEvaluatorBuilder> implements ReturnValueEvaluatorBuilder {

    private final static ReturnValueEvaluatorBuilder defaultBuilderInstance = new JavaReturnValueEvaluatorBuilder();
    private final static ThreadLocal<ReturnValueEvaluatorBuilder> threadLocalBuilder 
        = new GenericDefaultThreadLocal<ReturnValueEvaluatorBuilder>(defaultBuilderInstance);
    private final static DataServiceExpressionBuilder dataServiceBuilderInstance 
        = new DataServiceJavaActionBuilder();

    @Override
    public ReturnValueEvaluatorBuilder getThreadLocalBuilder() {
        return threadLocalBuilder.get();
    }

    @Override
    public void setThreadLocalBuilder( ReturnValueEvaluatorBuilder actionBuilder ) {
       threadLocalBuilder.set(actionBuilder); 
    }

    @Override
    public DataServiceExpressionBuilder getDataServiceBuilderInstance() {
        return dataServiceBuilderInstance;
    }

    @Override
    public ReturnValueEvaluatorBuilder getDefaultBuilderInstance() {
        return defaultBuilderInstance;
    }

    @Override
    public void build( PackageBuildContext context, ReturnValueConstraintEvaluator returnValueConstraintEvaluator,
            ReturnValueDescr returnValueDescr, ContextResolver contextResolver ) {
        getThreadLocalBuilder().build(context, returnValueConstraintEvaluator, returnValueDescr, contextResolver);
    }
  
}