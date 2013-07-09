/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.wih.internals;

import static org.kie.commons.io.FileSystemType.Bootstrap.BOOTSTRAP_INSTANCE;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceNio2WrapperImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class TaskEnvironmentProducer {

    private static final Logger logger = LoggerFactory.getLogger(TaskEnvironmentProducer.class);
  
    private static final String ORIGIN_URL      = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground.git";

    private IOService ioService = new IOServiceNio2WrapperImpl();    
    private EntityManagerFactory emf;

    
    @PersistenceUnit(unitName = "org.jbpm.services.task")
    @ApplicationScoped
    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        if (this.emf == null) {
            // this needs to be here for non EE containers

            this.emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");

        }
        return this.emf;
    }

    @Produces
    @ApplicationScoped
    public EntityManager getEntityManager() {
        final EntityManager em = getEntityManagerFactory().createEntityManager();
        EntityManager emProxy = (EntityManager) 
                Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{EntityManager.class}, new EmInvocationHandler(em));
        return emProxy;
    }

    @ApplicationScoped
    public void commitAndClose(@Disposes EntityManager em) {
        try {
            
            em.close();
        } catch (Exception e) {

        }
    }

   
    
    @Produces
    @Named("ioStrategy")
    public IOService prepareFileSystem() {

        try {
            final String userName = "guvnorngtestuser1";
            final String password = "test1234";
            final URI fsURI = URI.create( "git://jbpm-playground" );

            final Map<String, Object> env = new HashMap<String, Object>();
            env.put( "username", userName );
            env.put( "password", password );
            env.put( "origin", ORIGIN_URL );
            ioService.newFileSystem( fsURI, env, BOOTSTRAP_INSTANCE );
        } catch ( Exception e ) {
            logger.error("Error during file system preparation", e);
        }
        return ioService;
    }
    
    private class EmInvocationHandler implements InvocationHandler {

        private EntityManager delegate;
        
        EmInvocationHandler(EntityManager em) {
            this.delegate = em;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            joinTransactionIfNeeded();
            return method.invoke(delegate, args);
        }
        
        private void joinTransactionIfNeeded() {
            try {
                UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                if (ut.getStatus() == Status.STATUS_ACTIVE) {
                    delegate.joinTransaction();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
    
    
    
    
}
