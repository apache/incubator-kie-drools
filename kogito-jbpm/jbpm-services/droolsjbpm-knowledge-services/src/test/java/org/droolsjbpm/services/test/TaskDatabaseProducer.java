/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.droolsjbpm.services.test;

import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import org.jboss.solder.core.ExtensionManaged;

/**
 *
 */
public class TaskDatabaseProducer {

    
    @PersistenceUnit(unitName = "org.jbpm.task")
    @ExtensionManaged
    @ApplicationScoped
    @Produces
    private EntityManagerFactory emf;

    @Produces
    public Logger createLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember()
                .getDeclaringClass().getName());
    }
}
