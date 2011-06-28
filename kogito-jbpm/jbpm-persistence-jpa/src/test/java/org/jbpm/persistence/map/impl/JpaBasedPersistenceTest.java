package org.jbpm.persistence.map.impl;

import static org.jbpm.persistence.util.PersistenceUtil.*;
import java.io.InputStream;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.base.MapGlobalResolver;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Before;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import bitronix.tm.utils.PropertyException;

public class JpaBasedPersistenceTest extends MapPersistenceTest {

    private PoolingDataSource ds1;
    private EntityManagerFactory emf;
    
    @Before
    public void setUp() throws Exception {
        Properties btmProps = getBitronixProperties(this);
        ds1 = setupPoolingDataSource(btmProps);
        ds1.init();
        
        emf = Persistence.createEntityManagerFactory( "org.drools.persistence.jpa" );
    }
    
    @After
    public void tearDown() throws Exception {
        emf.close();
        ds1.close();
    }
    
    @Override
    protected StatefulKnowledgeSession createSession(KnowledgeBase kbase) {
        return JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, createEnvironment() );
    }

    @Override
    protected StatefulKnowledgeSession disposeAndReloadSession(StatefulKnowledgeSession ksession,
                                                               KnowledgeBase kbase) {
        int ksessionId = ksession.getId();
        ksession.dispose();
        return JPAKnowledgeService.loadStatefulKnowledgeSession( ksessionId, kbase, null, createEnvironment() );
    }

    @Override
    protected int getProcessInstancesCount() {
        return emf.createEntityManager().createQuery( "FROM ProcessInstanceInfo" ).getResultList().size();
    }

    @Override
    protected int getKnowledgeSessionsCount() {
        return emf.createEntityManager().createQuery( "FROM SessionInfo" ).getResultList().size();
    }

    private Environment createEnvironment(){
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 emf );
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 TransactionManagerServices.getTransactionManager() );
        env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );
        
        return env;
    }
}
