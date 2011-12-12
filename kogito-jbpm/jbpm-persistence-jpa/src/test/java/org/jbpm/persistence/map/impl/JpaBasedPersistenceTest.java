package org.jbpm.persistence.map.impl;

import static org.drools.persistence.util.PersistenceUtil.*;
import static org.drools.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;

import org.drools.KnowledgeBase;
import org.drools.marshalling.util.MarshallingTestUtil;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.jta.JtaTransactionManager;
import org.drools.persistence.util.PersistenceUtil;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;

public class JpaBasedPersistenceTest extends MapPersistenceTest {

    private HashMap<String, Object> context;
    private EntityManagerFactory emf;
    private JtaTransactionManager txm;
    private boolean useTransactions = false;
    
    @Before
    public void setUp() throws Exception {
        context = PersistenceUtil.setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        emf = (EntityManagerFactory) context.get(ENTITY_MANAGER_FACTORY);
        
        if( useTransactions() ) { 
            useTransactions = true;
            Environment env = createEnvironment(context);
            Object tm = env.get( EnvironmentName.TRANSACTION_MANAGER );
            this.txm = new JtaTransactionManager( env.get( EnvironmentName.TRANSACTION ),
                env.get( EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY ),
                tm );
        }
    }
    
    @After
    public void tearDown() throws Exception {
       PersistenceUtil.tearDown(context); 
    }
    
    @AfterClass
    public static void compareMarshallingData() throws Exception {
       MarshallingTestUtil.compareMarshallingDataFromTest(JBPM_PERSISTENCE_UNIT_NAME);
    }
   
    @Override
    protected StatefulKnowledgeSession createSession(KnowledgeBase kbase) {
        return JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, createEnvironment(context) );
    }

    @Override
    protected StatefulKnowledgeSession disposeAndReloadSession(StatefulKnowledgeSession ksession, int ksessionId,
                                                               KnowledgeBase kbase) {
        ksession.dispose();
        return JPAKnowledgeService.loadStatefulKnowledgeSession( ksessionId, kbase, null, createEnvironment(context) );
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

}
