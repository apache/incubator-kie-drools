/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.executor.ejb.impl.jpa;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.drools.core.command.CommandService;
import org.jbpm.executor.impl.jpa.ExecutorRequestAdminServiceImpl;

@Stateless
public class ExecutorRequestAdminServiceEJBImpl extends
		ExecutorRequestAdminServiceImpl {

	@EJB(beanInterface=TransactionalCommandServiceExecutorEJBImpl.class)
	@Override
	public void setCommandService(CommandService commandService) {
		super.setCommandService(commandService);
	}
}
