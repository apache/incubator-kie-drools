package org.jbpm.executor.impl;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jbpm.executor.ExecutorServiceFactory;
import org.kie.internal.executor.api.ExecutorService;

public class ExecutorServiceProducer {

	@Inject
	@PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;
	
	@Produces
	public ExecutorService produceExecutorService() {
		ExecutorService service = ExecutorServiceFactory.newExecutorService(emf);
		service.init();
		return service;
	}
	
	public void closeExecutor(@Disposes ExecutorService executorService) {
		executorService.destroy();
	}
}
