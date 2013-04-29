/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.form.builder.services.internal;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import org.jboss.solder.core.ExtensionManaged;

/**
 *
 */

public class FormDatabaseProducer {
    @PersistenceUnit(unitName = "org.jbpm.form.builder")
    @ExtensionManaged
    @ApplicationScoped
    @Produces
    private EntityManagerFactory emf;
    
    
}
