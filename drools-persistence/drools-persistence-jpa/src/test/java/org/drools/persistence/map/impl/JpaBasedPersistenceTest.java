package org.drools.persistence.map.impl;

import org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy;
import org.drools.persistence.jta.JtaTransactionManager;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static org.drools.persistence.util.DroolsPersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.DroolsPersistenceUtil.OPTIMISTIC_LOCKING;
import static org.drools.persistence.util.DroolsPersistenceUtil.PESSIMISTIC_LOCKING;
import static org.drools.persistence.util.DroolsPersistenceUtil.createEnvironment;
import static org.drools.persistence.util.DroolsPersistenceUtil.useTransactions;
import static org.kie.api.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;
import static org.kie.api.runtime.EnvironmentName.USE_PESSIMISTIC_LOCKING;

@RunWith(Parameterized.class)
public class JpaBasedPersistenceTest extends MapPersistenceTest {

    private static Logger logger = LoggerFactory.getLogger(JPAPlaceholderResolverStrategy.class);
    
    private Map<String, Object> context;
    private EntityManagerFactory emf;
    private JtaTransactionManager txm;
    private boolean useTransactions = false;
    private boolean locking;
    
    @Parameters(name="{0}")
    public static Collection<Object[]> persistence() {
        Object[][] locking = new Object[][] { 
                { OPTIMISTIC_LOCKING }, 
                { PESSIMISTIC_LOCKING } 
                };
        return Arrays.asList(locking);
    };
    
    public JpaBasedPersistenceTest(String locking) { 
        this.locking = PESSIMISTIC_LOCKING.equals(locking);
    }
    
    @Before
    public void setUp() throws Exception {
        context = DroolsPersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
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
        DroolsPersistenceUtil.cleanUp(context);
    }
    

    @Override
    protected KieSession createSession(KieBase kbase) {
        Environment env = createEnvironment(context);
        if( this.locking ) { 
            env.set(USE_PESSIMISTIC_LOCKING, true);
        }
        return JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env);
    }

    @Override
    protected KieSession disposeAndReloadSession(KieSession ksession, KieBase kbase) {
        long ksessionId = ksession.getIdentifier();
        ksession.dispose();
        return JPAKnowledgeService.loadStatefulKnowledgeSession(ksessionId, kbase, null, createEnvironment(context));
    }

    @Override
    protected long getSavedSessionsCount() {
        logger.info("quering number of saved sessions.");
        boolean transactionOwner = false;
        if( useTransactions ) { 
            transactionOwner = txm.begin();
        }
        long savedSessionsCount =  emf.createEntityManager().createQuery( "FROM SessionInfo" ).getResultList().size();
        if( useTransactions ) { 
            txm.commit(transactionOwner);
        }
        return savedSessionsCount;
    }

}
