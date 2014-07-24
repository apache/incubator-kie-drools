package org.jbpm.executor.test;

import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;

public class AddAnotherCallbackCommand implements Command {

	@Override
	public ExecutionResults execute(CommandContext ctx) throws Exception {
		String callbacks = (String) ctx.getData("callbacks");
		ctx.setData("callbacks", callbacks + ",org.jbpm.executor.test.CustomCallback");
		return new ExecutionResults();
	}

}
