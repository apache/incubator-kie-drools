package org.jbpm.kie.services.impl.bpmn2.builder.dialect.java;

import org.drools.compiler.lang.descr.ActionDescr;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.jbpm.kie.services.impl.bpmn2.builder.DataServiceExpressionBuilder;
import org.jbpm.kie.services.impl.bpmn2.builder.GenericDefaultThreadLocal;
import org.jbpm.kie.services.impl.bpmn2.builder.dialect.ThreadLocalAbstractBuilderFacade;
import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.builder.dialect.java.JavaActionBuilder;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.workflow.core.DroolsAction;

/**
 * @see {@link ThreadLocalAbstractBuilderFacade}
 */
class ThreadLocalJavaActionBuilder extends ThreadLocalAbstractBuilderFacade<ActionBuilder> implements ActionBuilder {

    private final static ActionBuilder defaultBuilderInstance = new JavaActionBuilder();
    private final static ThreadLocal<ActionBuilder> threadLocalBuilder 
        = new GenericDefaultThreadLocal<ActionBuilder>(defaultBuilderInstance);
    private final static DataServiceExpressionBuilder dataServiceBuilderInstance 
        = new DataServiceJavaActionBuilder();

    @Override
    public void build( PackageBuildContext context, DroolsAction action, ActionDescr actionDescr, ContextResolver contextResolver ) {
        getThreadLocalBuilder().build(context, action, actionDescr, contextResolver);
    }

    @Override
    public ActionBuilder getThreadLocalBuilder() {
        return threadLocalBuilder.get();
    }

    @Override
    public void setThreadLocalBuilder( ActionBuilder actionBuilder ) {
       threadLocalBuilder.set(actionBuilder); 
    }

    @Override
    public DataServiceExpressionBuilder getDataServiceBuilderInstance() {
        return dataServiceBuilderInstance;
    }

    @Override
    public ActionBuilder getDefaultBuilderInstance() {
        return defaultBuilderInstance;
    }
  
}