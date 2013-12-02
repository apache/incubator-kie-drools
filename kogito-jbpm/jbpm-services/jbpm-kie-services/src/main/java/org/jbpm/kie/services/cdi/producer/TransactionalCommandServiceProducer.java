package org.jbpm.kie.services.cdi.producer;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;

import org.jbpm.shared.services.impl.TransactionalCommandService;

public class TransactionalCommandServiceProducer {
	
    @Produces
    public TransactionalCommandService produceCommandService(EntityManagerFactory emf) {
    	return new TransactionalCommandService(emf);
    }
}
