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

import static org.drools.persistence.util.PersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.PersistenceUtil.createEnvironment;
import static org.drools.persistence.util.PersistenceUtil.getValueOfField;
import static org.drools.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.junit.Assert.fail;
import static org.kie.api.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;

import java.io.Serializable;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.persistence.SingleSessionCommandService;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.infinispan.InfinispanPersistenceContextManager;
import org.drools.persistence.infinispan.marshaller.InfinispanPlaceholderResolverStrategy;
import org.drools.persistence.info.EntityHolder;
import org.drools.persistence.util.PersistenceUtil;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
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
import org.kie.internal.persistence.infinispan.InfinispanKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JtaTransactionManagerTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    // Datasource (setup & clean up)
    private HashMap<String, Object> context;
    private DefaultCacheManager cm;

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
        cm = (DefaultCacheManager) context.get(ENTITY_MANAGER_FACTORY);
    }

    @After
    public void tearDown() {
        PersistenceUtil.tearDown(context);
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
    public void basicTransactionManagerTest() {
        String testName = getTestName();
        
        // Setup the JtaTransactionmanager
        Environment env = createEnvironment(context);
        //TransactionManager txm = (TransactionManager) env.get( EnvironmentName.TRANSACTION_MANAGER );
        javax.transaction.TransactionManager tm = (javax.transaction.TransactionManager) env.get( EnvironmentName.TRANSACTION_MANAGER );
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
        Cache<Serializable, Object> cache = cm.getCache("jbpm-configured-cache");
        try { 
            // Begin the real trasnaction
            boolean txOwner = txm.begin();
      
            // Do the "sub" transaction 
            // - the txm doesn't really commit, 
            //   because we keep track of who's the tx owner.
            boolean notTxOwner = txm.begin();
            cache.put(generateId(mainObject), mainObject);
            txm.commit(notTxOwner);
       
            // Finish the transaction off
            cache.put(generateId(subObject), subObject);
            txm.commit(txOwner);
        }
        catch( Throwable t ) { 
            fail( "No exception should have been thrown: " + t.getMessage() );
        }
    }
   
    @Test
    public void basicTransactionRollbackTest() {
        Environment env = createEnvironment(context);
        //TransactionManager txm = (TransactionManager) env.get( EnvironmentName.TRANSACTION_MANAGER );
        javax.transaction.TransactionManager tm = (javax.transaction.TransactionManager) env.get( EnvironmentName.TRANSACTION_MANAGER );
        TransactionManager txm = new JtaTransactionManager( env.get( EnvironmentName.TRANSACTION ),
                env.get( EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY ),
                tm ); 
           
        // Create linked transactionTestObjects 
        TransactionTestObject mainObject = new TransactionTestObject();
        mainObject.setName("main");
        TransactionTestObject subObject = new TransactionTestObject();
        subObject.setName("sub");
        mainObject.setSubObject(subObject);
       
        Cache<Serializable, Object> cache = cm.getCache("jbpm-configured-cache");
        boolean txOwner = false;
        try { 
            txOwner = txm.begin();
            
            boolean notTxOwner = txm.begin();
            cache.put(generateId(mainObject), mainObject);
            txm.rollback(notTxOwner);
        } catch ( Exception e ) {
        	fail("There should not be an exception thrown here: " + e.getMessage());
        }
        try {
            cache.put(generateId(subObject), subObject);
            txm.rollback(txOwner);
        } catch( Exception e ) {
        	//Infinispan tries to commit every put, so an exception will be thrown here
        }
        
    }

    public static String COMMAND_ENTITY_MANAGER = "drools.persistence.test.command.EntityManager";
    public static String COMMAND_ENTITY_MANAGER_FACTORY = "drools.persistence.test.EntityManagerFactory";
    
    @Test
    public void testSingleSessionCommandServiceAndJtaTransactionManagerTogether() { 
            
        // Initialize drools environment stuff
        Environment env = createEnvironment(context);
        KnowledgeBase kbase = initializeKnowledgeBase(simpleRule);
        StatefulKnowledgeSession commandKSession = InfinispanKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        SingleSessionCommandService commandService = (SingleSessionCommandService) ((CommandBasedStatefulKnowledgeSession) commandKSession).getCommandService();
        InfinispanPersistenceContextManager jpm = (InfinispanPersistenceContextManager) getValueOfField("jpm", commandService);

        jpm.getApplicationScopedPersistenceContext();
        @SuppressWarnings("unchecked")
        Cache<String, EntityHolder> cache = (Cache<String, EntityHolder>) getValueOfField("appScopedCache", jpm);
        
        TransactionTestObject mainObject = new TransactionTestObject();
        mainObject.setName("mainCommand");
        TransactionTestObject subObject = new TransactionTestObject();
        subObject.setName("subCommand");
        mainObject.setSubObject(subObject);
       
        HashMap<String, Object> emEnv = new HashMap<String, Object>();
        emEnv.put(COMMAND_ENTITY_MANAGER_FACTORY, cm);
        emEnv.put(COMMAND_ENTITY_MANAGER, cache);
        
        TransactionTestCommand txTestCmd = new TransactionTestCommand(mainObject, subObject, emEnv);
       
        
        commandKSession.execute(txTestCmd);
        
    }

    private static int id=1;
    
    protected static Serializable generateId(TransactionTestObject obj) {
    	Serializable s = InfinispanPlaceholderResolverStrategy.getClassIdValue(obj);
    	return (s == null) ? new Integer(++id) : s;
    }
}
