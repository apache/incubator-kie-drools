package org.jbpm.kie.services.impl.bpmn2.builder.dialect.java;

import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.builder.ProcessBuildContext;
import org.jbpm.process.builder.ProcessClassBuilder;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.jbpm.process.builder.dialect.java.JavaProcessDialect;

public class DataServiceJavaProcessDialect extends JavaProcessDialect {

    private final static ActionBuilder actionBuilder = new ThreadLocalJavaActionBuilder();
    private final static ReturnValueEvaluatorBuilder returnValueEvaluatorBuilder = new ThreadLocalJavaReturnValueEvaluatorBuilder();
   
    @Override
    public void addProcess( ProcessBuildContext context ) {
        super.addProcess(context);
//        throw new UnsupportedOperationException("THIS NEEDS TO BE FINISHED!");
        // TODO: check if information about the proces should be extracted here and added to a
        // ProcessDescRepoHelper instance -- specifically, we're looking for references/imports
        // of other resources such as java classes and drools rules (DRL, etc.).
    }

    @Override
    public ActionBuilder getActionBuilder() {
        return actionBuilder;
    }

    @Override
    public ProcessClassBuilder getProcessClassBuilder() {
        return super.getProcessClassBuilder();
//        throw new UnsupportedOperationException("THIS NEEDS TO BE FINISHED!");
        // TODO: check if information about the proces should be extracted here and added to a
        // ProcessDescRepoHelper instance -- specifically, we're looking for references/imports
        // of other resources such as java classes and drools rules (DRL, etc.).
    }

    @Override
    public ReturnValueEvaluatorBuilder getReturnValueEvaluatorBuilder() {
        return returnValueEvaluatorBuilder;
    }

}
