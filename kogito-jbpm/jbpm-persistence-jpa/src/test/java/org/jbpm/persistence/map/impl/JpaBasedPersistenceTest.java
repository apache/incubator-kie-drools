package org.jbpm.persistence.map.impl;

import static org.drools.persistence.util.PersistenceUtil.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.base.MapGlobalResolver;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.jta.JtaTransactionManager;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Before;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class JpaBasedPersistenceTest extends MapPersistenceTest {

    private PoolingDataSource ds1;
    private EntityManagerFactory emf;
    private JtaTransactionManager txm;
    private boolean useTransactions = false;
    
    @Before
    public void setUp() throws Exception {
        ds1 = setupPoolingDataSource();
        ds1.init();
        
        emf = Persistence.createEntityManagerFactory( JBPM_PERSISTENCE_UNIT_NAME );
        
        if( useTransactions() ) { 
            useTransactions = true;
            Environment env = createEnvironment();
            Object tm = env.get( EnvironmentName.TRANSACTION_MANAGER );
            this.txm = new JtaTransactionManager( env.get( EnvironmentName.TRANSACTION ),
                env.get( EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY ),
                tm );
        }
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
        boolean txOwner = false;
        if( useTransactions ) { 
            txOwner = txm.begin();
        }
        int size =  emf.createEntityManager().createQuery( "FROM ProcessInstanceInfo" ).getResultList().size();
        if( useTransactions ) { 
            txm.commit(txOwner);
        }
        return size;
    }

    @Override
    protected int getKnowledgeSessionsCount() {
        boolean transactionOwner = false;
        if( useTransactions ) { 
            transactionOwner = txm.begin();
        }
        int size = emf.createEntityManager().createQuery( "FROM SessionInfo" ).getResultList().size();
        if( useTransactions ) { 
            txm.commit(transactionOwner);
        }
        return size;
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
