package org.drools.persistence.local;

import static org.drools.persistence.util.PersistenceUtil.*;
import static org.junit.Assert.*;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.io.ResourceFactory;
import org.drools.persistence.SingleSessionCommandService;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionSynchronization;
import org.drools.persistence.VariablePersistenceUnitTest;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Both the JTA and local txm's are actually tested in all tests in drools-persistence where they can 
 * both be tested. 
 * 
 * We use the {@Link RerunWithLocalTransactions} junit rule in the {@link VariablePersistenceUnitTest},
 * and this allows us to run every test (method) twice: once with JTA and once with local tx's. 
 *
 * This class only tests specific {@link LocalTransactionManager} things: at the moment, only memory usage is tested.
 */
public class LocalTransactionManagerTest {

    private HashMap<String, Object> context;
    private Environment env;

    @Before
    public void setUp() throws Exception {
        context = setupWithPoolingDataSource(DROOLS_LOCAL_PERSISTENCE_UNIT_NAME);
        env = createEnvironment(context);
    }

    @After
    public void tearDown() throws Exception {
        cleanUp(context);
    }

    @Test
    public void testGarbageCollectionSyncs() throws Exception {
        String str = "";
        str += "package org.drools.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "  Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( 1 );\n";
        str += "end\n";
        str += "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }

        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
       
        SingleSessionCommandService sscs 
            = (SingleSessionCommandService) ((CommandBasedStatefulKnowledgeSession) ksession).getCommandService();
        Field txmField = SingleSessionCommandService.class.getDeclaredField("txm");
        txmField.setAccessible(true);
        LocalTransactionManager ltxm = (LocalTransactionManager) txmField.get(sscs);
        assertNotNull("Could not retrieve txm field from sscs.", ltxm);
        
        List<?> list = new ArrayList<Object>();
        ksession.setGlobal("list", list);
        
        int enqueued = 0;
        List<WeakReference<TransactionSynchronization>> weakRefTxSyncList = new java.util.LinkedList<WeakReference<TransactionSynchronization>>();
        while( enqueued == 0 ) { 
            ksession.insert(1);
            for( TransactionSynchronization ts : ltxm.getTransactionSynchronizations().values() ) { 
                weakRefTxSyncList.add(new WeakReference<TransactionSynchronization>(ts));
            }
            ksession.insert(2);
            for( TransactionSynchronization ts : ltxm.getTransactionSynchronizations().values() ) { 
                weakRefTxSyncList.add(new WeakReference<TransactionSynchronization>(ts));
            }
            ksession.insert(3);
            for( TransactionSynchronization ts : ltxm.getTransactionSynchronizations().values() ) { 
                weakRefTxSyncList.add(new WeakReference<TransactionSynchronization>(ts));
            }
            
            ksession.fireAllRules();
            for( TransactionSynchronization ts : ltxm.getTransactionSynchronizations().values() ) { 
                weakRefTxSyncList.add(new WeakReference<TransactionSynchronization>(ts));
            }
            
            System.gc();
            for( WeakReference<TransactionSynchronization> weakRefTs : weakRefTxSyncList ) { 
                if( weakRefTs.get() == null ) { 
                    ++enqueued;
                }
            }
            debug( "enq: " + enqueued + ", [" + weakRefTxSyncList.size() + "]");
        } 
        
    }
    
    @Test
    public void testGarbageCollectionTransactions() throws Exception {
        LocalTransactionManager ltxm = new LocalTransactionManager();
        
        EntityManagerFactory emf = (EntityManagerFactory) context.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        
        int max = -1;
        int size = 0;
        while( size >= max ) { 
            EntityManager em = emf.createEntityManager();
            
            ltxm.registerTransactionSynchronization( new SynchronizationImpl() );
            ltxm.attachPersistenceContext(em);
            ltxm.commit(true);

            size = ltxm.getTransactionSynchronizations().size();
            
            System.gc();
            
            if( size > max ) { 
                max = size;
            }
            debug( size + ", " + max );
        } 
        
    }
    
    @Test
    public void testGarbageCollectionEntityManagers() throws Exception {
        LocalTransactionManager ltxm = new LocalTransactionManager();
        
        EntityManagerFactory emf = (EntityManagerFactory) context.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        
        int max = -1;
        int enqueued = 0;
        List<EntityManager> weakRefEmList = ltxm.getEntityManagers();
        Set<WeakReference<EntityManager>> totalWeakRefEmSet = new LinkedHashSet<WeakReference<EntityManager>>();
        int size = 0;
        do { 
            
            EntityManager em = emf.createEntityManager();
            Thread.sleep(10);
            ltxm.attachPersistenceContext(em);
            totalWeakRefEmSet.add(new WeakReference<EntityManager>(em));
            ltxm.commit(true);
            
            size = totalWeakRefEmSet.size();
            if( size > max ) { 
                max = size;
            }
            debug( size + ", " + max + ", " + weakRefEmList.size(), true );
            
            if( totalWeakRefEmSet.size() > 100 ) { 
                // it can't hurt
                System.gc();
                
                for( WeakReference<EntityManager> weakRefEm : totalWeakRefEmSet ) { 
                    em = weakRefEm.get();
                    if( em == null ) { 
                        debug( ".", false );
                        ++enqueued;
                    }
                }
                debug( "", true );
                debug( "ltxm: " + ltxm.getEntityManagers().size() + ", size " + totalWeakRefEmSet.size() + ", enq: " + enqueued, true );
            }
            
        } while( enqueued == 0 );
        
    }
    
    private static boolean debug = false;
    private void debug( String out ) { 
        debug( out, true );
    }
    private void debug(String out, boolean newLine) { 
        if( debug ) {
            if( newLine ) { 
                System.out.println(out);
            }
            else { 
                System.out.print(out);
            }
        }
    }
    
    static Map<Integer, String> hashIdMap = new HashMap<Integer, String>();
    static int idGen = 0;

    static synchronized String getHashId(Object obj) {
        if (obj == null) {
            return "[---]";
        }
        int hashCode = obj.hashCode();
        String hashId = hashIdMap.get(hashCode);
        if (hashId == null) {
            hashId = "[" + ++idGen + "]";
            hashIdMap.put(hashCode, hashId);
        }
        return hashId;
    }
    
    private static class SynchronizationImpl implements TransactionSynchronization {

        public SynchronizationImpl() { }

        public void afterCompletion(int status) {

        }

        public void beforeCompletion() {

        }

    }
}
