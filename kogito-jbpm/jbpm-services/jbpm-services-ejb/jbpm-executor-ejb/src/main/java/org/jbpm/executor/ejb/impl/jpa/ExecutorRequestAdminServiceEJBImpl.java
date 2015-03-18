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
