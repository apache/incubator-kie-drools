/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.shared.services.impl.commands;

import java.util.Map;

import org.jbpm.shared.services.impl.JpaPersistenceContext;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;

public class QueryStringCommand<T> implements ExecutableCommand<T> {

	private static final long serialVersionUID = -4014807273522465028L;

	private Class<T> resultType;
	private String queryName;
	private Map<String, Object> params;
	
	public QueryStringCommand(String queryName) {
		this.resultType = (Class<T>) Object.class.getClass();
		this.queryName = queryName;
	}
	
	public QueryStringCommand(String queryName, Map<String, Object> params) {
		this.resultType = (Class<T>) Object.class.getClass();
		this.queryName = queryName;
		this.params = params;
	}
	
	@Override
	public T execute(Context context) {
		JpaPersistenceContext ctx = (JpaPersistenceContext) context;
		if (params == null) {
			return ctx.queryStringInTransaction(queryName, resultType);
		} else {
			return ctx.queryStringWithParametersInTransaction(queryName, params, resultType);
		}
	}

}
