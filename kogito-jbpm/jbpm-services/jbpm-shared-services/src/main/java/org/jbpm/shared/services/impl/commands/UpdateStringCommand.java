package org.jbpm.shared.services.impl.commands;

import org.drools.core.command.impl.GenericCommand;
import org.jbpm.shared.services.impl.JpaPersistenceContext;
import org.kie.internal.command.Context;

public class UpdateStringCommand implements GenericCommand<Integer> {

	private static final long serialVersionUID = -4014807273522465028L;

	private String updateString;

	public UpdateStringCommand(String updateString) {
		this.updateString = updateString;
	}
	
	@Override
	public Integer execute(Context context) {
		JpaPersistenceContext ctx = (JpaPersistenceContext) context;
		return ctx.executeUpdateString(updateString);		
	}

}
