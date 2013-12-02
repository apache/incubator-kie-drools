package org.jbpm.shared.services.impl.commands;

import java.util.Map;

import org.drools.core.command.impl.GenericCommand;
import org.jbpm.shared.services.impl.JpaPersistenceContext;
import org.kie.internal.command.Context;

public class QueryLockNameCommand<T> implements GenericCommand<T> {

	private static final long serialVersionUID = -4014807273522165028L;

	private Class<T> resultType;
	private String queryName;
	private Map<String, Object> params;
	private boolean singleResult;
	
	public QueryLockNameCommand(String queryName, Map<String, Object> params, boolean singleResult) {
		this.resultType = (Class<T>) Object.class.getClass();
		this.queryName = queryName;
		this.params = params;
	}
	
	@Override
	public T execute(Context context) {
		JpaPersistenceContext ctx = (JpaPersistenceContext) context;
		return ctx.queryAndLockWithParametersInTransaction(queryName, params, singleResult, resultType);		
	}

}
