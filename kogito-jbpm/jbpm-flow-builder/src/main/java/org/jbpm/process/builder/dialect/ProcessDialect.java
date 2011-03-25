package org.jbpm.process.builder.dialect;

import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.builder.AssignmentBuilder;
import org.jbpm.process.builder.ProcessBuildContext;
import org.jbpm.process.builder.ProcessClassBuilder;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;

public interface ProcessDialect {

    ActionBuilder getActionBuilder();

    ReturnValueEvaluatorBuilder getReturnValueEvaluatorBuilder();

    ProcessClassBuilder getProcessClassBuilder();
    
    AssignmentBuilder getAssignmentBuilder();

    void addProcess(final ProcessBuildContext context);

}
