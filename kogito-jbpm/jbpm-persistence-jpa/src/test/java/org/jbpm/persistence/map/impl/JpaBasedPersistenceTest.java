package org.jbpm.persistence.map.impl;

import static org.jbpm.persistence.util.PersistenceUtil.*;
import static org.kie.api.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;
import static org.kie.api.runtime.EnvironmentName.USE_PESSIMISTIC_LOCKING;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import javax.persistence.EntityManagerFactory;

import org.drools.persistence.jta.JtaTransactionManager;
import org.jbpm.persistence.util.PersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;

@RunWith(Parameterized.class)
public class JpaBasedPersistenceTest extends MapPersistenceTest {

    private HashMap<String, Object> context;
    private EntityManagerFactory emf;
    private JtaTransactionManager txm;
    private boolean useTransactions = false;
    
    public JpaBasedPersistenceTest(boolean locking) { 
       this.useLocking = locking; 
    }
    
    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] { { false }, { true } };
        return Arrays.asList(data);
    };
    
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
       cleanUp(context); 
    }
    
    @Override
    protected StatefulKnowledgeSession createSession(KieBase kbase) {
        Environment env = createEnvironment(context);
        if( this.useLocking ) { 
            env.set(USE_PESSIMISTIC_LOCKING, true);
        }
        return JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env);
    }

    @Override
    protected StatefulKnowledgeSession disposeAndReloadSession(StatefulKnowledgeSession ksession, long ksessionId,
                                                               KieBase kbase) {
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
