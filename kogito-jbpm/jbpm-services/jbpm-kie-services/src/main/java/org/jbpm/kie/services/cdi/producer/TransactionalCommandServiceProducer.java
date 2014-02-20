package org.jbpm.kie.services.cdi.producer;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jbpm.shared.services.impl.TransactionalCommandService;

public class TransactionalCommandServiceProducer {

    @Inject
    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;

    @Produces
    public TransactionalCommandService produceCommandService() {
        return new TransactionalCommandService( emf );
    }
}
