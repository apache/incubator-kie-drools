package org.jbpm.kie.services.impl.bpmn2.builder.dialect.mvel;

import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.builder.ProcessBuildContext;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.jbpm.process.builder.dialect.mvel.MVELProcessDialect;

public class DataServiceMvelProcessDialect extends MVELProcessDialect {

    private final static ActionBuilder actionBuilder = new ThreadLocalMvelActionBuilder();
    private final static ReturnValueEvaluatorBuilder returnValueEvaluatorBuilder = new ThreadLocalMvelReturnValueBuilder();
   
    @Override
    public void addProcess( ProcessBuildContext context ) {
        throw new UnsupportedOperationException("THIS NEEDS TO BE FINISHED!");
        // TODO: check if information about the proces should be extracted here and added to a
        // ProcessDescRepoHelper instance -- specifically, we're looking for references/imports
        // of other resources such as java classes and drools rules (DRL, etc.)
    }

    @Override
    public ActionBuilder getActionBuilder() {
        return actionBuilder;
    }

    @Override
    public ReturnValueEvaluatorBuilder getReturnValueEvaluatorBuilder() {
        return returnValueEvaluatorBuilder;
    }

}
