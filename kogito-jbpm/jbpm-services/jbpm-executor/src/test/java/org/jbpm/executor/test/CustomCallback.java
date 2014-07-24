package org.jbpm.executor.test;

import org.kie.internal.executor.api.CommandCallback;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;

public class CustomCallback implements CommandCallback {
    
	@Override
	public void onCommandDone(CommandContext ctx, ExecutionResults results) {
		results.setData("custom", "custom callback invoked");
	}

	@Override
	public void onCommandError(CommandContext ctx, Throwable exception) {
		
	}

}
