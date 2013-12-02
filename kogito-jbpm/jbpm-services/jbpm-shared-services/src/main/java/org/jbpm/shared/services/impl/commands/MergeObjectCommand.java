package org.jbpm.shared.services.impl.commands;

import org.drools.core.command.impl.GenericCommand;
import org.jbpm.shared.services.impl.JpaPersistenceContext;
import org.kie.internal.command.Context;

public class MergeObjectCommand implements GenericCommand<Void> {

	private static final long serialVersionUID = -4014807273522465028L;

	private Object[] objectsToMerge;

	public MergeObjectCommand(Object ...objects) {
		this.objectsToMerge = objects;
	}
	
	@Override
	public Void execute(Context context) {
		JpaPersistenceContext ctx = (JpaPersistenceContext) context;
		if (objectsToMerge != null) {
			for (Object object : objectsToMerge) {
				ctx.merge(object);
			}
		}
		return null;
	}

}
