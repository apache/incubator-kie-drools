package org.jbpm.executor.ejb.impl.jpa;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.drools.core.command.CommandService;
import org.jbpm.executor.impl.jpa.JPAExecutorStoreService;
import org.kie.internal.executor.api.ExecutorStoreService;

@Stateless
public class JPAExecutorStoreServiceEJBImpl extends JPAExecutorStoreService implements ExecutorStoreService {

	public JPAExecutorStoreServiceEJBImpl() {
		super(true);
	}

	@EJB(beanInterface=TransactionalCommandServiceExecutorEJBImpl.class)
	@Override
	public void setCommandService(CommandService commandService) {
		super.setCommandService(commandService);
	}

	@PersistenceUnit(unitName="org.jbpm.domain")
	@Override
	public void setEmf(EntityManagerFactory emf) {
		super.setEmf(emf);
	}

}
