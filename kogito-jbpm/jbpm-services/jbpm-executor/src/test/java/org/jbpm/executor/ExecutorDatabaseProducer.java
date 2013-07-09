/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor;

import static org.kie.commons.io.FileSystemType.Bootstrap.BOOTSTRAP_INSTANCE;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import org.kie.commons.io.IOService;
import org.kie.commons.io.impl.IOServiceNio2WrapperImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@ApplicationScoped
public class ExecutorDatabaseProducer {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorDatabaseProducer.class);
    
    private IOService ioService = new IOServiceNio2WrapperImpl();
    private static final String ORIGIN_URL      = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground.git";
    
    private EntityManagerFactory emf;
	
    @PersistenceUnit(unitName = "org.jbpm.executor")
    @ApplicationScoped
    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
    	if (this.emf == null) {
    		// this needs to be here for non EE containers
    		this.emf = Persistence.createEntityManagerFactory("org.jbpm.executor");
    	}
    	return this.emf;
    }

    @Produces
    @ApplicationScoped
    public EntityManager getEntityManager() {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        return em;
    }

    @ApplicationScoped
    public void commitAndClose(@Disposes EntityManager em) {
        try {
            em.getTransaction().commit();
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
            logger.error("Error while preparing file system ", e);
        }
        return ioService;
    }
    
    
    
}
