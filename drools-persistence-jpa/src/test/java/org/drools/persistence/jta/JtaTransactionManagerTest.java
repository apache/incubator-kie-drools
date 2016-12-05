/*
 * Copyright 2011 Red Hat Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.persistence.jta;

import bitronix.tm.internal.BitronixRollbackException;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.persistence.PersistableRunner;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.jpa.JpaPersistenceContextManager;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.hibernate.TransientObjectException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.test.util.db.PersistenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.drools.persistence.util.DroolsPersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.DroolsPersistenceUtil.createEnvironment;
import static org.drools.persistence.util.DroolsPersistenceUtil.setupWithPoolingDataSource;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JtaTransactionManagerTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    // Datasource (setup & clean up)
    private Map<String, Object> context;
    private EntityManagerFactory emf;

    private static String simpleRule = "package org.kie.test\n"
            + "global java.util.List list\n" 
            + "rule rule1\n" 
            + "when\n"
            + "  Integer(intValue > 0)\n" 
            + "then\n" 
            + "  list.add( 1 );\n"
            + "end\n" 
            + "\n";

    @Before
    public void setup() {
        // This test does only plays with tx's, it doesn't actually persist
        // any interersting (wrt marshalling) SessionInfo objects
        boolean testMarshalling = false;

        context = setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME, testMarshalling);
        emf = (EntityManagerFactory) context.get(PersistenceUtil.ENTITY_MANAGER_FACTORY);
    }

    @After
    public void tearDown() {
        DroolsPersistenceUtil.cleanUp(context);
    }

    private KnowledgeBase initializeKnowledgeBase(String rule) { 
        // Initialize knowledge base/session/etc..
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(rule.getBytes()), ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
       
        return kbase;
    }
   
    public static final String DEFAULT_USER_TRANSACTION_NAME = "java:comp/UserTransaction";

    protected UserTransaction findUserTransaction() {
        try {
            InitialContext context = new InitialContext();
            return (UserTransaction) context.lookup( DEFAULT_USER_TRANSACTION_NAME );
        } catch ( NamingException ex ) {
            logger.debug( "No UserTransaction found at JNDI location [{}]",
                          DEFAULT_USER_TRANSACTION_NAME,
                          ex );
            return null;
        }
    }

    private String getTestName() { 
        StackTraceElement [] ste = Thread.currentThread().getStackTrace();
        String methodName =  ste[2].getMethodName();
        return methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
    }
    
    @Test
    public void showingTransactionTestObjectsNeedTransactions()  throws Exception {
        String testName = getTestName();
        
        // Create linked transactionTestObjects but only persist the main one.
        TransactionTestObject badMainObject = new TransactionTestObject();
        badMainObject.setName("bad" + testName);
        TransactionTestObject subObject = new TransactionTestObject();
        subObject.setName("sub" + testName);
        badMainObject.setSubObject(subObject);
       
        // Initialized persistence/tx's and persist to db
        EntityManager em = emf.createEntityManager();
        UserTransaction tx = findUserTransaction();
        tx.begin();
        em.joinTransaction();
        em.persist(badMainObject);
        
        boolean rollBackExceptionthrown = false;
        try { 
            logger.info("The following " + IllegalStateException.class.getSimpleName() + " SHOULD be thrown.");
            tx.commit();
        }
        catch( Exception e ) { 
            if( e instanceof BitronixRollbackException || e.getCause() instanceof TransientObjectException ) { 
                rollBackExceptionthrown = true;
                
                // Depends on JTA version (and thus BTM version)
                if( tx.getStatus() == 1 ) {
                    tx.rollback();
                }
            }
        }           
        assertTrue( "A rollback exception should have been thrown because of foreign key violations.", rollBackExceptionthrown );
       
        TransactionTestObject mainObject = new TransactionTestObject();
        mainObject.setName("main" + testName);
        mainObject.setSubObject(subObject);
        
        // Now persist both.. 
        tx.begin();
        em.joinTransaction();
        em.persist(mainObject);
        em.persist(subObject);
        
        try { 
            tx.commit();
        }
        catch( Exception e ) { 
            e.printStackTrace();
            fail( "No exception should have been thrown: " + e.getMessage() );
        }           
    }

    @Test
    public void basicTransactionManagerTest() {
        String testName = getTestName();
        
        // Setup the JtaTransactionmanager
        Environment env = createEnvironment(context);
        Object tm = env.get( EnvironmentName.TRANSACTION_MANAGER );
        TransactionManager txm = new JtaTransactionManager( env.get( EnvironmentName.TRANSACTION ),
                env.get( EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY ),
                tm ); 
           
        // Create linked transactionTestObjects 
        TransactionTestObject mainObject = new TransactionTestObject();
        mainObject.setName("main" + testName);
        TransactionTestObject subObject = new TransactionTestObject();
        subObject.setName("sub" + testName);
        mainObject.setSubObject(subObject);
      
        // Commit the mainObject after "commiting" the subObject
        EntityManager em = emf.createEntityManager();
        try { 
            // Begin the real trasnaction
            boolean txOwner = txm.begin();
      
            // Do the "sub" transaction 
            // - the txm doesn't really commit, 
            //   because we keep track of who's the tx owner.
            boolean notTxOwner = txm.begin();
            em.persist(mainObject);
            txm.commit(notTxOwner);
       
            // Finish the transaction off
            em.persist(subObject);
            txm.commit(txOwner);
        }
        catch( Throwable t ) { 
            fail( "No exception should have been thrown: " + t.getMessage() );
        }
    }
   
    @Test
    public void basicTransactionRollbackTest() {
        Environment env = createEnvironment(context);
        Object tm = env.get( EnvironmentName.TRANSACTION_MANAGER );
        TransactionManager txm = new JtaTransactionManager( env.get( EnvironmentName.TRANSACTION ),
                env.get( EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY ),
                tm ); 
           
        // Create linked transactionTestObjects 
        TransactionTestObject mainObject = new TransactionTestObject();
        mainObject.setName("main");
        TransactionTestObject subObject = new TransactionTestObject();
        subObject.setName("sub");
        mainObject.setSubObject(subObject);
       
        EntityManager em = emf.createEntityManager();
        try { 
            boolean txOwner = txm.begin();
            
            boolean notTxOwner = txm.begin();
            em.persist(mainObject);
            txm.rollback(notTxOwner);
        
            em.persist(subObject);
            txm.rollback(txOwner);
        }
        catch( Exception e ) { 
            fail("There should not be an exception thrown here: " + e.getMessage());
        }
        
    }

    public static String COMMAND_ENTITY_MANAGER = "drools.persistence.test.command.EntityManager";
    public static String COMMAND_ENTITY_MANAGER_FACTORY = "drools.persistence.test.EntityManagerFactory";
    
    @Test
    public void testSingleSessionCommandServiceAndJtaTransactionManagerTogether() throws Exception { 
        // Initialize drools environment stuff
        Environment env = createEnvironment(context);
        KnowledgeBase kbase = initializeKnowledgeBase(simpleRule);
        StatefulKnowledgeSession commandKSession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        commandKSession.getIdentifier(); // initialize CSEM
        PersistableRunner commandService = (PersistableRunner) ((CommandBasedStatefulKnowledgeSession) commandKSession).getRunner();
        JpaPersistenceContextManager jpm = (JpaPersistenceContextManager) getValueOfField("jpm", commandService);
        
        TransactionTestObject mainObject = new TransactionTestObject();
        mainObject.setName("mainCommand");
        TransactionTestObject subObject = new TransactionTestObject();
        subObject.setName("subCommand");
        mainObject.setSubObject(subObject);

        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        HashMap<String, Object> emEnv = new HashMap<String, Object>();
        emEnv.put(COMMAND_ENTITY_MANAGER_FACTORY, emf);
        emEnv.put(COMMAND_ENTITY_MANAGER, jpm.getCommandScopedEntityManager());
        
        TransactionTestCommand txTestCmd = new TransactionTestCommand(mainObject, subObject, emEnv);
        
        commandKSession.execute(txTestCmd);

        ut.commit();
    }

    /**
     * Reflection method when doing ugly hacks in tests.
     * 
     * @param fieldname
     *            The name of the field to be retrieved.
     * @param source
     *            The object containing the field to be retrieved.
     * @return The value (object instance) stored in the field requested from
     *         the given source object.
     */
    public static Object getValueOfField(String fieldname, Object source) {
        String sourceClassName = source.getClass().getSimpleName();
    
        Field field = null;
        try {
            field = source.getClass().getDeclaredField(fieldname);
            field.setAccessible(true);
        } catch (SecurityException e) {
            fail("Unable to retrieve " + fieldname + " field from " + sourceClassName + ": " + e.getCause());
        } catch (NoSuchFieldException e) {
            fail("Unable to retrieve " + fieldname + " field from " + sourceClassName + ": " + e.getCause());
        }
    
        assertNotNull("." + fieldname + " field is null!?!", field);
        Object fieldValue = null;
        try {
            fieldValue = field.get(source);
        } catch (IllegalArgumentException e) {
            fail("Unable to retrieve value of " + fieldname + " from " + sourceClassName + ": " + e.getCause());
        } catch (IllegalAccessException e) {
            fail("Unable to retrieve value of " + fieldname + " from " + sourceClassName + ": " + e.getCause());
        }
        return fieldValue;
    }
}
