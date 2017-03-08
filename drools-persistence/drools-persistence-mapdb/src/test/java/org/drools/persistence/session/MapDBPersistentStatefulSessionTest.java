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
package org.drools.persistence.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.drools.compiler.Address;
import org.drools.compiler.Person;
import org.drools.core.SessionConfiguration;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.FireAllRulesInterceptor;
import org.drools.core.command.impl.LoggingInterceptor;
import org.drools.core.runtime.ChainableRunner;
import org.drools.persistence.mapdb.MapDBSessionCommandService;
import org.drools.persistence.mapdb.util.MapDBPersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;

public class MapDBPersistentStatefulSessionTest {

    private Map<String, Object> context;
    private Environment env;

    @Before
    public void setUp() throws Exception {
        context = MapDBPersistenceUtil.setupMapDB();
        env = MapDBPersistenceUtil.createEnvironment(context);
    }
        
    @After
    public void tearDown() throws Exception {
        MapDBPersistenceUtil.cleanUp(context);
    }


    @Test
    public void testFactHandleSerialization() {
        String str = "";
        str += "package org.kie.test\n";
        str += "import java.util.concurrent.atomic.AtomicInteger\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "when\n";
        str += " $i: AtomicInteger(intValue > 0)\n";
        str += "then\n";
        str += " list.add( $i );\n";
        str += "end\n";
        str += "\n";

        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", ResourceFactory.newByteArrayResource(str.getBytes()));
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();

        KieSession ksession = KieServices.Factory.get().getStoreServices().newKieSession( kbase, null, env );
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal( "list",
                            list );

        AtomicInteger value = new AtomicInteger(4);
        FactHandle atomicFH = ksession.insert( value );

        ksession.fireAllRules();

        assertEquals( 1,
                      list.size() );

        value.addAndGet(1);
        ksession.update(atomicFH, value);
        ksession.fireAllRules();
        
        assertEquals( 2,
                list.size() );
        String externalForm = atomicFH.toExternalForm();
        
        ksession = KieServices.Factory.get().getStoreServices().loadKieSession(ksession.getIdentifier(), kbase, null, env);
        
        atomicFH = ksession.execute(CommandFactory.fromExternalFactHandleCommand(externalForm));
        
        value.addAndGet(1);
        ksession.update(atomicFH, value);
        
        ksession.fireAllRules();
        
        list = (List<?>) ksession.getGlobal("list");
        
        assertEquals( 3,
                list.size() );
        
    }
    
    @Test
    public void testLocalTransactionPerStatement() {
        String str = "";
        str += "package org.kie.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "  Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( 1 );\n";
        str += "end\n";
        str += "\n";

        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", ResourceFactory.newByteArrayResource(str.getBytes()));
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();
        KieSession ksession = KieServices.Factory.get().getStoreServices().newKieSession( kbase, null, env );
        List<?> list = new ArrayList<Object>();
        ksession.setGlobal( "list", list );
        ksession.insert( 1 );
        ksession.insert( 2 );
        ksession.insert( 3 );
        ksession.fireAllRules();
        assertEquals( 3, list.size() );
    }

    @Test @Ignore("NEXT: For now, we're not working with transactions")
    public void testUserTransactions() throws Exception {
        String str = "";
        str += "package org.kie.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "  $i : Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( $i );\n";
        str += "end\n";
        str += "\n";

        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", ResourceFactory.newByteArrayResource(str.getBytes()));
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();

        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        KieSession ksession = KieServices.Factory.get().getStoreServices().newKieSession( kbase, null, env );
        ut.commit();
        List<?> list = new ArrayList<Object>();
        // insert and commit
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.setGlobal( "list", list );
        ksession.insert( 1 );
        ksession.insert( 2 );
        ksession.fireAllRules();
        ut.commit();

        // insert and rollback
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.insert( 3 );
        ut.rollback();

        // check we rolled back the state changes from the 3rd insert
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.fireAllRules();
        ut.commit();
        assertEquals( 2, list.size() );

        // insert and commit
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.insert( 3 );
        ksession.insert( 4 );
        ut.commit();

        // rollback again, this is testing that we can do consecutive rollbacks and commits without issue
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.insert( 5 );
        ksession.insert( 6 );
        ut.rollback();

        ksession.fireAllRules();

        assertEquals( 4,
                      list.size() );
        
        // now load the ksession
        ksession = KieServices.Factory.get().getStoreServices().loadKieSession( ksession.getIdentifier(), kbase, null, env );
        
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.insert( 7 );
        ksession.insert( 8 );
        ut.commit();

        ksession.fireAllRules();

        assertEquals( 6,
                      list.size() );
    }

