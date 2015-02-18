package org.drools.compiler.integrationtests.marshalling;

import org.drools.compiler.Address;
import org.drools.compiler.Cell;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.FactA;
import org.drools.compiler.FactB;
import org.drools.compiler.FactC;
import org.drools.compiler.Message;
import org.drools.compiler.Person;
import org.drools.compiler.Primitives;
import org.drools.compiler.integrationtests.IteratorToList;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.core.ClockType;
import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemory;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BaseNode;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.IdentityPlaceholderResolverStrategy;
import org.drools.core.marshalling.impl.RuleBaseNodes;
import org.drools.core.reteoo.MockTupleSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.MapBackedClassLoader;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.time.impl.DurationTimer;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.core.util.KeyStoreHelper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyAcceptor;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.TimerJobFactoryOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.time.SessionClock;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static org.drools.compiler.integrationtests.SerializationHelper.getSerialisedStatefulKnowledgeSession;

public class MarshallingTest extends CommonTestMethodBase {

    @Test
    public void testSerializable() throws Exception {
        Collection<KnowledgePackage>  kpkgs = loadKnowledgePackages("../test_Serializable.drl" );
        KnowledgePackage kpkg = kpkgs.iterator().next();
        kpkg = SerializationHelper.serializeObject( kpkg );

        KnowledgeBase kbase = loadKnowledgeBase();
        kbase.addKnowledgePackages( Collections.singleton( kpkg ) );

        Map<String, KnowledgeBase> map = new HashMap<String, KnowledgeBase>();
        map.put( "x",
                 kbase );
        map = SerializationHelper.serializeObject( map );
        kbase = map.get( "x" );

        final org.kie.api.definition.rule.Rule[] rules = kbase.getKnowledgePackages().iterator().next().getRules().toArray( new org.kie.api.definition.rule.Rule[0] );
        assertEquals( 4,
                      rules.length );

        assertEquals( "match Person 1",
                      rules[0].getName() );
        assertEquals( "match Person 2",
                      rules[1].getName() );
        assertEquals( "match Person 3",
                      rules[2].getName() );
        assertEquals( "match Integer",
                      rules[3].getName() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.setGlobal( "list",
                            new ArrayList() );

        final Person bob = new Person( "bob" );
        ksession.insert( bob );

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              true );

        assertEquals( 1,
                      ksession.getFactCount() );
        assertEquals( bob,
                      ksession.getObjects().iterator().next() );

        assertEquals( 2,
                      ((InternalAgenda) ksession.getAgenda()).agendaSize() );

        ksession.fireAllRules();

        List list = (List) ksession.getGlobal( "list" );

        assertEquals( 3,
                      list.size() );
        // because of agenda-groups
        assertEquals( new Integer( 4 ),
                      list.get( 0 ) );

        // need to create a new collection or otherwise the collection will be identity based
        List< ? > objects = new ArrayList<Object>( ksession.getObjects() );
        assertEquals( 2, objects.size() );
        assertTrue( objects.contains( bob ) );
        assertTrue( objects.contains( new Person( "help" ) ) );
    }

    @Test
    public void testSerializeWorkingMemoryAndRuleBase1() throws Exception {
        // has the first newStatefulSession before the ruleBase is serialised
        Collection<KnowledgePackage>  kpkgs = loadKnowledgePackages("../test_Serializable.drl" );

        KnowledgeBase kBase = getKnowledgeBase();

        StatefulKnowledgeSession session = kBase.newStatefulKnowledgeSession();

        Map map = new HashMap();
        map.put( "x",
                 kBase );
        map = SerializationHelper.serializeObject( map );
        kBase = (KnowledgeBase) map.get( "x" );

        kBase.addKnowledgePackages( kpkgs );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );
        kBase = SerializationHelper.serializeObject( kBase );

        session.setGlobal( "list",
                           new ArrayList() );

        final Person bob = new Person( "bob" );
        session.insert( bob );

        org.kie.api.definition.rule.Rule[] rules = (org.kie.api.definition.rule.Rule[]) kBase.getKnowledgePackage("org.drools.compiler.test").getRules().toArray(new org.kie.api.definition.rule.Rule[0] );

        assertEquals( 4,
                      rules.length );

        assertEquals( "match Person 1",
                      rules[0].getName() );
        assertEquals( "match Person 2",
                      rules[1].getName() );
        assertEquals( "match Person 3",
                      rules[2].getName() );
        assertEquals( "match Integer",
                      rules[3].getName() );

        Assert.assertEquals(1, session.getObjects().size());
        assertEquals( bob,
                      IteratorToList.convert( session.getObjects().iterator() ).get(0) );

        assertEquals(2,
                     ((InternalAgenda) session.getAgenda()).agendaSize());

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        session.fireAllRules();

        final List list = (List) session.getGlobal( "list" );

        assertEquals( 3,
                      list.size() );
        // because of agenda-groups
        assertEquals( new Integer( 4 ),
                      list.get( 0 ) );

