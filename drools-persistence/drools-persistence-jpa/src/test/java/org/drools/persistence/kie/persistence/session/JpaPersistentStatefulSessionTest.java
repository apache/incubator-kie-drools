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
package org.drools.persistence.kie.persistence.session;

import org.drools.compiler.Person;
import org.drools.core.SessionConfiguration;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.FireAllRulesInterceptor;
import org.drools.core.command.impl.LoggingInterceptor;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.time.SessionPseudoClock;
import org.drools.persistence.PersistableRunner;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.type.FactType;
import org.kie.api.definition.type.Position;
import org.kie.api.io.Resource;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static org.drools.persistence.util.DroolsPersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.DroolsPersistenceUtil.OPTIMISTIC_LOCKING;
import static org.drools.persistence.util.DroolsPersistenceUtil.PESSIMISTIC_LOCKING;
import static org.drools.persistence.util.DroolsPersistenceUtil.createEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class JpaPersistentStatefulSessionTest {

    private static Logger logger = LoggerFactory.getLogger(JpaPersistentStatefulSessionTest.class);
    
    private Map<String, Object> context;
    private Environment env;
    private boolean locking;

    @Parameters(name="{0}")
    public static Collection<Object[]> persistence() {
        Object[][] locking = new Object[][] { 
                { OPTIMISTIC_LOCKING }, 
                { PESSIMISTIC_LOCKING } 
                };
        return Arrays.asList(locking);
    };
    
    public JpaPersistentStatefulSessionTest(String locking) { 
        this.locking = PESSIMISTIC_LOCKING.equals(locking);
    }
    
    @Before
    public void setUp() throws Exception {
        context = DroolsPersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        env = createEnvironment(context);
        if( locking ) { 
            env.set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        }
    }
        
    @After
    public void tearDown() throws Exception {
        DroolsPersistenceUtil.cleanUp(context);
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

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        ks.newKieBuilder( kfs ).buildAll();

        KieBase kbase = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).getKieBase();
        KieSession ksession = ks.getStoreServices().newKieSession( kbase, null, env );

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
        
        ksession = ks.getStoreServices().loadKieSession(ksession.getIdentifier(), kbase, null, env);
        
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

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        ks.newKieBuilder( kfs ).buildAll();

        KieBase kbase = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).getKieBase();
        KieSession ksession = ks.getStoreServices().newKieSession( kbase, null, env );

        List<?> list = new ArrayList<Object>();

        ksession.setGlobal( "list",
                            list );

        ksession.insert( 1 );
        ksession.insert( 2 );
        ksession.insert( 3 );

        ksession.fireAllRules();

        assertEquals( 3,
                      list.size() );

    }

    @Test
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

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        ks.newKieBuilder( kfs ).buildAll();

        KieBase kbase = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).getKieBase();

        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        KieSession ksession = ks.getStoreServices().newKieSession( kbase, null, env );
        ut.commit();

        List<?> list = new ArrayList<Object>();

        // insert and commit
        ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
        ksession.setGlobal( "list",
                            list );
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
        assertEquals( 2,
                      list.size() );

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
        ksession = ks.getStoreServices().loadKieSession( ksession.getIdentifier(), kbase, null, env );
        
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

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        ks.newKieBuilder( kfs ).buildAll();

        KieBase kbase = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).getKieBase();

        KieSession ksession = ks.getStoreServices().newKieSession( kbase, null, env );
        PersistableRunner sscs = (PersistableRunner)
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

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        ks.newKieBuilder( kfs ).buildAll();

        KieBase kbase = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).getKieBase();

        KieSession ksession = ks.getStoreServices().newKieSession( kbase, null, env );
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
        KieServices ks = KieServices.Factory.get();
        KieBase kbase = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).getKieBase();
        KieSession ksession = ks.getStoreServices().newKieSession( kbase, null, env );

        Person x = new Person( "test" );
        List test = new ArrayList();
        List test2 = new ArrayList();
        test.add( x );
        test2.add( x );

        assertSame( test.get( 0 ), test2.get( 0 ) );

        ksession.insert( test );
        ksession.insert( test2 );
        ksession.fireAllRules();

        KieSession ksession2 = ks.getStoreServices().loadKieSession(ksession.getIdentifier(), kbase, null, env);

        Iterator c = ksession2.getObjects().iterator();
        List ref1 = (List) c.next();
        List ref2 = (List) c.next();

        assertSame( ref1.get( 0 ), ref2.get( 0 ) );

    }

    @Test
    public void testMergeConfig() {
        // JBRULES-3155
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

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        ks.newKieBuilder( kfs ).buildAll();

        KieBase kbase = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).getKieBase();

        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "com.example.CustomJPAProcessInstanceManagerFactory");
        KieSessionConfiguration config = ks.newKieSessionConfiguration(properties);

        KieSession ksession = ks.getStoreServices().newKieSession( kbase, config, env );
        SessionConfiguration sessionConfig = (SessionConfiguration)ksession.getSessionConfiguration();

        assertEquals("com.example.CustomJPAProcessInstanceManagerFactory", sessionConfig.getProcessInstanceManagerFactory());
    }
    
    @Test
    public void testMoreComplexRulesSerialization() throws Exception {
        KieServices ks = KieServices.Factory.get();

        Resource drlResource = ks.getResources().newClassPathResource("collect_rules.drl", JpaPersistentStatefulSessionTest.class);
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drlResource );
        ks.newKieBuilder( kfs ).buildAll();

        KieBase kbase = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).getKieBase();
        KieSession ksession = ks.getStoreServices().newKieSession( kbase, null, env );

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        FactType hereType = kbase.getFactType(this.getClass().getPackage().getName(), "Here");
        assertNotNull(hereType);
        Object here = hereType.newInstance();
        hereType.set(here, "place", "office");

        ksession.insert(here);
        ksession.fireAllRules();
    }

    public static class ListHolder implements Serializable {

        private static final long serialVersionUID = -3058814255413392428L;
        private List<String> things;
        private List<String> food;
        private List<String> exits;

        ListHolder() {
            this.things = new ArrayList<String>();
            this.food = new ArrayList<String>();
            this.exits = new ArrayList<String>();
        }

        public void setThings(List<String> things) {
            this.things = things;
        }

        public List<String> getThings() {
            return things;
        }

        public void setFood(List<String> food) {
            this.food = food;
        }

        public List<String> getFood() {
            return food;
        }

        public void setExits(List<String> exits) {
            this.exits = exits;
        }

        public List<String> getExits() {
            return exits;
        }
    }

    @Test
    public void testTraitsSerialization() throws Exception {
        String drl = "package org.drools.persistence.kie.persistence.session\n" +
                     "\n" +
                     "import java.util.List\n" +
                     "\n" +
                     "import org.drools.persistence.kie.persistence.session.JpaPersistentStatefulSessionTest.Door\n" +
                     "\n" +
                     "declare trait WoodenDoor\n" +
                     "    from : String\n" +
                     "    to : String\n" +
                     "    wood : String\n" +
                     "end\n" +
                     "\n" +
                     "rule \"wooden door\"\n" +
                     "    no-loop\n" +
                     "    when\n" +
                     "        $door : Door()\n" +
                     "    then\n" +
                     "        WoodenDoor woodenDoor = don( $door, WoodenDoor.class );\n" +
                     "end";

        KieServices ks = KieServices.Factory.get();

        Resource drlResource = ks.getResources().newByteArrayResource( drl.getBytes() );
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drlResource );
        ks.newKieBuilder( kfs ).buildAll();

        KieBase kbase = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).getKieBase();
        KieSession ksession = ks.getStoreServices().newKieSession( kbase, null, env );

        ksession.insert(new Door());
        ksession.fireAllRules();
    }

    @Test
    public void testGetCount() {
        // BZ-1022374
        String str = "";
        str += "package org.kie.test\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "then\n";
        str += " insertLogical( new String(\"a\") );\n";
        str += "end\n";
        str += "\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        ks.newKieBuilder( kfs ).buildAll();

        KieBase kbase = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).getKieBase();
        KieSession ksession = ks.getStoreServices().newKieSession( kbase, null, env );

        assertEquals(1, ksession.fireAllRules());
        assertEquals(1, ksession.getFactCount());
    }

    @Traitable
    public static class Door implements Serializable {

        private static final long serialVersionUID = 4173662501120948262L;
        @Position(0)
        private String fromLocation;
        @Position(1)
        private String toLocation;

        public Door() {
            this(null, null);
        }

        public Door(String fromLocation, String toLocation) {
            this.fromLocation = fromLocation;
            this.toLocation = toLocation;
        }

        public String getFromLocation() {
            return fromLocation;
        }

        public void setFromLocation(String fromLocation) {
            this.fromLocation = fromLocation;
        }

        public String getToLocation() {
            return toLocation;
        }

        public void setToLocation(String toLocation) {
            this.toLocation = toLocation;
        }
    }

    public static class Edible implements Serializable {

        private static final long serialVersionUID = -7102636642802292131L;
        @Position(0)
        private String thing;

        public Edible() {
            this(null);
        }

        public Edible(String thing) {
            this.thing = thing;
        }

        public String getThing() {
            return thing;
        }

        public void setThing(String thing) {
            this.thing = thing;
        }
    }

    @Test
    public void testSessionConfigurationFromContainer() {
        // DROOLS-1002
        String str = "rule R when then end";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel kmodel = ks.newKieModuleModel();
        kmodel.newKieBaseModel( "kbase1" )
              .newKieSessionModel( "ksession1" )
              .setClockType( ClockTypeOption.get( "pseudo" ) );

        KieFileSystem kfs = ks.newKieFileSystem()
                              .write( "src/main/resources/r1.drl", str )
                              .writeKModuleXML( kmodel.toXML() );

        ks.newKieBuilder( kfs ).buildAll();

        KieContainer kcontainer = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() );

        KieSessionConfiguration conf = kcontainer.getKieSessionConfiguration( "ksession1" );
        assertEquals( "pseudo", conf.getOption( ClockTypeOption.class ).getClockType() );

        KieSession ksession = ks.getStoreServices().newKieSession( kcontainer.getKieBase("kbase1"), conf, env );
        assertTrue(ksession.getSessionClock() instanceof SessionPseudoClock);
    }

    @Test
    public void testGetFactHandles() {
        // DROOLS-1270
        String str =
                "package org.kie.test\n" +
                "rule rule1 when\n" +
                "  String(this == \"A\")\n" +
                "then\n" +
                "  insertLogical( \"B\" );\n" +
                "end\n" +
                "\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );
        ks.newKieBuilder( kfs ).buildAll();

        KieBase kbase = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).getKieBase();
        KieSession ksession = ks.getStoreServices().newKieSession( kbase, null, env );

        ksession.insert( "A" );
        ksession.fireAllRules();
        assertEquals(2, ksession.getFactCount());

        for (FactHandle fh : ksession.getFactHandles()) {
            System.out.println(fh);
            if (fh.toString().contains( "String:A" )) {
                ksession.delete( fh );
            }
        }

        ksession.fireAllRules();
        assertEquals(0, ksession.getFactCount());
    }
}
