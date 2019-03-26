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

package org.jbpm.executor.ejb.impl.jpa;

import org.jbpm.executor.impl.jpa.ExecutorQueryServiceImpl;
import org.kie.api.executor.ExecutorQueryService;
import org.kie.api.runtime.CommandExecutor;

import javax.ejb.EJB;
import javax.ejb.Stateless;


@Stateless
public class ExecutorQueryServiceEJBImpl extends ExecutorQueryServiceImpl implements ExecutorQueryService {

	public ExecutorQueryServiceEJBImpl() {
		super(true);
	}

	@EJB(beanInterface=TransactionalCommandServiceExecutorEJBImpl.class)
	@Override
	public void setCommandService(CommandExecutor commandService ) {
		super.setCommandService(commandService);
	}

}
