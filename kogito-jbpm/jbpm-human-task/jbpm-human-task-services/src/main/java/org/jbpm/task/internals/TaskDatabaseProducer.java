/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.internals;

import java.util.logging.Logger;
import org.jbpm.task.annotations.TaskPersistence;
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

    @TaskPersistence
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
