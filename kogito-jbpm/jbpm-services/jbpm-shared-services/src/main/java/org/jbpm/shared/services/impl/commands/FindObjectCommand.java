package org.jbpm.shared.services.impl.commands;

import org.drools.core.command.impl.GenericCommand;
import org.jbpm.shared.services.impl.JpaPersistenceContext;
import org.kie.internal.command.Context;

public class FindObjectCommand<T> implements GenericCommand<T> {

	private static final long serialVersionUID = -4014807273522465028L;

	private Object identifer;
	private Class<T> clazz;
	
	public FindObjectCommand(Object identifer, Class<T> clazz) {
		this.identifer = identifer;
		this.clazz = clazz;
	}
	
	@Override
	public T execute(Context context) {
		JpaPersistenceContext ctx = (JpaPersistenceContext) context;
		return ctx.find(clazz, identifer);
	}


}