    @Test
    public void testInterceptor() {
        String str = "";
        str += "package org.kie.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "  Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( 1 );\n";
        str += "end\n";
        str += "\n";

        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", ResourceFactory.newByteArrayResource(str.getBytes()));
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();

        KieSession ksession = KieServices.Factory.get().getStoreServices().newKieSession( kbase, null, env );
        MapDBSessionCommandService sscs = (MapDBSessionCommandService)
            ((CommandBasedStatefulKnowledgeSession) ksession).getRunner();
        sscs.addInterceptor(new LoggingInterceptor());
        sscs.addInterceptor(new FireAllRulesInterceptor());
        sscs.addInterceptor(new LoggingInterceptor());
        List<?> list = new ArrayList<Object>();
        ksession.setGlobal( "list", list );
        ksession.insert( 1 );
        ksession.insert( 2 );
        ksession.insert( 3 );
        ksession.getWorkItemManager().completeWorkItem(0, null);
        assertEquals( 3, list.size() );
    }

    @Test
    public void testInterceptorOnRollback() throws Exception{
        String str = "";
        str += "package org.kie.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "  Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( 1 );\n";
        str += "end\n";
        str += "\n";

        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", ResourceFactory.newByteArrayResource(str.getBytes()));
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();

        KieSession ksession = KieServices.Factory.get().getStoreServices().newKieSession( kbase, null, env );
        MapDBSessionCommandService sscs = (MapDBSessionCommandService)
                ((CommandBasedStatefulKnowledgeSession) ksession).getRunner();
        sscs.addInterceptor(new LoggingInterceptor());
        sscs.addInterceptor(new FireAllRulesInterceptor());
        sscs.addInterceptor(new LoggingInterceptor());

        ChainableRunner internalCommandService = sscs.getChainableRunner();

        assertEquals(LoggingInterceptor.class, internalCommandService.getClass());
        internalCommandService = (ChainableRunner) internalCommandService.getNext();
        assertEquals(FireAllRulesInterceptor.class, internalCommandService.getClass());
        internalCommandService = (ChainableRunner) internalCommandService.getNext();
        assertEquals(LoggingInterceptor.class, internalCommandService.getClass());

        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        List<?> list = new ArrayList<Object>();
        ksession.setGlobal( "list", list );
        ksession.insert( 1 );
        ut.rollback();

        ksession.insert( 3 );

        internalCommandService = sscs.getChainableRunner();

        assertEquals(LoggingInterceptor.class, internalCommandService.getClass());
        internalCommandService = (ChainableRunner) internalCommandService.getNext();
        assertEquals(FireAllRulesInterceptor.class, internalCommandService.getClass());
        internalCommandService = (ChainableRunner) internalCommandService.getNext();
        assertEquals(LoggingInterceptor.class, internalCommandService.getClass());

    }
    
    @Test
    public void testSetFocus() {
        String str = "";
        str += "package org.kie.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "agenda-group \"badfocus\"";
        str += "when\n";
        str += "  Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( 1 );\n";
        str += "end\n";
        str += "\n";
    
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", ResourceFactory.newByteArrayResource(str.getBytes()));
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();

        KieSession ksession = KieServices.Factory.get().getStoreServices().newKieSession( kbase, null, env );
        List<?> list = new ArrayList<Object>();
    
        ksession.setGlobal( "list",
                            list );
    
        ksession.insert( 1 );
        ksession.insert( 2 );
        ksession.insert( 3 );
        ksession.getAgenda().getAgendaGroup("badfocus").setFocus();
    
        ksession.fireAllRules();
    
        assertEquals( 3,
                      list.size() );
    }
    
