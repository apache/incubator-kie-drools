/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.internals;

import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import org.jboss.solder.core.ExtensionManaged;
import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceNio2WrapperImpl;

/**
 *
 */
public class TestProducers {

    
    @PersistenceUnit(unitName = "org.jbpm.task")
    @ExtensionManaged
    @ApplicationScoped
    @Produces
    private EntityManagerFactory emf;

    private final IOService ioService = new IOServiceNio2WrapperImpl();
    
    @Produces
    public Logger createLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember()
                .getDeclaringClass().getName());
    }
    
    @Produces
    @Named("ioStrategy")
    public IOService createIOService(){
        return ioService;
    }
     
    
    
    
}