        assertEquals( 2, session.getObjects().size() );
        assertTrue(IteratorToList.convert(session.getObjects().iterator()).contains(bob));
        assertTrue(IteratorToList.convert(session.getObjects().iterator()).contains( new Person( "help" ) ) );

    }

    @Test
    public void testSerializeWorkingMemoryAndRuleBase2() throws Exception {
        Collection<KnowledgePackage>  kpkgs = loadKnowledgePackages("../test_Serializable.drl" );

        KnowledgeBase kBase = getKnowledgeBase();

        // serialise a hashmap with the RuleBase as a key
        Map map = new HashMap();
        map.put( "x",
                 kBase );
        map = SerializationHelper.serializeObject( map );
        kBase = (KnowledgeBase) map.get( "x" );

        StatefulKnowledgeSession session = kBase.newStatefulKnowledgeSession();

        // serialise the working memory before population
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);

        kBase.addKnowledgePackages(kpkgs);

        session.setGlobal( "list",
                           new ArrayList() );

        final Person bob = new Person( "bob" );
        session.insert( bob );

        org.kie.api.definition.rule.Rule[] rules = (org.kie.api.definition.rule.Rule[]) kBase.getKnowledgePackage("org.drools.compiler.test").getRules().toArray(new org.kie.api.definition.rule.Rule[0] );

        assertEquals( 4,
                      rules.length );

        assertEquals( "match Person 1",
                      rules[0].getName() );
        assertEquals( "match Person 2",
                      rules[1].getName() );
        assertEquals( "match Person 3",
                      rules[2].getName() );
        assertEquals( "match Integer",
                      rules[3].getName() );

        assertEquals(1,
                     session.getObjects().size());
        assertEquals( bob,
                      IteratorToList.convert( session.getObjects().iterator() ).get( 0 ) );

        assertEquals( 2,
                      ((InternalAgenda) session.getAgenda()).agendaSize() );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        session.fireAllRules();

        final List list = (List) session.getGlobal( "list" );

        assertEquals( 3,
                      list.size() );
        // because of agenda-groups
        assertEquals( new Integer( 4 ),
                      list.get( 0 ) );

        assertEquals( 2, session.getObjects().size() );
        assertTrue( IteratorToList.convert( session.getObjects().iterator() ).contains( bob ) );
        assertTrue(IteratorToList.convert( session.getObjects().iterator() ).contains( new Person( "help" ) ) );
    }

    @Test
    public void testSerializeWorkingMemoryAndRuleBase3() throws Exception {
        // has the first newStatefulSession after the ruleBase is serialised
        Collection<KnowledgePackage>  kpkgs = loadKnowledgePackages("../test_Serializable.drl" );

        KnowledgeBase kBase = getKnowledgeBase();
        StatefulKnowledgeSession session = kBase.newStatefulKnowledgeSession();

        kBase.addKnowledgePackages(kpkgs);

        session.setGlobal( "list",
                           new ArrayList() );

        final Person bob = new Person( "bob" );
        session.insert( bob );

        // serialise a hashmap with the RuleBase as a key, after WM population
        Map map = new HashMap();
        map.put( "x",
                 kBase );
        map = SerializationHelper.serializeObject( map );
        kBase = (KnowledgeBase) map.get( "x" );

        // now try serialising with a fully populated wm from a serialised rulebase
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);

        org.kie.api.definition.rule.Rule[] rules = (org.kie.api.definition.rule.Rule[]) kBase.getKnowledgePackage("org.drools.compiler.test").getRules().toArray(new org.kie.api.definition.rule.Rule[0] );

        assertEquals( 4,
                      rules.length );

        assertEquals( "match Person 1",
                      rules[0].getName() );
        assertEquals( "match Person 2",
                      rules[1].getName() );
        assertEquals( "match Person 3",
                      rules[2].getName() );
        assertEquals( "match Integer",
                      rules[3].getName() );

        assertEquals( 1,
                      session.getObjects().size() );
        assertEquals( bob,
                      IteratorToList.convert( session.getObjects().iterator() ).get( 0 ) );

        assertEquals( 2,
                      ((InternalAgenda) session.getAgenda()).agendaSize() );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);

        session.fireAllRules();

        final List list = (List) session.getGlobal( "list" );

        assertEquals( 3,
                      list.size() );
        // because of agenda-groups
        assertEquals( new Integer( 4 ),
                      list.get( 0 ) );

        assertEquals( 2, session.getObjects().size() );
        assertTrue( IteratorToList.convert( session.getObjects().iterator() ).contains( bob ) );
        assertTrue(IteratorToList.convert( session.getObjects().iterator() ).contains( new Person( "help" ) ) );
    }

    @Test
    public void testSerializeAdd() throws Exception {
        Collection<KnowledgePackage>  kpkgs = loadKnowledgePackages("../test_Dynamic1.drl" );
        kpkgs = SerializationHelper.serializeObject( kpkgs );
        KnowledgeBase kBase = getKnowledgeBase();
        kBase.addKnowledgePackages(kpkgs);
        kBase = SerializationHelper.serializeObject( kBase );

        StatefulKnowledgeSession session = kBase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        InternalFactHandle stilton = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                      10 ) );
        InternalFactHandle brie = (InternalFactHandle) session.insert( new Cheese( "brie",
                                                                                   10 ) );
        session.fireAllRules();

        assertEquals( list.size(),
                      1 );
        assertEquals( "stilton",
                      list.get( 0 ) );

        // now recreate the rulebase, deserialize the session and test it
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);
        list = (List) session.getGlobal( "list" );

        assertNotNull( list );
        assertEquals( list.size(),
                      1 );
        assertEquals( "stilton",
                      list.get( 0 ) );

        kpkgs = loadKnowledgePackages("../test_Dynamic3.drl" );
        kpkgs = SerializationHelper.serializeObject( kpkgs );
        kBase.addKnowledgePackages(kpkgs);

        InternalFactHandle stilton2 = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                       10 ) );
        InternalFactHandle brie2 = (InternalFactHandle) session.insert( new Cheese( "brie",
                                                                                    10 ) );
        InternalFactHandle bob = (InternalFactHandle) session.insert( new Person( "bob",
                                                                                  30 ) );
        session.fireAllRules();

        assertEquals( list.size(),
                      3 );
        assertEquals( bob.getObject(),
                      list.get( 2 ) );
        assertEquals( "stilton",
                      list.get( 1 ) );

        session.dispose();

    }

    @Test
    public void testSerializationOfIndexedWM() throws Exception {
        Collection<KnowledgePackage>  kpkgs = loadKnowledgePackages("../test_Serializable2.drl" );
        kpkgs = SerializationHelper.serializeObject( kpkgs );
        KnowledgeBase kBase = getKnowledgeBase();
        kBase.addKnowledgePackages(kpkgs);
        kBase = SerializationHelper.serializeObject( kBase );

        Map map = new HashMap();
        map.put( "x",
                 kBase );
        map = SerializationHelper.serializeObject( map );
        kBase = (KnowledgeBase) map.get( "x" );
        org.kie.api.definition.rule.Rule[] rules = (org.kie.api.definition.rule.Rule[]) kBase.getKnowledgePackage("org.drools.compiler").getRules().toArray(new org.kie.api.definition.rule.Rule[0] );
        assertEquals( 3, rules.length );

        StatefulKnowledgeSession session = kBase.newStatefulKnowledgeSession();

        session.setGlobal( "list",
                           new ArrayList() );

        final Primitives p = new Primitives();
        p.setBytePrimitive( (byte) 1 );
        p.setShortPrimitive( (short) 2 );
        p.setIntPrimitive( (int) 3 );
        session.insert( p );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        assertEquals( 1,
                      session.getObjects().size() );
        assertEquals( p,
                      IteratorToList.convert( session.getObjects().iterator() ).get( 0 ) );

        assertEquals( 3,
                      ((InternalAgenda) session.getAgenda()).agendaSize() );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );
        session.fireAllRules();

        final List list = (List) session.getGlobal( "list" );

        assertEquals( 3,
                      list.size() );
        // because of agenda-groups
        assertEquals( "1",
                      list.get( 0 ) );
        assertEquals( "2",
                      list.get( 1 ) );
        assertEquals( "3",
                      list.get( 2 ) );

    }

    /*
     *  Here I am inserting data points which are not used by any rule (e.g Person).
     *  Later adding rule (e.g. Rule: 'match Person') for those data points.
     *
     *  Result: Pkg/Rule addition is failing with ClassCastException
     */
    @Test
    public void testSerializeAdd2() throws Exception {
        //Create a rulebase, a session, and test it
        Collection<KnowledgePackage>  kpkgs = loadKnowledgePackages("../test_Dynamic1_0.drl" );
        kpkgs = SerializationHelper.serializeObject( kpkgs );
        KnowledgeBase kBase = getKnowledgeBase();
        kBase.addKnowledgePackages(kpkgs);
        kBase = SerializationHelper.serializeObject( kBase );

        List results = new ArrayList();
        StatefulKnowledgeSession session = kBase.newStatefulKnowledgeSession();
        session.setGlobal( "results",
                           results );

        InternalFactHandle stilton1 = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                       10 ) );
        session.insert( new Cheese( "brie",
                                    10 ) );
        InternalFactHandle bob = (InternalFactHandle) session.insert( new Person( "bob",
                                                                                  10 ) );

        // fire rules
        session.fireAllRules();
        // check the results are correct
        assertEquals( 1,
                      results.size() );
        assertEquals( stilton1.getObject(),
                      results.get( 0 ) );


        // serialize session and rulebase
        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        // dynamically add a new package
        kpkgs = loadKnowledgePackages("../test_Dynamic3_0.drl" );
        kBase.addKnowledgePackages(SerializationHelper.serializeObject( kpkgs ));
        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);

        InternalFactHandle stilton2 = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                       20 ) );
        session.insert( new Cheese( "brie",
                                    20 ) );
        InternalFactHandle mark = (InternalFactHandle) session.insert( new Person( "mark",
                                                                                   20 ) );
        session.fireAllRules();

        results = (List) session.getGlobal( "results" );
        assertEquals( 4,
                      results.size() );

        assertEquals( stilton2.getObject(),
                      results.get( 1 ) );

        assertEquals( bob.getObject(),
                      results.get( 2 ) );

        assertEquals( mark.getObject(),
                      results.get( 3 ) );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );
        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );

        // dispose session
        session.dispose();

    }

    /*
     *  Here I am inserting data points(e.g. Cheese) with  'stilton' / 'brie' as type value.
     *  Initially I had only 1 rule ('like stilton') for data points(e.g. Cheese) having type as 'stilton'.
     *
     *  Later added new rule ('like brie')  for data points(e.g. Cheese) having type as 'brie'.
     *
     *  Result: new rule is not getting fired for new data points having type as 'brie'.
     *          Only for old data points having type as 'brie' the new rule got fired.
     */
    @Test
    public void testSerializeAdd_newRuleNotFiredForNewData() throws Exception {
        //Create a rulebase, a session, and test it
        Collection<KnowledgePackage>  kpkgs = loadKnowledgePackages("../test_Dynamic1_0.drl" );
        kpkgs = SerializationHelper.serializeObject( kpkgs );
        KnowledgeBase kBase = getKnowledgeBase();
        kBase.addKnowledgePackages(kpkgs);
        kBase = SerializationHelper.serializeObject( kBase );

        List results = new ArrayList();
        StatefulKnowledgeSession session = kBase.newStatefulKnowledgeSession();
        session.setGlobal( "results",
                           results );

        InternalFactHandle stilton1 = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                       10 ) );
        InternalFactHandle brie1 = (InternalFactHandle) session.insert( new Cheese( "brie",
                                                                                    10 ) );
        session.fireAllRules();

        // serialize session and rulebase
        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        // dynamically add a new package
        kpkgs = loadKnowledgePackages("../test_Dynamic1_1.drl" );
        kBase.addKnowledgePackages(SerializationHelper.serializeObject( kpkgs ));
        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, kBase, true);

        InternalFactHandle stilton2 = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                       20 ) );
        InternalFactHandle brie2 = (InternalFactHandle) session.insert( new Cheese( "brie",
                                                                                    20 ) );
        InternalFactHandle brie3 = (InternalFactHandle) session.insert( new Cheese( "brie",
                                                                                    30 ) );
        session.fireAllRules();
        assertEquals( 5,
                      results.size() );

        assertEquals( stilton2.getObject(),
                      results.get( 1 ) );

        assertEquals( brie1.getObject(),
                      results.get( 2 ) );
        assertEquals( brie3.getObject(),
                      results.get( 3 ) );

        assertEquals( brie2.getObject(),
                      results.get( 4 ) );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );
        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );

        session.dispose();
    }

    /*
     *  Works Fine if both the scenarios mentioned above are skipped.
     */
    @Test
    public void testSerializeAdd3() throws Exception {
        //Create a rulebase, a session, and test it
        Collection<KnowledgePackage>  kpkgs = loadKnowledgePackages("../test_Dynamic1_0.drl" );
        kpkgs = SerializationHelper.serializeObject( kpkgs );
        KnowledgeBase kBase = getKnowledgeBase();
        kBase.addKnowledgePackages(kpkgs);
        kBase = SerializationHelper.serializeObject( kBase );

        List results = new ArrayList();
        StatefulKnowledgeSession session = kBase.newStatefulKnowledgeSession();
        session.setGlobal( "results",
                           results );


        InternalFactHandle stilton1 = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                       10 ) );
        InternalFactHandle brie1 = (InternalFactHandle) session.insert( new Cheese( "brie",
                                                                                    10 ) );
        session.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( stilton1.getObject(),
                      results.get( 0 ) );

        // serialize session and rulebase
        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        // dynamic add pkgs
        kpkgs = loadKnowledgePackages("../test_Dynamic3_0.drl" );
        kBase.addKnowledgePackages(SerializationHelper.serializeObject( kpkgs ));
        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        InternalFactHandle stilton2 = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                       20 ) );
        InternalFactHandle brie2 = (InternalFactHandle) session.insert( new Cheese( "brie",
                                                                                    20 ) );
        InternalFactHandle bob1 = (InternalFactHandle) session.insert( new Person( "bob",
                                                                                   20 ) );
        InternalFactHandle bob2 = (InternalFactHandle) session.insert( new Person( "bob",
                                                                                   30 ) );
        session.fireAllRules();

        assertEquals( 4,
                      results.size() );
        assertEquals( stilton2.getObject(),
                      results.get( 1 ) );
        assertEquals( bob2.getObject(),
                      results.get( 2 ) );
        assertEquals( bob1.getObject(),
                      results.get( 3 ) );

        // serialize session and rulebase
        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        // dynamic add pkgs
        kpkgs = loadKnowledgePackages("../test_Dynamic1_2.drl" );
        kBase.addKnowledgePackages(SerializationHelper.serializeObject( kpkgs ));
        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        InternalFactHandle stilton3 = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                       40 ) );
        InternalFactHandle brie3 = (InternalFactHandle) session.insert( new Cheese( "brie",
                                                                                    40 ) );
        InternalFactHandle bob3 = (InternalFactHandle) session.insert( new Person( "bob",
                                                                                   40 ) );
        InternalFactHandle bob4 = (InternalFactHandle) session.insert( new Person( "bob",
                                                                                   40 ) );
        InternalFactHandle addr1 = (InternalFactHandle) session.insert( new Address( "bangalore" ) );
        InternalFactHandle addr2 = (InternalFactHandle) session.insert( new Address( "India" ) );

        session.fireAllRules();

        assertEquals( 9,
                      results.size() );
        assertEquals( stilton3.getObject(),
                      results.get( 4 ) );
        assertEquals( bob4.getObject(),
                      results.get( 5 ) );
        assertEquals( bob3.getObject(),
                      results.get( 6 ) );
        assertEquals( addr2.getObject(),
                      results.get( 7 ) );
        assertEquals( addr1.getObject(),
                      results.get( 8 ) );

        // serialize session and rulebase
        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        InternalFactHandle stilton4 = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                       50 ) );
        InternalFactHandle brie4 = (InternalFactHandle) session.insert( new Cheese( "brie",
                                                                                    50 ) );
        InternalFactHandle bob5 = (InternalFactHandle) session.insert( new Person( "bob",
                                                                                   50 ) );
        InternalFactHandle bob6 = (InternalFactHandle) session.insert( new Person( "bob",
                                                                                   50 ) );
        InternalFactHandle addr3 = (InternalFactHandle) session.insert( new Address( "Tripura" ) );
        InternalFactHandle addr4 = (InternalFactHandle) session.insert( new Address( "Agartala" ) );

        session.fireAllRules();

        assertEquals( 14,
                      results.size() );
        assertEquals( stilton4.getObject(),
                      results.get( 9 ) );
        assertEquals( bob6.getObject(),
                      results.get( 10 ) );
        assertEquals( bob5.getObject(),
                      results.get( 11 ) );
        assertEquals( addr4.getObject(),
                      results.get( 12 ) );
        assertEquals( addr3.getObject(),
                      results.get( 13 ) );

        // serialize session and rulebase
        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        session.dispose();
    }

    /*
     * I have tried both the scenarios
     * 1. Remove a rule from a pkg.
     * 2. Remove a pkg
     *
     * But both cases after inserting associated data points (i.e data points which are used to fire/activate the removed rule)
     * session.fireAllRules() is throwing NoClassDefFoundError
     */
    @Test
    public void testSerializeAddRemove_NoClassDefFoundError() throws Exception {

        //Create a rulebase, a session, and test it
        Collection<KnowledgePackage>  kpkgs = loadKnowledgePackages("../test_Dynamic1_0.drl" );
        kpkgs = SerializationHelper.serializeObject( kpkgs );
        KnowledgeBase kBase = getKnowledgeBase();
        kBase.addKnowledgePackages(kpkgs);
        kBase = SerializationHelper.serializeObject( kBase );

        List results = new ArrayList();
        StatefulKnowledgeSession session = kBase.newStatefulKnowledgeSession();
        session.setGlobal( "results",
                           results );

        InternalFactHandle stilton1 = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                       10 ) );
        InternalFactHandle brie1 = (InternalFactHandle) session.insert( new Cheese( "brie",
                                                                                    10 ) );
        session.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( stilton1.getObject(),
                      results.get( 0 ) );

        // serialize session and rulebase
        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        // dynamic add pkgs
        kpkgs = loadKnowledgePackages("../test_Dynamic3_0.drl" );
        kBase.addKnowledgePackages(SerializationHelper.serializeObject( kpkgs ));
        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        results.clear();

        InternalFactHandle stilton2 = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                       20 ) );
        InternalFactHandle brie2 = (InternalFactHandle) session.insert( new Cheese( "brie",
                                                                                    20 ) );
        InternalFactHandle bob1 = (InternalFactHandle) session.insert( new Person( "bob",
                                                                                   20 ) );
        InternalFactHandle bob2 = (InternalFactHandle) session.insert( new Person( "bob",
                                                                                   30 ) );
        session.fireAllRules();

        assertEquals( 3,
                      results.size() );
        assertEquals( stilton2.getObject(),
                      results.get( 0 ) );
        assertEquals( bob2.getObject(),
                      results.get( 1 ) );
        assertEquals( bob1.getObject(),
                      results.get( 2 ) );

        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );
        results.clear();

        // CASE 1: remove rule
        kBase.removeRule( "org.drools.compiler.test",
                          "like stilton" );

        InternalFactHandle stilton3 = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                       20 ) );
        InternalFactHandle brie3 = (InternalFactHandle) session.insert( new Cheese( "brie",
                                                                                    20 ) );
        InternalFactHandle bob3 = (InternalFactHandle) session.insert( new Person( "bob",
                                                                                   20 ) );
        InternalFactHandle bob4 = (InternalFactHandle) session.insert( new Person( "bob",
                                                                                   30 ) );
        session.fireAllRules();

        assertEquals( 2,
                      results.size() );
        assertEquals( bob4.getObject(),
                      results.get( 0 ) );
        assertEquals( bob3.getObject(),
                      results.get( 1 ) );

        // now recreate the rulebase, deserialize the session and test it
        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );
        results.clear();

        // CASE 2: remove pkg
        kBase.removeKnowledgePackage( "org.drools.compiler.test" );

        InternalFactHandle stilton4 = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                       20 ) );
        InternalFactHandle brie4 = (InternalFactHandle) session.insert( new Cheese( "brie",
                                                                                    20 ) );
        InternalFactHandle bob5 = (InternalFactHandle) session.insert( new Person( "bob",
                                                                                   20 ) );
        InternalFactHandle bob6 = (InternalFactHandle) session.insert( new Person( "bob",
                                                                                   30 ) );
        session.fireAllRules();

        assertEquals( 2,
                      results.size() );
        assertEquals( bob6.getObject(),
                      results.get( 0 ) );
        assertEquals( bob5.getObject(),
                      results.get( 1 ) );

        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );
        results.clear();

        session.setGlobal( "results",
                           results );

        InternalFactHandle stilton5 = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                       30 ) );
        InternalFactHandle brie5 = (InternalFactHandle) session.insert( new Cheese( "brie",
                                                                                    30 ) );
        InternalFactHandle bob7 = (InternalFactHandle) session.insert( new Person( "bob",
                                                                                   30 ) );
        InternalFactHandle bob8 = (InternalFactHandle) session.insert( new Person( "bob",
                                                                                   40 ) );
        session.fireAllRules();

        assertEquals( 2,
                      results.size() );
        assertEquals( bob8.getObject(),
                      results.get( 0 ) );
        assertEquals( bob7.getObject(),
                      results.get( 1 ) );

        kBase = (KnowledgeBase) SerializationHelper.serializeObject( kBase );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        session.dispose();

    }

    /*
     *  Testing the signature framework
     */
    @Test
    public void testSignedSerialization1() throws Exception {
        try {
            setPrivateKeyProperties();
            setPublicKeyProperties();

            //Compile a package, add it to kbase, serialize both
            Collection<KnowledgePackage>  kpkgs = loadKnowledgePackages("../test_Dynamic1_0.drl" );
            kpkgs = SerializationHelper.serializeObject( kpkgs );
            KnowledgeBase kBase = getKnowledgeBase();
            kBase.addKnowledgePackages(kpkgs);
            kBase = SerializationHelper.serializeObject( kBase );

        } finally {
            unsetPrivateKeyProperties();
            unsetPublicKeyProperties();
        }
    }

    /*
     *  Deserializing a signed package without the proper public key
     *  should fail.
     */
    @Test
    public void testSignedSerialization2() throws Exception {
        try {
            // set only the serialisation properties, but not the deserialization
            setPrivateKeyProperties();

            try {
                // Test package serialization/deserialization
                Collection<KnowledgePackage>  kpkgs = loadKnowledgePackages("../test_Dynamic1_0.drl");
                fail( "Deserialisation should have failed." );
            } catch ( Exception e ) {
                // success
            }
        } finally {
            unsetPrivateKeyProperties();
        }
    }

    /*
     *  Deserializing a signed rulebase without the proper public key
     *  should fail.
     */
    @Test
    public void testSignedSerialization3() throws Exception {
        try {
            // set only the serialisation properties, but not the deserialization
            setPrivateKeyProperties();

            // create the kpkgs, but do not let them serialize
            Collection<KnowledgePackage>  kpkgs = loadKnowledgePackages(null, false, "../test_Dynamic1_0.drl" );
            KnowledgeBase kBase = getKnowledgeBase();
            kBase.addKnowledgePackages(kpkgs);

            try {
                kBase = SerializationHelper.serializeObject( kBase );
                fail( "Deserialisation should have failed." );
            } catch ( Exception e ) {
                // success
            }
        } finally {
            unsetPrivateKeyProperties();
        }
    }

    /*
     *  A client environment configured to use signed serialization
     *  should refuse any non-signed serialized rulebase
     */
    @Test
    public void testSignedSerialization4() throws Exception {

        Collection<KnowledgePackage>  kpkgs = loadKnowledgePackages(null, false, "../test_Dynamic1_0.drl" );
        kpkgs = SerializationHelper.serializeObject( kpkgs );
        KnowledgeBase kBase = getKnowledgeBase();
        kBase.addKnowledgePackages(kpkgs);
        kBase = SerializationHelper.serializeObject( kBase );

        try {
            // set only the deserialisation properties, but not the serialization
            setPublicKeyProperties();
            kBase = SerializationHelper.serializeObject( kBase );
            fail( "Should not deserialize an unsigned rulebase on an environment configured to work with signed rulebases." );
        } catch ( Exception e ) {
            // success
        } finally {
            unsetPublicKeyProperties();
        }
    }

    private void setPublicKeyProperties() {
        // Set the client properties to de-serialise the signed packages
        URL clientKeyStoreURL = getClass().getResource( "droolsClient.keystore" );
        System.setProperty( KeyStoreHelper.PROP_SIGN,
                            "true" );
        System.setProperty( KeyStoreHelper.PROP_PUB_KS_URL,
                            clientKeyStoreURL.toExternalForm() );
        System.setProperty( KeyStoreHelper.PROP_PUB_KS_PWD,
                            "clientpwd" );
    }

    private void unsetPublicKeyProperties() {
        // Un-set the client properties to de-serialise the signed packages
        System.setProperty( KeyStoreHelper.PROP_SIGN,
                            "" );
        System.setProperty( KeyStoreHelper.PROP_PUB_KS_URL,
                            "" );
        System.setProperty( KeyStoreHelper.PROP_PUB_KS_PWD,
                            "" );
    }

    private void setPrivateKeyProperties() {
        // Set the server properties to serialise the signed packages
        URL serverKeyStoreURL = getClass().getResource( "droolsServer.keystore" );
        System.setProperty( KeyStoreHelper.PROP_SIGN,
                            "true" );
        System.setProperty( KeyStoreHelper.PROP_PVT_KS_URL,
                            serverKeyStoreURL.toExternalForm() );
        System.setProperty( KeyStoreHelper.PROP_PVT_KS_PWD,
                            "serverpwd" );
        System.setProperty( KeyStoreHelper.PROP_PVT_ALIAS,
                            "droolsKey" );
        System.setProperty( KeyStoreHelper.PROP_PVT_PWD,
                            "keypwd" );
    }

    private void unsetPrivateKeyProperties() {
        // Un-set the server properties to serialise the signed packages
        System.setProperty( KeyStoreHelper.PROP_SIGN,
                            "" );
        System.setProperty( KeyStoreHelper.PROP_PVT_KS_URL,
                            "" );
        System.setProperty( KeyStoreHelper.PROP_PVT_KS_PWD,
                            "" );
        System.setProperty( KeyStoreHelper.PROP_PVT_ALIAS,
                            "" );
        System.setProperty( KeyStoreHelper.PROP_PVT_PWD,
                            "" );
    }

    /**
     * In this case we are dealing with facts which are not on the systems classpath.
     */
    @Test
    public void testSerializabilityWithJarFacts() throws Exception {
        MapBackedClassLoader loader = new MapBackedClassLoader( this.getClass().getClassLoader() );

        JarInputStream jis = new JarInputStream( this.getClass().getResourceAsStream( "/billasurf.jar" ) );

        JarEntry entry = null;
        byte[] buf = new byte[1024];
        int len = 0;
        while ( (entry = jis.getNextJarEntry()) != null ) {
            if ( !entry.isDirectory() ) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while ( (len = jis.read( buf )) >= 0 ) {
                    out.write( buf,
                               0,
                               len );
                }
                loader.addResource( entry.getName(),
                                    out.toByteArray() );
            }
        }

        String drl = "package foo.bar \n" +
                     "import com.billasurf.Board\n" +
                     "rule 'MyGoodRule' \n dialect 'mvel' \n when " +
                     "   Board() " +
                     "then \n" +
                     " System.err.println(42); \n" +
                     "end\n";


        KnowledgeBuilderConfiguration kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, loader);

        Collection<KnowledgePackage>  kpkgs = loadKnowledgePackagesFromString(kbuilderConf, drl);

        kpkgs = SerializationHelper.serializeObject( kpkgs, loader );

    }

    @Test
    public void testEmptyRule() throws Exception {
        String rule = "package org.drools.compiler.test;\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "when\n";
        rule += "then\n";
        rule += "    list.add( \"fired\" );\n";
        rule += "end";

        KnowledgeBase kBase = loadKnowledgeBaseFromString( rule );

        // Make sure the rete node map is created correctly
        Map<Integer, BaseNode> nodes = RuleBaseNodes.getNodeMap((InternalKnowledgeBase) kBase);
        assertEquals( 2,
                      nodes.size() );
        assertEquals( "InitialFactImpl",
                      ((ClassObjectType) ((ObjectTypeNode) nodes.get( 2 )).getObjectType()).getClassType().getSimpleName() );
        assertEquals( "Rule 1",
                      ((RuleTerminalNode) nodes.get( 4 )).getRule().getName() );

        StatefulKnowledgeSession session = kBase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        session.fireAllRules();

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( "fired",
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

    @Test
    public void testDynamicEmptyRule() throws Exception {
        String rule1 = "package org.drools.compiler.test;\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "    list.add( \"fired1\" );\n";
        rule1 += "end";

        String rule2 = "package org.drools.compiler.test;\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule \"Rule 2\"\n";
        rule2 += "when\n";
        rule2 += "then\n";
        rule2 += "    list.add( \"fired2\" );\n";
        rule2 += "end";

        KnowledgeBase kBase = loadKnowledgeBaseFromString( rule1 );

        // Make sure the rete node map is created correctly
        Map<Integer, BaseNode> nodes = RuleBaseNodes.getNodeMap((InternalKnowledgeBase) kBase);

        // Make sure the rete node map is created correctly
        assertEquals( 2,
                      nodes.size() );
        assertEquals( "InitialFactImpl",
                      ((ClassObjectType) ((ObjectTypeNode) nodes.get( 2 )).getObjectType()).getClassType().getSimpleName() );
        assertEquals( "Rule 1",
                      ((RuleTerminalNode) nodes.get( 4 )).getRule().getName() );

        StatefulKnowledgeSession session = kBase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );
        StatefulKnowledgeSession session1 = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, false );
        session1.fireAllRules();

        assertEquals( 1,
                      ((List) session1.getGlobal( "list" )).size() );

        StatefulKnowledgeSession session2 = SerializationHelper.getSerialisedStatefulKnowledgeSession( session1, kBase, false );

        session.dispose();
        session1.dispose();

        Collection<KnowledgePackage>  kpkgs = loadKnowledgePackagesFromString( rule2 );
        kBase.addKnowledgePackages( kpkgs );

        session2.fireAllRules();
        System.out.println(session2.getGlobal( "list" ));

        assertEquals( 2,
                      ((List) session2.getGlobal( "list" )).size() );
        assertEquals( "fired1",
                      ((List) session2.getGlobal( "list" )).get( 0 ) );
        assertEquals( "fired2",
                      ((List) session2.getGlobal( "list" )).get( 1 ) );
    }

    @Test
    public void testSinglePattern() throws Exception {
        String rule = "package org.drools.compiler.test;\n";
        rule += "import org.drools.compiler.Person\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "when\n";
        rule += "    $p : Person( ) \n";
        rule += "then\n";
        rule += "    list.add( $p );\n";
        rule += "end";

        KnowledgeBase kBase = loadKnowledgeBaseFromString( rule );

        // Make sure the rete node map is created correctly
        Map<Integer, BaseNode> nodes = RuleBaseNodes.getNodeMap((InternalKnowledgeBase) kBase);
        assertEquals( 3,
                      nodes.size() );
        assertEquals( "Person",
                      ((ClassObjectType) ((ObjectTypeNode) nodes.get( 3 )).getObjectType()).getClassType().getSimpleName() );
        assertEquals( "Rule 1",
                      ((RuleTerminalNode) nodes.get( 5 )).getRule().getName() );

        StatefulKnowledgeSession session = kBase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        Person p = new Person( "bobba fet",
                               32 );
        session.insert( p );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );
        session.fireAllRules();

        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( p,
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

    @Test
    public void testSingleRuleSingleJoinNodePattern() throws Exception {
        String rule = "package org.drools.compiler.test;\n";
        rule += "import org.drools.compiler.Person\n";
        rule += "import org.drools.compiler.Cheese\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "when\n";
        rule += "    $c : Cheese( ) \n";
        rule += "    $p : Person( cheese == $c ) \n";
        rule += "then\n";
        rule += "    list.add( $p );\n";
        rule += "end";

        KnowledgeBase kBase = loadKnowledgeBaseFromString( rule );

        // Make sure the rete node map is created correctly
        Map<Integer, BaseNode> nodes = RuleBaseNodes.getNodeMap( (InternalKnowledgeBase) kBase );

        assertEquals( 5,
                      nodes.size() );
        assertEquals( "Cheese",
                      ((ClassObjectType) ((ObjectTypeNode) nodes.get( 3 )).getObjectType()).getClassType().getSimpleName() );
        assertEquals( "Person",
                      ((ClassObjectType) ((ObjectTypeNode) nodes.get( 5 )).getObjectType()).getClassType().getSimpleName() );
        assertTrue( "Should end with JoinNode",  nodes.get( 6 ).getClass().getSimpleName().endsWith( "JoinNode") );
        assertEquals( "Rule 1",
                      ((RuleTerminalNode) nodes.get( 7 )).getRule().getName() );

        StatefulKnowledgeSession session = kBase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        Cheese stilton = new Cheese( "stilton",
                                     25 );
        Cheese brie = new Cheese( "brie",
                                  49 );
        Person bobba = new Person( "bobba fet",
                                   32 );
        bobba.setCheese( stilton );

        Person vadar = new Person( "darth vadar",
                                   32 );

        session.insert( stilton );
        session.insert( bobba );
        session.insert( vadar );
        session.insert( brie );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        session.fireAllRules();

        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( bobba,
                      ((List) session.getGlobal( "list" )).get( 0 ) );

        Person c3po = new Person( "c3p0",
                                  32 );
        c3po.setCheese( stilton );
        session.insert( c3po );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        session.fireAllRules();

        assertEquals( 2,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( c3po,
                      ((List) session.getGlobal( "list" )).get( 1 ) );

        Person r2d2 = new Person( "r2d2",
                                  32 );
        r2d2.setCheese( brie );
        session.insert( r2d2 );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        session.fireAllRules();

        assertEquals( 3,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( r2d2,
                      ((List) session.getGlobal( "list" )).get( 2 ) );
    }

    @Test
    public void testMultiRuleMultiJoinNodePatternsWithHalt() throws Exception {
        String rule1 = "package org.drools.compiler.test;\n";
        rule1 += "import org.drools.compiler.Person\n";
        rule1 += "import org.drools.compiler.Cheese\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "when\n";
        rule1 += "    $c : Cheese( ) \n";
        rule1 += "    $p : Person( cheese == $c ) \n";
        rule1 += "then\n";
        rule1 += "    list.add( $p );\n";
        rule1 += "end";

        String rule2 = "package org.drools.compiler.test;\n";
        rule2 += "import org.drools.compiler.Person\n";
        rule2 += "import org.drools.compiler.Cheese\n";
        rule2 += "import org.drools.compiler.Cell\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule \"Rule 2\"\n";
        rule2 += "when\n";
        rule2 += "    $c : Cheese( ) \n";
        rule2 += "    $p : Person( cheese == $c ) \n";
        rule2 += "    $x : Cell( value == $p.age ) \n";
        rule2 += "then\n";
        rule2 += "    list.add( $x );\n";
        rule2 += "end";

        String rule3 = "package org.drools.compiler.test;\n";
        rule3 += "import org.drools.compiler.FactA\n";
        rule3 += "import org.drools.compiler.FactB\n";
        rule3 += "import org.drools.compiler.FactC\n";
        rule3 += "import org.drools.compiler.Person\n";
        rule3 += "global java.util.List list\n";
        rule3 += "rule \"Rule 3\"\n";
        rule3 += "when\n";
        rule3 += "    $a : FactA( field2 > 10 ) \n";
        rule3 += "    $b : FactB( f2 >= $a.field2 ) \n";
        rule3 += "    $p : Person( name == \"darth vadar\" ) \n";
        rule3 += "    $c : FactC( f2 >= $b.f2 ) \n";
        rule3 += "then\n";
        rule3 += "    list.add( $c );\n";
        rule3 += "    drools.halt();\n";
        rule3 += "end";

        KnowledgeBase kBase = loadKnowledgeBaseFromString( rule1, rule2, rule3 );


        StatefulKnowledgeSession session = kBase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        Cheese stilton = new Cheese( "stilton",
                                     25 );
        Cheese brie = new Cheese( "brie",
                                  49 );
        Person bobba = new Person( "bobba fet",
                                   30 );
        bobba.setCheese( stilton );
        Person vadar = new Person( "darth vadar",
                                   38 );
        Person c3po = new Person( "c3p0",
                                  17 );
        c3po.setCheese( stilton );
        Person r2d2 = new Person( "r2d2",
                                  58 );
        r2d2.setCheese( brie );

        session.insert( stilton );
        session.insert( bobba );
        session.insert( vadar );
        session.insert( brie );
        session.insert( c3po );
        session.insert( r2d2 );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );
        session.fireAllRules();

        list = (List) session.getGlobal( "list" );
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( r2d2 ) );
        assertTrue( list.contains( c3po ) );
        assertTrue( list.contains( bobba ) );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        Cell cell30 = new Cell( 30 );
        session.insert( cell30 );
        Cell cell58 = new Cell( 58 );
        session.insert( cell58 );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        session.fireAllRules();

        assertEquals( 5,
                      list.size() );
        assertTrue( list.contains( cell30 ) );
        assertTrue( list.contains( cell58 ) );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        session.insert( new FactA( 15 ) );
        session.insert( new FactB( 20 ) );
        FactC factC27 = new FactC( 27 );
        session.insert( factC27 );
        FactC factC52 = new FactC( 52 );
        session.insert( factC52 );

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession( session, kBase, true );

        session.fireAllRules();
        session.fireAllRules();

        assertEquals( 7,
                      list.size() );
        assertTrue( list.contains( factC52 ) );
        assertTrue( list.contains( factC27 ) );
    }

    @Test
    public void testNot() throws Exception {
        String header = "package org.drools.compiler.test;\n";
        header += "import java.util.List;\n";
        header += "import org.drools.compiler.Person\n";
        header += "import org.drools.compiler.Cheese\n";
        header += "global java.util.List list;\n";

        String rule1 = "rule \"not rule test\"\n";
        rule1 += "salience 10\n";
        rule1 += "when\n";
        rule1 += "    Person()\n";
        rule1 += "    not Cheese( price >= 5 )\n";
        rule1 += "then\n";
        rule1 += "    list.add( new Integer( 5 ) );\n";
        rule1 += "end\n";

        KnowledgeBase kBase = loadKnowledgeBaseFromString( header + rule1 );

        Environment env = EnvironmentFactory.newEnvironment();
        env.set( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{
                new IdentityPlaceholderResolverStrategy( ClassObjectMarshallingStrategyAcceptor.DEFAULT )} );

        StatefulKnowledgeSession ksession = kBase.newStatefulKnowledgeSession( null, env );

        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        // add a person, no cheese
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        Person bobba = new Person( "bobba fet",
                                   50 );
        ksession.insert( bobba );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );

        // add another person, no cheese
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        Person darth = new Person( "darth vadar",
                                   200 );
        ksession.insert( darth );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );

        // add cheese
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        Cheese stilton = new Cheese( "stilton",
                                     5 );
        ksession.insert( stilton );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );

        // remove cheese
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.retract( ksession.getFactHandle( stilton ) );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );

        // put 2 cheeses back in
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.insert( stilton );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        Cheese brie = new Cheese( "brie",
                                  18 );
        ksession.insert( brie );
        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );

        // now remove a cheese, should be no change
        ksession.retract( ksession.getFactHandle( stilton ) );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );

        // now remove a person, should be no change
        ksession.retract( ksession.getFactHandle( bobba ) );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );

        //removal remaining cheese, should increase by one, as one person left
        ksession.retract( ksession.getFactHandle( brie ) );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.fireAllRules();
        assertEquals( 5,
                      list.size() );
    }

    @Test
    public void testExists() throws Exception {
        String header = "package org.drools.compiler.test;\n";
        header += "import java.util.List;\n";
        header += "import org.drools.compiler.Person\n";
        header += "import org.drools.compiler.Cheese\n";
        header += "global java.util.List list;\n";

        String rule1 = "rule \"not rule test\"\n";
        rule1 += "salience 10\n";
        rule1 += "when\n";
        rule1 += "    Person()\n";
        rule1 += "    exists Cheese( price >= 5 )\n";
        rule1 += "then\n";
        rule1 += "    list.add( new Integer( 5 ) );\n";
        rule1 += "end\n";

        KnowledgeBase kBase = loadKnowledgeBaseFromString( header + rule1 );

        Environment env = EnvironmentFactory.newEnvironment();
        env.set( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{
                 new IdentityPlaceholderResolverStrategy( ClassObjectMarshallingStrategyAcceptor.DEFAULT )} );


        StatefulKnowledgeSession ksession = kBase.newStatefulKnowledgeSession( null, env );
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        // add a person, no cheese
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        Person bobba = new Person( "bobba fet",
                                   50 );
        ksession.insert( bobba );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.fireAllRules();
        assertEquals( 0,
                      list.size() );

        // add another person, no cheese
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        Person darth = new Person( "darth vadar",
                                   200 );
        ksession.insert( darth );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.fireAllRules();
        assertEquals( 0,
                      list.size() );

        // add cheese
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        Cheese stilton = new Cheese( "stilton",
                                     5 );
        ksession.insert( stilton );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );

        // remove cheese
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.retract( ksession.getFactHandle( stilton ) );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );

        // put 2 cheeses back in
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.insert( stilton );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        Cheese brie = new Cheese( "brie",
                                  18 );
        ksession.insert( brie );
        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );

        // now remove a cheese, should be no change
        ksession.retract( ksession.getFactHandle( stilton ) );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );

        // now remove a person, should be no change
        ksession.retract( ksession.getFactHandle( bobba ) );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );

        //removal remaining cheese, no
        ksession.retract( ksession.getFactHandle( brie ) );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );

        // put one cheese back in, with one person should increase by one
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.insert( stilton );
        ksession.fireAllRules();
        assertEquals( 5,
                      list.size() );
    }

    @Test
    public void testTruthMaintenance() throws Exception {
        String header = "package org.drools.compiler.test;\n";
        header += "import java.util.List;\n";
        header += "import org.drools.compiler.Person\n";
        header += "import org.drools.compiler.Cheese\n";
        header += "global Cheese cheese;\n";
        header += "global Person person;\n";
        header += "global java.util.List list;\n";

        String rule1 = "rule \"not person then cheese\"\n";
        rule1 += "when \n";
        rule1 += "    not Person() \n";
        rule1 += "then \n";
        rule1 += "    if (list.size() < 3) { \n";
        rule1 += "        list.add(new Integer(0)); \n";
        rule1 += "        insertLogical( cheese ); \n" +
                 "    }\n";
        rule1 += "    drools.halt();\n" +
                 "end\n";

        String rule2 = "rule \"if cheese then person\"\n";
        rule2 += "when\n";
        rule2 += "    Cheese()\n";
        rule2 += "then\n";
        rule2 += "    if (list.size() < 3) {\n";
        rule2 += "        list.add(new Integer(0));\n";
        rule2 += "        insertLogical( person );\n";
        rule2 += "    }\n" +
                 "    drools.halt();\n";
        rule2 += "end\n";


        KnowledgeBase kBase = loadKnowledgeBaseFromString( header + rule1 + rule2 );

        StatefulKnowledgeSession ksession = kBase.newStatefulKnowledgeSession( );

        final List list = new ArrayList();

        final Person person = new Person( "person" );
        final Cheese cheese = new Cheese( "cheese",
                                          0 );
        ksession.setGlobal( "cheese",
                           cheese );
        ksession.setGlobal( "person",
                           person );
        ksession.setGlobal( "list",
                           list );
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );

        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );

        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.fireAllRules();
        assertEquals( 3,
                      list.size() );

        // should not grow any further
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );
        ksession.fireAllRules();
        assertEquals( 3,
                      list.size() );
    }

    @Test
    public void testActivationGroups() throws Exception {
        String rule1 = "package org.drools.compiler.test;\n";
        rule1 += "import org.drools.compiler.Cheese\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "    activation-group \"activation-group-1\"\n";
        rule1 += "when\n";
        rule1 += "    $c : Cheese( ) \n";
        rule1 += "then\n";
        rule1 += "    list.add( \"rule1\" );\n";
        rule1 += "    drools.halt();\n";
        rule1 += "end";

        String rule2 = "package org.drools.compiler.test;\n";
        rule2 += "import org.drools.compiler.Cheese\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule \"Rule 2\"\n";
        rule2 += "    salience 10\n";
        rule2 += "    activation-group \"activation-group-1\"\n";
        rule2 += "when\n";
        rule2 += "    $c : Cheese( ) \n";
        rule2 += "then\n";
        rule2 += "    list.add( \"rule2\" );\n";
        rule2 += "    drools.halt();\n";
        rule2 += "end";

        String rule3 = "package org.drools.compiler.test;\n";
        rule3 += "import org.drools.compiler.Cheese\n";
        rule3 += "global java.util.List list\n";
        rule3 += "rule \"Rule 3\"\n";
        rule3 += "    activation-group \"activation-group-1\"\n";
        rule3 += "when\n";
        rule3 += "    $c : Cheese( ) \n";
        rule3 += "then\n";
        rule3 += "    list.add( \"rule3\" );\n";
        rule3 += "    drools.halt();\n";
        rule3 += "end";

        String rule4 = "package org.drools.compiler.test;\n";
        rule4 += "import org.drools.compiler.Cheese\n";
        rule4 += "global java.util.List list\n";
        rule4 += "rule \"Rule 4\"\n";
        rule4 += "    activation-group \"activation-group-2\"\n";
        rule4 += "when\n";
        rule4 += "    $c : Cheese( ) \n";
        rule4 += "then\n";
        rule4 += "    list.add( \"rule4\" );\n";
        rule4 += "    drools.halt();\n";
        rule4 += "end";

        KnowledgeBase kBase = loadKnowledgeBaseFromString( rule1, rule2, rule3, rule4);
        StatefulKnowledgeSession ksession = kBase.newStatefulKnowledgeSession( );

        kBase = SerializationHelper.serializeObject( kBase );
        ksession = getSerialisedStatefulKnowledgeSession( ksession, true );

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                           list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        ksession.insert( brie );

        ksession = getSerialisedStatefulKnowledgeSession( ksession, true );
        ksession.fireAllRules();

        ksession = getSerialisedStatefulKnowledgeSession( ksession, true );
        ksession.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertEquals( "rule2",
                      list.get( 0 ) );
        assertEquals( "rule4",
                      list.get( 1 ) );
    }

    @Test
    public void testAgendaGroups() throws Exception {
        String rule1 = "package org.drools.compiler.test;\n";
        rule1 += "import org.drools.compiler.Cheese\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "    agenda-group \"agenda-group-1\"\n";
        rule1 += "when\n";
        rule1 += "    $c : Cheese( ) \n";
        rule1 += "then\n";
        rule1 += "    list.add( \"rule1\" );\n";
        rule1 += "    drools.halt();\n";
        rule1 += "end";

        String rule2 = "package org.drools.compiler.test;\n";
        rule2 += "import org.drools.compiler.Cheese\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule \"Rule 2\"\n";
        rule2 += "    salience 10\n";
        rule2 += "    agenda-group \"agenda-group-1\"\n";
        rule2 += "when\n";
        rule2 += "    $c : Cheese( ) \n";
        rule2 += "then\n";
        rule2 += "    list.add( \"rule2\" );\n";
        rule2 += "    drools.halt();\n";
        rule2 += "end";

        String rule3 = "package org.drools.compiler.test;\n";
        rule3 += "import org.drools.compiler.Cheese\n";
        rule3 += "global java.util.List list\n";
        rule3 += "rule \"Rule 3\"\n";
        rule3 += "    salience 10\n";
        rule3 += "    agenda-group \"agenda-group-2\"\n";
        rule3 += "    activation-group \"activation-group-2\"\n";
        rule3 += "when\n";
        rule3 += "    $c : Cheese( ) \n";
        rule3 += "then\n";
        rule3 += "    list.add( \"rule3\" );\n";
        rule3 += "    drools.halt();\n";
        rule3 += "end";

        String rule4 = "package org.drools.compiler.test;\n";
        rule4 += "import org.drools.compiler.Cheese\n";
        rule4 += "global java.util.List list\n";
        rule4 += "rule \"Rule 4\"\n";
        rule4 += "    agenda-group \"agenda-group-2\"\n";
        rule4 += "    activation-group \"activation-group-2\"\n";
        rule4 += "when\n";
        rule4 += "    $c : Cheese( ) \n";
        rule4 += "then\n";
        rule4 += "    list.add( \"rule4\" );\n";
        rule4 += "    drools.halt();\n";
        rule4 += "end";

        KnowledgeBase kBase = loadKnowledgeBaseFromString( rule1, rule2, rule3, rule4);
        StatefulKnowledgeSession ksession = kBase.newStatefulKnowledgeSession( );

        kBase = SerializationHelper.serializeObject( kBase );
        ksession = getSerialisedStatefulKnowledgeSession( ksession, true );

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                           list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        ksession.insert( brie );

        ksession = getSerialisedStatefulKnowledgeSession( ksession, true );
        ksession.getAgenda().getAgendaGroup("agenda-group-1" ).setFocus( );
        ksession = getSerialisedStatefulKnowledgeSession( ksession, true );
        ksession.fireAllRules();
        assertEquals( "rule2",
                      list.get( 0 ) );

        ksession = getSerialisedStatefulKnowledgeSession( ksession, true );
        ksession.getAgenda().getAgendaGroup("agenda-group-2" ).setFocus( );
        ksession = getSerialisedStatefulKnowledgeSession( ksession, true );
        ksession.fireAllRules();
        assertEquals( "rule3",
                      list.get( 1 ) );

        ksession = getSerialisedStatefulKnowledgeSession( ksession, true );
        ksession.fireAllRules();
        assertEquals( "rule1",
                      list.get( 2 ) );
    }

    @Test
    public void testRuleFlowGroups() throws Exception {
        String rule1 = "package org.drools.compiler.test;\n";
        rule1 += "import org.drools.compiler.Cheese\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "    ruleflow-group \"ruleflow-group-1\"\n";
        rule1 += "when\n";
        rule1 += "    $c : Cheese( ) \n";
        rule1 += "then\n";
        rule1 += "    list.add( \"rule1\" );\n";
        rule1 += "    drools.halt();\n";
        rule1 += "end";

        String rule2 = "package org.drools.compiler.test;\n";
        rule2 += "import org.drools.compiler.Cheese\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule \"Rule 2\"\n";
        rule2 += "    salience 10\n";
        rule2 += "    ruleflow-group \"ruleflow-group-1\"\n";
        rule2 += "when\n";
        rule2 += "    $c : Cheese( ) \n";
        rule2 += "then\n";
        rule2 += "    list.add( \"rule2\" );\n";
        rule2 += "    drools.halt();\n";
        rule2 += "end";

        String rule3 = "package org.drools.compiler.test;\n";
        rule3 += "import org.drools.compiler.Cheese\n";
        rule3 += "global java.util.List list\n";
        rule3 += "rule \"Rule 3\"\n";
        rule3 += "    salience 10\n";
        rule3 += "    ruleflow-group \"ruleflow-group-2\"\n";
        rule3 += "    activation-group \"activation-group-2\"\n";
        rule3 += "when\n";
        rule3 += "    $c : Cheese( ) \n";
        rule3 += "then\n";
        rule3 += "    list.add( \"rule3\" );\n";
        rule3 += "    drools.halt();\n";
        rule3 += "end";

        String rule4 = "package org.drools.compiler.test;\n";
        rule4 += "import org.drools.compiler.Cheese\n";
        rule4 += "global java.util.List list\n";
        rule4 += "rule \"Rule 4\"\n";
        rule4 += "    ruleflow-group \"ruleflow-group-2\"\n";
        rule4 += "    activation-group \"activation-group-2\"\n";
        rule4 += "when\n";
        rule4 += "    $c : Cheese( ) \n";
        rule4 += "then\n";
        rule4 += "    list.add( \"rule4\" );\n";
        rule4 += "    drools.halt();\n";
        rule4 += "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule1, rule2, rule3, rule4 );
        StatefulKnowledgeSession ksession = getSerialisedStatefulKnowledgeSession( kbase.newStatefulKnowledgeSession(), true );

        kbase = SerializationHelper.serializeObject( kbase );
        ksession = getSerialisedStatefulKnowledgeSession( ksession, true );

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                           list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        ksession.insert( brie );

        ksession = getSerialisedStatefulKnowledgeSession( ksession, true  );
        ((InternalAgenda) ksession.getAgenda()).activateRuleFlowGroup( "ruleflow-group-1" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession, true  );
        ksession.fireAllRules();
        assertEquals( "rule2",
                      list.get( 0 ) );

        ksession = getSerialisedStatefulKnowledgeSession( ksession, true  );
        ((InternalAgenda) ksession.getAgenda()).activateRuleFlowGroup( "ruleflow-group-2" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession, true  );
        ksession.fireAllRules();
        assertEquals( "rule3",
                      list.get( 1 ) );

        ksession = getSerialisedStatefulKnowledgeSession( ksession, true  );
        ksession.fireAllRules();
        assertEquals( "rule1",
                      list.get( 2 ) );
    }

    @Test
    public void testAccumulate() throws Exception {
        String rule = "package org.drools\n" +
                "import org.drools.compiler.Message\n" +
                "global java.util.List results\n" +
                "rule MyRule\n" +
                "  when\n" +
                "    $n : Number( intValue >= 2 ) from accumulate ( m: Message( ), count( m ) )\n" +
                "  then\n" +
                "    results.add($n);\n" +
                "end";

        KnowledgeBase kBase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = getSerialisedStatefulKnowledgeSession( kBase.newStatefulKnowledgeSession(), true );

        kBase = SerializationHelper.serializeObject( kBase );
        ksession = getSerialisedStatefulKnowledgeSession( ksession, true );

        ksession.setGlobal( "results",
                           new ArrayList() );

        ksession = getSerialisedStatefulKnowledgeSession( ksession, true );
        ksession.insert( new Message() );
        ksession = getSerialisedStatefulKnowledgeSession( ksession, true );
        List results = (List) ksession.getGlobal( "results" );

        ksession.insert( new Message() );
        ksession.insert( new Message() );
        ksession.fireAllRules();
        assertEquals( 3,
                      ((Number) results.get( 0 )).intValue() );

        ksession = getSerialisedStatefulKnowledgeSession( ksession, true );

        ksession.insert( new Message() );
        ksession.insert( new Message() );
        ksession = getSerialisedStatefulKnowledgeSession( ksession, true );

        assertEquals( 1,
                      ((InternalAgenda) ksession.getAgenda()).agendaSize() );
        ksession.fireAllRules();
        assertEquals( 5,
                      ((Number) results.get( 1 )).intValue() );
    }

    @Test
    public void testAccumulate2() throws Exception {
        String str = "package org.drools\n" + "\n" +
                     "import org.drools.compiler.Message\n" + "\n" +
                     "rule MyRule\n" + "  when\n" +
                     "    Number( intValue >= 5 ) from accumulate ( m: Message( ), count( m ) )\n" +
                     "  then\n" +
                     "    System.out.println(\"Found messages\");\n" +
                     "end\n";

        KnowledgeBase kBase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kBase.newStatefulKnowledgeSession();

        ksession = getSerialisedStatefulKnowledgeSession( ksession, true );
        ksession.insert( new Message() );
        ksession.insert( new Message() );
        ksession.insert( new Message() );
        ksession.insert( new Message() );
        ksession.insert( new Message() );

        assertEquals( 1,
                      ((InternalAgenda) ksession.getAgenda()).agendaSize()  );
    }

    @Test
    public void testAccumulateSessionSerialization() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase("../test_AccumulateSerialization.drl" );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List<Number> results = new ArrayList<Number>();

        ksession.setGlobal( "results",
                            results );

        ksession.insert( new Cheese( "stilton",
                                     10 ) );
        ksession.insert( new Cheese( "brie",
                                     5 ) );
        ksession.insert( new Cheese( "provolone",
                                     150 ) );
        ksession.insert( new Cheese( "brie",
                                     20 ) );
        ksession.insert( new Person( "Bob",
                                     "brie" ) );

        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );

        ksession.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertEquals( 25,
                      results.get( 0 ).intValue() );
    }

    /**
     * test that creates a new knowledge base, new stateful session, inserts new
     * fact, serializes the knowledge base and session and fact using one output
     * stream then deserializes and verifies that fact in the session is the
     * same as fact that was deserialized,
     *
     * from the ObjectOutputStream API: "Multiple references to a single object
     * are encoded using a reference sharing mechanism so that graphs of objects
     * can be restored to the same shape as when the original was written."
     *
     * This is still not fixed, as mentioned in the JIRA
     *
     * JBRULES-2048
     *
     * @throws Exception
     */
    @Test @Ignore
    public void testDroolsObjectOutputInputStream() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase("org/drools/compiler/integrationtests/test_Serializable.drl"  );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        Person bob = new Person();
        session.insert( bob );

        assertSame( "these two object references should be same",
                    bob,
                    session.getObjects().iterator().next() );

        Marshaller marshaller = createSerializableMarshaller( kbase );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new DroolsObjectOutputStream( baos );
        out.writeObject( bob );
        out.writeObject( kbase );
        marshaller.marshall( out,
                             session );
        out.flush();
        out.close();

        ObjectInputStream in = new DroolsObjectInputStream( new ByteArrayInputStream( baos.toByteArray() ) );
        Person deserializedBob = (Person) in.readObject();
        kbase = (KnowledgeBase) in.readObject();
        marshaller = createSerializableMarshaller( kbase );
        session = (StatefulKnowledgeSession) marshaller.unmarshall( in );

        assertSame( "these two object references should be same",
                    deserializedBob,
                    session.getObjects().iterator().next() );
        in.close();
    }

    @Test
    public void testAccumulateSerialization() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "org/drools/compiler/integrationtests/marshalling/test_SerializableAccumulate.drl"  );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.setGlobal( "results",
                            new ArrayList() );

        Cheese t1 = new Cheese( "brie",
                                10 );
        Cheese t2 = new Cheese( "brie",
                                15 );
        Cheese t3 = new Cheese( "stilton",
                                20 );
        Cheese t4 = new Cheese( "brie",
                                30 );

        ksession.insert( t1 );
        ksession.insert( t2 );
        ksession.insert( t3 );
        ksession.insert( t4 );

        //ksession.fireAllRules();
        Marshaller marshaller = createSerializableMarshaller( kbase );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new DroolsObjectOutputStream( baos );
        out.writeObject( kbase );
        marshaller.marshall( out,
                             ksession );
        out.flush();
        out.close();

        ObjectInputStream in = new DroolsObjectInputStream( new ByteArrayInputStream( baos.toByteArray() ) );
        kbase = (KnowledgeBase) in.readObject();
        marshaller = createSerializableMarshaller( kbase );
        ksession = (StatefulKnowledgeSession) marshaller.unmarshall( in );
        in.close();

        // setting the global again, since it is not serialized with the session
        List<List> results = (List<List>) new ArrayList<List>();
        ksession.setGlobal( "results",
                            results );
        assertNotNull( results );

        ksession.fireAllRules();
        ksession.dispose();

        assertEquals( 1,
                      results.size() );
        assertEquals( 3,
                      results.get( 0 ).size() );
    }

    @Test
    public void testMarshallWithNot() throws Exception {
        String str =
                "import " + getClass().getCanonicalName() + ".*\n" +
                        "rule one\n" +
                        "when\n" +
                        "   A()\n" +
                        "   not(B())\n" +
                        "then\n" +
                        "System.out.println(\"a\");\n" +
                        "end\n" +
                        "\n" +
                        "rule two\n" +
                        "when\n" +
                        "   A()\n" +
                        "then\n" +
                        "System.out.println(\"b\");\n" +
                        "end\n";

        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );

        KnowledgeBase kBase = loadKnowledgeBaseFromString(config, str);
        StatefulKnowledgeSession ksession = kBase.newStatefulKnowledgeSession();
        ksession.insert( new A() );
        MarshallerFactory.newMarshaller( kBase ).marshall( new ByteArrayOutputStream(), ksession );
    }

    @Test
    public void testMarshallEvents() throws Exception {
        String str =
                "import " + getClass().getCanonicalName() + ".*\n" +
                        "declare A\n" +
                        " @role( event )\n" +
                        " @expires( 10m )\n" +
                        "end\n" +
                        "declare B\n" +
                        " @role( event )\n" +
                        " @expires( 10m )\n" +
                        "end\n" +
                        "rule one\n" +
                        "when\n" +
                        "   $a : A()\n" +
                        "   B(this after $a)\n" +
                        "then\n" +
                        "insert(new C());" +
                        "end\n";

        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );

        KnowledgeBase kBase = loadKnowledgeBaseFromString(config, str);

        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( "pseudo" ) );
        ksconf.setOption( TimerJobFactoryOption.get("trackable") );
        KieSession ksession = kBase.newStatefulKnowledgeSession( ksconf, null );

        ksession.insert( new A() );

        ksession = marsallStatefulKnowledgeSession( ksession );

        ksession.insert( new B() );

        ksession = marsallStatefulKnowledgeSession( ksession );

        ksession.fireAllRules();
        assertEquals( 2,
                      ksession.getObjects().size() );
    }

    @Test @Ignore("This test is suspicious to say the least...")
    public void testScheduledActivation() {
        KnowledgeBaseImpl knowledgeBase = (KnowledgeBaseImpl) KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgePackageImpl impl = new KnowledgePackageImpl( "test" );

        BuildContext buildContext = new BuildContext( knowledgeBase, knowledgeBase.getReteooBuilder().getIdGenerator() );
        //simple rule that fires after 10 seconds
        final RuleImpl rule = new RuleImpl( "test-rule" );
        new RuleTerminalNode( 1, new MockTupleSource( 2 ), rule, rule.getLhs(), 0, buildContext );

        final List<String> fired = new ArrayList<String>();

        rule.setConsequence( new Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) throws Exception {
                fired.add( "a" );
            }

            public String getName() {
                return "default";
            }
        } );

        rule.setTimer( new DurationTimer( 10000 ) );
        rule.setPackage( "test" );
        impl.addRule( rule );

        knowledgeBase.addKnowledgePackages( Collections.singleton( (KnowledgePackage) impl ) );
        SessionConfiguration config = new SessionConfiguration();
        config.setClockType( ClockType.PSEUDO_CLOCK );
        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession( config, KnowledgeBaseFactory.newEnvironment() );
        PseudoClockScheduler scheduler = (PseudoClockScheduler) ksession.<SessionClock> getSessionClock();
        Marshaller marshaller = MarshallerFactory.newMarshaller( knowledgeBase );

        ksession.insert( "cheese" );
        assertTrue( fired.isEmpty() );
        //marshall, then unmarshall session
        readWrite( knowledgeBase, ksession, config );
        //the activations should fire after 10 seconds
        assertTrue( fired.isEmpty() );
        scheduler.advanceTime( 12, TimeUnit.SECONDS );
        assertFalse( fired.isEmpty() );

    }

    public static class A
            implements
            Serializable {

        @Override
        public String toString() {
            return "A[]";
        }

    }

    public static class B
            implements
            Serializable {
        @Override
        public String toString() {
            return "B[]";
        }
    }

    public static class C
            implements
            Serializable {
        @Override
        public String toString() {
            return "C[]";
        }
    }

    @Test
    public void testMarshallEntryPointsWithExpires() throws Exception {
        String str =
                "package org.domain.test \n" +
                        "import " + getClass().getCanonicalName() + ".*\n" +
                        "global java.util.List list\n" +
                        "declare A\n" +
                        " @role( event )\n" +
                        " @expires( 10s )\n" +
                        "end\n" +
                        "declare B\n" +
                        "" +
                        " @role( event )\n" +
                        " @expires( 10s )\n" +
                        "end\n" +
                        "" +
                        "declare C\n" +
                        " @role( event )\n" +
                        " @expires( 15s )\n" +
                        "end\n" +
                        "" +
                        "rule a1\n" +
                        "when\n" +
                        "   $a : A() from entry-point 'a-ep'\n" +
                        "then\n" +
                        "list.add( $a );" +
                        "end\n" +
                        "" +
                        "rule b1\n" +
                        "when\n" +
                        "   $b : B() from entry-point 'b-ep'\n" +
                        "then\n" +
                        "list.add( $b );" +
                        "end\n" +
                        "" +
                        "rule c1\n" +
                        "when\n" +
                        "   $c : C() from entry-point 'c-ep'\n" +
                        "then\n" +
                        "list.add( $c );" +
                        "end\n";

        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );

        KnowledgeBase kBase = loadKnowledgeBaseFromString(config, str);

        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( "pseudo" ) );
        ksconf.setOption( TimerJobFactoryOption.get("trackable") );
        KieSession ksession = kBase.newStatefulKnowledgeSession( ksconf, null );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        EntryPoint aep = ksession.getEntryPoint( "a-ep" );
        aep.insert( new A() );

        ksession = marsallStatefulKnowledgeSession( ksession );

        EntryPoint bep = ksession.getEntryPoint( "b-ep" );
        bep.insert( new B() );

        ksession = marsallStatefulKnowledgeSession( ksession );

        EntryPoint cep = ksession.getEntryPoint( "c-ep" );
        cep.insert( new C() );

        ksession = marsallStatefulKnowledgeSession( ksession );

        ksession.fireAllRules();

        ksession = marsallStatefulKnowledgeSession( ksession );

        assertEquals( 3,
                      list.size() );

        aep = ksession.getEntryPoint( "a-ep" );
        assertEquals( 1, aep.getFactHandles().size() );

        bep = ksession.getEntryPoint( "b-ep" );
        assertEquals( 1, bep.getFactHandles().size() );

        cep = ksession.getEntryPoint( "c-ep" );
        assertEquals( 1, cep.getFactHandles().size() );

        PseudoClockScheduler timeService = (PseudoClockScheduler) ksession.<SessionClock> getSessionClock();
        timeService.advanceTime( 11, TimeUnit.SECONDS );

        ksession = marsallStatefulKnowledgeSession( ksession );

        ksession.fireAllRules();

        ksession = marsallStatefulKnowledgeSession( ksession );

        aep = ksession.getEntryPoint( "a-ep" );
        assertEquals( 0, aep.getFactHandles().size() );

        bep = ksession.getEntryPoint( "b-ep" );
        assertEquals( 0, bep.getFactHandles().size() );

        cep = ksession.getEntryPoint( "c-ep" );
        assertEquals( 1, cep.getFactHandles().size() );
    }

    @Test
    public void testMarshallEntryPointsWithNot() throws Exception {
        String str =
                "package org.domain.test \n" +
                        "import " + getClass().getCanonicalName() + ".*\n" +
                        "global java.util.List list\n" +
                        "declare A\n" +
                        " @role( event )\n" +
                        " @expires( 10m )\n" +
                        "end\n" +
                        "declare B\n" +
                        "" +
                        " @role( event )\n" +
                        " @expires( 10m )\n" +
                        "end\n" +
                        "" +
                        "rule a1\n" +
                        "when\n" +
                        "   $a : A() from entry-point 'a-ep'\n" +
                        "   not B( this after[0s, 10s] $a) from entry-point 'a-ep'\n" +
                        "then\n" +
                        "list.add( $a );" +
                        "end\n";

        KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );

        KnowledgeBase kBase = loadKnowledgeBaseFromString(config, str);

        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( "pseudo" ) );
        ksconf.setOption( TimerJobFactoryOption.get("trackable") );
        KieSession ksession = kBase.newStatefulKnowledgeSession( ksconf, null );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        EntryPoint aep = ksession.getEntryPoint( "a-ep" );
        aep.insert( new A() );

        ksession = marsallStatefulKnowledgeSession( ksession );

        PseudoClockScheduler timeService = (PseudoClockScheduler) ksession.<SessionClock> getSessionClock();
        timeService.advanceTime( 3, TimeUnit.SECONDS );

        ksession = marsallStatefulKnowledgeSession( ksession );

        ksession.fireAllRules();

        ksession = marsallStatefulKnowledgeSession( ksession );

        assertEquals( 0,
                      list.size() );
    }

    @Test @Ignore("beta4 phreak")
    public void testMarshallEntryPointsWithSlidingTimeWindow() throws Exception {
        String str =
                "package org.domain.test \n" +
                        "import " + getClass().getCanonicalName() + ".*\n" +
                        "import java.util.List\n" +
                        "global java.util.List list\n" +
                        "declare A\n" +
                        " @role( event )\n" +
                        " @expires( 10m )\n" +
                        "end\n" +
                        "declare B\n" +
                        "" +
                        " @role( event )\n" +
                        " @expires( 10m )\n" +
                        "end\n" +
                        "" +
                        "rule a1\n" +
                        "when\n" +
                        "   $l : List() from collect( A()  over window:time(30s) from entry-point 'a-ep') \n" +
                        "then\n" +
                        "   list.add( $l );" +
                        "end\n";

        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBaseFromString( conf, str );

        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( "pseudo" ) );
        ksconf.setOption( TimerJobFactoryOption.get("trackable") );
        KieSession ksession = createKnowledgeSession(kbase, ksconf);

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        EntryPoint aep = ksession.getEntryPoint( "a-ep" );
        aep.insert( new A() );
        ksession = marsallStatefulKnowledgeSession( ksession );

        aep = ksession.getEntryPoint( "a-ep" );
        aep.insert( new A() );
        ksession = marsallStatefulKnowledgeSession( ksession );

        list.clear();
        ksession.fireAllRules();
        ksession = marsallStatefulKnowledgeSession( ksession );
        assertEquals( 2, ((List) list.get( 0 )).size() );

        PseudoClockScheduler timeService = (PseudoClockScheduler) ksession.<SessionClock> getSessionClock();
        timeService.advanceTime( 15, TimeUnit.SECONDS );
        ksession = marsallStatefulKnowledgeSession( ksession );

        aep = ksession.getEntryPoint( "a-ep" );
        aep.insert( new A() );
        ksession = marsallStatefulKnowledgeSession( ksession );

        aep = ksession.getEntryPoint( "a-ep" );
        aep.insert( new A() );
        ksession = marsallStatefulKnowledgeSession( ksession );

        list.clear();
        ksession.fireAllRules();
        ksession = marsallStatefulKnowledgeSession( ksession );
        assertEquals( 4, ((List) list.get( 0 )).size() );

        timeService = (PseudoClockScheduler) ksession.<SessionClock> getSessionClock();
        timeService.advanceTime( 20, TimeUnit.SECONDS );
        ksession = marsallStatefulKnowledgeSession( ksession );

        list.clear();
        ksession.fireAllRules();
        assertEquals( 2, ((List) list.get( 0 )).size() );
    }

    @Test
    public void testMarshallEntryPointsWithSlidingLengthWindow() throws Exception {
        String str =
                "package org.domain.test \n" +
                        "import " + getClass().getCanonicalName() + ".*\n" +
                        "import java.util.List\n" +
                        "global java.util.List list\n" +
                        "declare A\n" +
                        " @role( event )\n" +
                        " @expires( 10m )\n" +
                        "end\n" +
                        "declare B\n" +
                        "" +
                        " @role( event )\n" +
                        " @expires( 10m )\n" +
                        "end\n" +
                        "" +
                        "rule a1\n" +
                        "when\n" +
                        "   $l : List() from collect( A()  over window:length(3) from entry-point 'a-ep') \n" +
                        "then\n" +
                        "   list.add( $l );" +
                        "end\n";

        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        final KnowledgeBase kbase = loadKnowledgeBaseFromString( conf, str );

        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( "pseudo" ) );
        ksconf.setOption( TimerJobFactoryOption.get("trackable") );
        KieSession ksession = createKnowledgeSession(kbase, ksconf);

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        EntryPoint aep = ksession.getEntryPoint( "a-ep" );
        aep.insert( new A() );
        ksession = marsallStatefulKnowledgeSession( ksession );

        aep = ksession.getEntryPoint( "a-ep" );
        aep.insert( new A() );
        ksession = marsallStatefulKnowledgeSession( ksession );

        list.clear();
        ksession.fireAllRules();
        ksession = marsallStatefulKnowledgeSession( ksession );
        assertEquals( 2, ((List) list.get( 0 )).size() );

        aep = ksession.getEntryPoint( "a-ep" );
        aep.insert( new A() );
        ksession = marsallStatefulKnowledgeSession( ksession );

        aep = ksession.getEntryPoint( "a-ep" );
        aep.insert( new A() );
        ksession = marsallStatefulKnowledgeSession( ksession );

        list.clear();
        ksession.fireAllRules();
        ksession = marsallStatefulKnowledgeSession( ksession );
        assertEquals( 3, ((List) list.get( 0 )).size() );
    }

    @Test
    public void testMarshalWithProtoBuf() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "../test_Serializable.drl" );
        KieSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.setGlobal( "list",
                            new ArrayList() );
        final Person bob = new Person( "bob" );
        ksession.insert( bob );

        ksession = marsallStatefulKnowledgeSession( ksession );

        assertEquals( 1,
                      ksession.getFactCount() );
        assertEquals( bob,
                      ksession.getObjects().iterator().next() );

        int fired = ksession.fireAllRules();

        assertEquals( 3,
                      fired );

        List list = (List) ksession.getGlobal( "list" );

        assertEquals( 3,
                      list.size() );
        // because of agenda-groups
        assertEquals( new Integer( 4 ),
                      list.get( 0 ) );

        Collection<? extends Object> facts = ksession.getObjects();
        System.out.println( new ArrayList( facts ) );
        assertEquals( 2,
                      facts.size() );
    }

    private KieSession marsallStatefulKnowledgeSession(KieSession ksession) throws IOException,
                                                                                                       ClassNotFoundException {
        Globals globals = ksession.getGlobals();

        KieBase kbase = ksession.getKieBase();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MarshallerFactory.newMarshaller( kbase ).marshall( out,
                                                           ksession );

        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( TimerJobFactoryOption.get("trackable") );
        ksconf.setOption( ClockTypeOption.get( "pseudo" ) );

        Environment env = EnvironmentFactory.newEnvironment();
        env.set( EnvironmentName.GLOBALS, globals );
        ksession = MarshallerFactory.newMarshaller( kbase ).unmarshall( new ByteArrayInputStream( out.toByteArray() ), ksconf, env );

        return ksession;
    }

    private void readWrite(KnowledgeBase knowledgeBase,
                           KieSession ksession,
                           KieSessionConfiguration config) {
        try {
            Marshaller marshaller = MarshallerFactory.newMarshaller( knowledgeBase );
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            marshaller.marshall( o, ksession );
            ksession = marshaller.unmarshall( new ByteArrayInputStream( o.toByteArray() ), config, KnowledgeBaseFactory.newEnvironment() );
            ksession.fireAllRules();
            //scheduler = ksession.<SessionClock>getSessionClock();
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    private Marshaller createSerializableMarshaller(KnowledgeBase knowledgeBase) {
        ObjectMarshallingStrategyAcceptor acceptor = MarshallerFactory.newClassFilterAcceptor( new String[]{"*.*"} );
        ObjectMarshallingStrategy strategy = MarshallerFactory.newSerializeMarshallingStrategy( acceptor );
        Marshaller marshaller = MarshallerFactory.newMarshaller( knowledgeBase,
                                                                 new ObjectMarshallingStrategy[]{strategy} );
        return marshaller;
    }

    @Test
    public void testMarshallWithCollects() throws Exception {
        // BZ-1193600
        String str =
                "import java.util.Collection\n" +
                "rule R1 when\n" +
                "    Collection(empty==false) from collect( Integer() )\n" +
                "    Collection() from collect( String() )\n" +
                "then\n" +
                "end\n" +
                "rule R2 when then end\n";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL).build();
        KieSession ksession = kbase.newKieSession();

        try {
            Marshaller marshaller = MarshallerFactory.newMarshaller(kbase);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshall(baos, ksession);
            marshaller = MarshallerFactory.newMarshaller(kbase);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            baos.close();
            ksession = marshaller.unmarshall(bais);
            bais.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("unexpected exception :" + e.getMessage());
        }
    }
}
