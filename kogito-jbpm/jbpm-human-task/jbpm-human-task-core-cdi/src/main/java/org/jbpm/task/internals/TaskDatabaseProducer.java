/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.internals;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import org.jboss.solder.core.ExtensionManaged;

/**
 *
 * @author salaboy
 */
public class TaskDatabaseProducer {

    @PersistenceUnit(unitName = "org.jbpm.task")
    @Produces
    @ExtensionManaged
    @ApplicationScoped
    EntityManagerFactory emf;
}
