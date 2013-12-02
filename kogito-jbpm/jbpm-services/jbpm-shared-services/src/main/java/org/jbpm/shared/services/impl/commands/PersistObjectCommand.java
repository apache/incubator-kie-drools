package org.jbpm.shared.services.impl.commands;

import org.drools.core.command.impl.GenericCommand;
import org.jbpm.shared.services.impl.JpaPersistenceContext;
import org.kie.internal.command.Context;

public class PersistObjectCommand implements GenericCommand<Void> {

	private static final long serialVersionUID = -4014807273522465028L;

	private Object[] objectsToPersist;

	public PersistObjectCommand(Object ...objects) {
		this.objectsToPersist = objects;
	}
	
	@Override
	public Void execute(Context context) {
		JpaPersistenceContext ctx = (JpaPersistenceContext) context;
		if (objectsToPersist != null) {
			for (Object object : objectsToPersist) {
				ctx.persist(object);
			}
		}
		return null;
	}

}
