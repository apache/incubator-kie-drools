package org.jbpm.shared.services.impl.commands;

import org.drools.core.command.impl.GenericCommand;
import org.jbpm.shared.services.impl.JpaPersistenceContext;
import org.kie.internal.command.Context;

public class RemoveObjectCommand implements GenericCommand<Void> {

	private static final long serialVersionUID = -4014807273522465028L;

	private Object[] objectsToRemove;

	public RemoveObjectCommand(Object ...objects) {
		this.objectsToRemove = objects;
	}
	
	@Override
	public Void execute(Context context) {
		JpaPersistenceContext ctx = (JpaPersistenceContext) context;
		if (objectsToRemove != null) {
			for (Object object : objectsToRemove) {
				Object toremove = ctx.merge(object);
				ctx.remove(toremove);
			}
		}
		return null;
	}

}