    @Test
    public void testSharedReferences() {
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KieSession ksession = KieServices.Factory.get().getStoreServices().newKieSession( kbase, null, env );

        Person x = new Person( "test" );
        List<Object> test = new ArrayList<>();
        List<Object> test2 = new ArrayList<>();
        test.add( x );
        test2.add( x );

        assertSame( test.get( 0 ), test2.get( 0 ) );

        ksession.insert( test );
        ksession.insert( test2 );
        ksession.fireAllRules();

        KieSession ksession2 = KieServices.Factory.get().getStoreServices().loadKieSession(ksession.getIdentifier(), kbase, null, env);

        Iterator<?> c = ksession2.getObjects().iterator();
        List<?> ref1 = (List<?>) c.next();
        List<?> ref2 = (List<?>) c.next();

        assertSame( ref1.get( 0 ), ref2.get( 0 ) );

    }

    @Test
    public void testMergeConfig() {
        // JBRULES-3155
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "com.example.CustomJPAProcessInstanceManagerFactory");
        KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);

        KieSession ksession = KieServices.Factory.get().getStoreServices().newKieSession( kbase, config, env );
        SessionConfiguration sessionConfig = (SessionConfiguration)ksession.getSessionConfiguration();

        assertEquals("com.example.CustomJPAProcessInstanceManagerFactory", sessionConfig.getProcessInstanceManagerFactory());
    }

    @Test
    public void testCreateAndDestroySession() {
        String str = "";
        str += "package org.kie.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "  Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( 1 );\n";
        str += "end\n";
        str += "\n";

        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", ResourceFactory.newByteArrayResource(str.getBytes()));
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();

        KieSession ksession = KieServices.Factory.get().getStoreServices().newKieSession( kbase, null, env );
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal( "list",
                list );

        ksession.insert( 1 );
        ksession.insert( 2 );
        ksession.insert( 3 );

        ksession.fireAllRules();

        assertEquals( 3, list.size() );

        long ksessionId = ksession.getIdentifier();
        ksession.destroy();

        try {
            KieServices.Factory.get().getStoreServices().loadKieSession(ksessionId, kbase, null, env);
            fail("There should not be any session with id " + ksessionId);
        } catch (Exception e) {

        }
    }

    @Test
    public void testCreateAndDestroyNonPersistentSession() {
        String str = "";
        str += "package org.kie.test\n";
        str += "global java.util.List list\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "  Integer(intValue > 0)\n";
        str += "then\n";
        str += "  list.add( 1 );\n";
        str += "end\n";
        str += "\n";

        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", ResourceFactory.newByteArrayResource(str.getBytes()));
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();

        KieSession ksession = kbase.newKieSession();
        List<?> list = new ArrayList<Object>();

        ksession.setGlobal( "list",
                list );

        ksession.insert( 1 );
        ksession.insert( 2 );
        ksession.insert( 3 );

        ksession.fireAllRules();

        assertEquals( 3, list.size() );

        long ksessionId = ksession.getIdentifier();
        ksession.destroy();

        try {
            ksession.fireAllRules();
            fail("Session should already be disposed " + ksessionId);
        } catch (IllegalStateException e) {

        }
    }

    @Test
    public void testFromNodeWithModifiedCollection() {
        // DROOLS-376
        String str = "";
        str += "package org.drools.test\n";
        str += "import org.drools.compiler.Person\n";
        str += "import org.drools.compiler.Address\n";
        str += "rule rule1\n";
        str += "when\n";
        str += " $p: Person($list : addresses)\n";
        str += " $a: Address(street == \"y\") from $list\n";
        str += "then\n";
        str += " $list.add( new Address(\"z\") );\n";
        str += " $list.add( new Address(\"w\") );\n";
        str += "end\n";
        str += "\n";

        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write("src/main/resources/r1.drl", ResourceFactory.newByteArrayResource(str.getBytes()));
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
        	fail (kbuilder.getResults().toString());
        }
        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();

        KieSession ksession = KieServices.Factory.get().getStoreServices().newKieSession( kbase, null, env );
        long sessionId = ksession.getIdentifier();

        Person p1 = new Person("John");
        p1.addAddress(new Address("x"));
        p1.addAddress(new Address("y"));

        ksession.insert( p1 );

        ksession.fireAllRules();

        assertEquals( 4,
                      p1.getAddresses().size() );

        ksession.dispose();

        // Should not fail here
        ksession = KieServices.Factory.get().getStoreServices().loadKieSession(sessionId, kbase, null, env);
    }
}
