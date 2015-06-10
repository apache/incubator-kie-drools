package org.jbpm.process.builder.dialect.javascript;

import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.builder.AssignmentBuilder;
import org.jbpm.process.builder.ProcessBuildContext;
import org.jbpm.process.builder.ProcessClassBuilder;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.jbpm.process.builder.dialect.ProcessDialect;

public class JavaScriptProcessDialect implements ProcessDialect {

	private static final ActionBuilder actionBuilder = new JavaScriptActionBuilder();
	private static final ReturnValueEvaluatorBuilder returnValueEvaluatorBuilder = new JavaScriptReturnValueEvaluatorBuilder();
	
	public void addProcess(final ProcessBuildContext context) {
        // @TODO setup line mappings
	}

	public ActionBuilder getActionBuilder() {
		return actionBuilder;
	}

	public ProcessClassBuilder getProcessClassBuilder() {
        throw new UnsupportedOperationException(
            "JavaScriptProcessDialect.getProcessClassBuilder is not supported" );
	}

	public ReturnValueEvaluatorBuilder getReturnValueEvaluatorBuilder() {
	    return returnValueEvaluatorBuilder;
	}

	public AssignmentBuilder getAssignmentBuilder() {
		throw new UnsupportedOperationException(
			"JavaScript assignments not supported");
	}

}
