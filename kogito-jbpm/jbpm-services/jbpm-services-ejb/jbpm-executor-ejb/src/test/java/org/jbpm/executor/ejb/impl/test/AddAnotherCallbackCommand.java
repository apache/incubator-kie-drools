package org.jbpm.executor.ejb.impl.test;

import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;

public class AddAnotherCallbackCommand implements Command {

	@Override
	public ExecutionResults execute(CommandContext ctx) throws Exception {
		String callbacks = (String) ctx.getData("callbacks");
		ctx.setData("callbacks", callbacks + ",org.jbpm.executor.ejb.impl.test.CustomCallback");
		return new ExecutionResults();
	}

}
