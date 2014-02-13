package org.jbpm.kie.services.cdi.producer;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jbpm.shared.services.impl.TransactionalCommandService;

public class TransactionalCommandServiceProducer {
	
    @Produces
    @PersistenceUnit(unitName = "org.jbpm.domain")
    public TransactionalCommandService produceCommandService(EntityManagerFactory emf) {
    	return new TransactionalCommandService(emf);
    }
}
