package org.jbpm.executor.ejb.impl.jpa;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.drools.core.command.CommandService;
import org.jbpm.executor.impl.jpa.ExecutorQueryServiceImpl;
import org.kie.internal.executor.api.ExecutorQueryService;


@Stateless
public class ExecutorQueryServiceEJBImpl extends ExecutorQueryServiceImpl implements ExecutorQueryService {

	public ExecutorQueryServiceEJBImpl() {
		super(true);
	}

	@EJB(beanInterface=TransactionalCommandServiceExecutorEJBImpl.class)
	@Override
	public void setCommandService(CommandService commandService) {
		super.setCommandService(commandService);
	}

}
