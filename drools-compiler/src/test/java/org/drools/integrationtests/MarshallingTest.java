package org.drools.integrationtests;

import static org.drools.integrationtests.SerializationHelper.getSerialisedStatefulKnowledgeSession;
import static org.drools.integrationtests.SerializationHelper.getSerialisedStatefulSession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.Address;
import org.drools.Cell;
import org.drools.Cheese;
import org.drools.ClockType;
import org.drools.FactA;
import org.drools.FactB;
import org.drools.FactC;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.Message;
import org.drools.Person;
import org.drools.Primitives;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.BaseNode;
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.DroolsObjectOutputStream;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.conf.EventProcessingOption;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.core.util.KeyStoreHelper;
import org.drools.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.ResourceFactory;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.MarshallerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.ObjectMarshallingStrategyAcceptor;
import org.drools.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.marshalling.impl.IdentityPlaceholderResolverStrategy;
import org.drools.marshalling.impl.RuleBaseNodes;
import org.drools.reteoo.MockTupleSource;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.MapBackedClassLoader;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.Globals;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.Consequence;
import org.drools.spi.GlobalResolver;
import org.drools.spi.KnowledgeHelper;
import org.drools.time.impl.DurationTimer;
import org.drools.time.impl.PseudoClockScheduler;
import org.drools.time.impl.TrackableTimeJobFactoryManager;

public class MarshallingTest {

    @Test
    public void testSerializable() throws Exception {

        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Serializable.drl" ) );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        if ( builder.hasErrors() ) {
            fail( builder.getErrors().toString() );
        }
        final Package pkg = SerializationHelper.serializeObject( builder.getPackage() );

        assertEquals( 0,
                      builder.getErrors().getErrors().length );

        RuleBase ruleBase = getRuleBase( pkg );// RuleBaseFactory.newRuleBase();

        Map map = new HashMap();
        map.put( "x",
                 ruleBase );
        map = SerializationHelper.serializeObject( map );
        ruleBase = (RuleBase) map.get( "x" );
        final Rule[] rules = ruleBase.getPackages()[0].getRules();
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

        StatefulSession session = ruleBase.newStatefulSession();

        session.setGlobal( "list",
                           new ArrayList() );

        final Person bob = new Person( "bob" );
        session.insert( bob );

        session = SerializationHelper.getSerialisedStatefulSession( session );

        assertEquals( 1,
                      IteratorToList.convert( session.iterateObjects() ).size() );
        assertEquals( bob,
                      IteratorToList.convert( session.iterateObjects() ).get( 0 ) );

        assertEquals( 2,
                      session.getAgenda().agendaSize() );

        session.fireAllRules();

        List list = (List) session.getGlobal( "list" );

        assertEquals( 3,
                      list.size() );
        // because of agenda-groups
        assertEquals( new Integer( 4 ),
                      list.get( 0 ) );

        list = IteratorToList.convert( session.iterateObjects() );
        assertEquals( 2,
                      list.size() );
        
        assertTrue(  list.contains( bob ) );
        assertTrue( list.contains( new Person( "help" ) ) );
    }

    @Test
    public void testSerializeWorkingMemoryAndRuleBase1() throws Exception {
        // has the first newStatefulSession before the ruleBase is serialised
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Serializable.drl" ) );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg = SerializationHelper.serializeObject( builder.getPackage() );

        assertEquals( 0,
                      builder.getErrors().getErrors().length );

        RuleBase ruleBase = getRuleBase();// RuleBaseFactory.newRuleBase();

        StatefulSession session = ruleBase.newStatefulSession();

        Map map = new HashMap();
        map.put( "x",
                 ruleBase );
        map = SerializationHelper.serializeObject( map );
        ruleBase = (RuleBase) map.get( "x" );

        ruleBase.addPackage( pkg );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        session.setGlobal( "list",
                           new ArrayList() );

        final Person bob = new Person( "bob" );
        session.insert( bob );

        final Rule[] rules = ruleBase.getPackages()[0].getRules();

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
                      IteratorToList.convert( session.iterateObjects() ).size() );
        assertEquals( bob,
                      IteratorToList.convert( session.iterateObjects() ).get( 0 ) );

        assertEquals( 2,
                      session.getAgenda().agendaSize() );

        session = SerializationHelper.getSerialisedStatefulSession( session );
        session.fireAllRules();

        final List list = (List) session.getGlobal( "list" );

        assertEquals( 3,
                      list.size() );
        // because of agenda-groups
        assertEquals( new Integer( 4 ),
                      list.get( 0 ) );

        assertEquals( 2,
                      IteratorToList.convert( session.iterateObjects() ).size() );
        assertTrue( IteratorToList.convert( session.iterateObjects() ).contains( bob ) );
        assertTrue( IteratorToList.convert( session.iterateObjects() ).contains( new Person( "help" ) ) );

    }

    @Test
    public void testSerializeWorkingMemoryAndRuleBase2() throws Exception {
        // has the first newStatefulSession after the ruleBase is serialised
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Serializable.drl" ) );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg = SerializationHelper.serializeObject( builder.getPackage() );

        assertEquals( 0,
                      builder.getErrors().getErrors().length );

        RuleBase ruleBase = getRuleBase();// RuleBaseFactory.newRuleBase();

        // serialise a hashmap with the RuleBase as a key
        Map map = new HashMap();
        map.put( "x",
                 ruleBase );
        map = SerializationHelper.serializeObject( map );
        ruleBase = (RuleBase) map.get( "x" );

        StatefulSession session = ruleBase.newStatefulSession();

        // serialise the working memory before population
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );

        ruleBase.addPackage( pkg );

        session.setGlobal( "list",
                           new ArrayList() );

        final Person bob = new Person( "bob" );
        session.insert( bob );

        final Rule[] rules = ruleBase.getPackages()[0].getRules();

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
                      IteratorToList.convert( session.iterateObjects() ).size() );
        assertEquals( bob,
                      IteratorToList.convert( session.iterateObjects() ).get( 0 ) );

        assertEquals( 2,
                      session.getAgenda().agendaSize() );

        session = SerializationHelper.getSerialisedStatefulSession( session );
        session.fireAllRules();

        final List list = (List) session.getGlobal( "list" );

        assertEquals( 3,
                      list.size() );
        // because of agenda-groups
        assertEquals( new Integer( 4 ),
                      list.get( 0 ) );

        assertEquals( 2,
                      IteratorToList.convert( session.iterateObjects() ).size() );
        assertTrue( IteratorToList.convert( session.iterateObjects() ).contains( bob ) );
        assertTrue( IteratorToList.convert( session.iterateObjects() ).contains( new Person( "help" ) ) );
    }

    @Test
    public void testSerializeWorkingMemoryAndRuleBase3() throws Exception {
        // has the first newStatefulSession after the ruleBase is serialised
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Serializable.drl" ) );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg = SerializationHelper.serializeObject( builder.getPackage() );

        assertEquals( 0,
                      builder.getErrors().getErrors().length );

        RuleBase ruleBase = getRuleBase();
        StatefulSession session = ruleBase.newStatefulSession();

        ruleBase.addPackage( pkg );

        session.setGlobal( "list",
                           new ArrayList() );

        final Person bob = new Person( "bob" );
        session.insert( bob );

        // serialise a hashmap with the RuleBase as a key, after WM population
        Map map = new HashMap();
        map.put( "x",
                 ruleBase );
        map = SerializationHelper.serializeObject( map );
        ruleBase = (RuleBase) map.get( "x" );

        // now try serialising with a fully populated wm from a serialised rulebase
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );

        final Rule[] rules = ruleBase.getPackages()[0].getRules();

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
                      IteratorToList.convert( session.iterateObjects() ).size() );
        assertEquals( bob,
                      IteratorToList.convert( session.iterateObjects() ).get( 0 ) );

        assertEquals( 2,
                      session.getAgenda().agendaSize() );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        session.fireAllRules();

        final List list = (List) session.getGlobal( "list" );

        assertEquals( 3,
                      list.size() );
        // because of agenda-groups
        assertEquals( new Integer( 4 ),
                      list.get( 0 ) );

        assertEquals( 2,
                      IteratorToList.convert( session.iterateObjects() ).size() );
        assertTrue( IteratorToList.convert( session.iterateObjects() ).contains( bob ) );
        assertTrue( IteratorToList.convert( session.iterateObjects() ).contains( new Person( "help" ) ) );
    }

    @Test
    public void testSerializeAdd() throws Exception {

        //Create a rulebase, a session, and test it
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );
        Package pkg = SerializationHelper.serializeObject( builder.getPackage() );
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        StatefulSession session = ruleBase.newStatefulSession();
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
        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        list = (List) session.getGlobal( "list" );

        assertNotNull( list );
        assertEquals( list.size(),
                      1 );
        assertEquals( "stilton",
                      list.get( 0 ) );

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3.drl" ) ) );
        pkg = SerializationHelper.serializeObject( builder.getPackage() );
        ruleBase.addPackage( pkg );

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
                      list.get( 1 ) );
        assertEquals( "stilton",
                      list.get( 2 ) );

        session.dispose();

    }

    @Test
    public void testSerializationOfIndexedWM() throws Exception {
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Serializable2.drl" ) );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg = builder.getPackage();

        assertEquals( builder.getErrors().toString(),
                      0,
                      builder.getErrors().getErrors().length );

        RuleBase ruleBase = getRuleBase( pkg );// RuleBaseFactory.newRuleBase();

        Map map = new HashMap();
        map.put( "x",
                 ruleBase );
        map = SerializationHelper.serializeObject( map );
        ruleBase = (RuleBase) map.get( "x" );
        final Rule[] rules = ruleBase.getPackages()[0].getRules();
        assertEquals( 3,
                      rules.length );

        StatefulSession session = ruleBase.newStatefulSession();

        session.setGlobal( "list",
                           new ArrayList() );

        final Primitives p = new Primitives();
        p.setBytePrimitive( (byte) 1 );
        p.setShortPrimitive( (short) 2 );
        p.setIntPrimitive( (int) 3 );
        session.insert( p );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );

        assertEquals( 1,
                      IteratorToList.convert( session.iterateObjects() ).size() );
        assertEquals( p,
                      IteratorToList.convert( session.iterateObjects() ).get( 0 ) );

        assertEquals( 3,
                      session.getAgenda().agendaSize() );

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
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
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1_0.drl" ) ) );
        Package pkg = SerializationHelper.serializeObject( builder.getPackage() );
        ruleBase.addPackage( pkg );

        List results = new ArrayList();
        StatefulSession session = ruleBase.newStatefulSession();
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

        // serialize session and rulebase out
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StatefulKnowledgeSessionImpl ksession = new StatefulKnowledgeSessionImpl( (ReteooStatefulSession) session );
        Marshaller marshaller = MarshallerFactory.newMarshaller( ksession.getKnowledgeBase() );
        marshaller.marshall( baos,
                             ksession );
        baos.close();

        GlobalResolver resolver = session.getGlobalResolver();
        byte[] serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );
        session.dispose();

        // now deserialize the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );

        marshaller = MarshallerFactory.newMarshaller( new KnowledgeBaseImpl( ruleBase ) );
        ksession = (StatefulKnowledgeSessionImpl) marshaller.unmarshall( new ByteArrayInputStream( baos.toByteArray() ),
                                                                         KnowledgeBaseFactory.newKnowledgeSessionConfiguration(),
                                                                         EnvironmentFactory.newEnvironment() );
        session = (ReteooStatefulSession) ksession.session;
        session.setGlobalResolver( resolver );

        // dynamically add a new package
        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3_0.drl" ) ) );
        pkg = SerializationHelper.serializeObject( builder.getPackage() );
        ruleBase.addPackage( pkg );

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

        assertEquals( mark.getObject(),
                      results.get( 2 ) );

        assertEquals( bob.getObject(),
                      results.get( 3 ) );

        serializedRulebase = null;

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );

        serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

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
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1_0.drl" ) ) );
        Package pkg = SerializationHelper.serializeObject( builder.getPackage() );
        ruleBase.addPackage( pkg );

        List results = new ArrayList();
        StatefulSession session = ruleBase.newStatefulSession();
        session.setGlobal( "results",
                           results );

        InternalFactHandle stilton1 = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                       10 ) );
        InternalFactHandle brie1 = (InternalFactHandle) session.insert( new Cheese( "brie",
                                                                                    10 ) );
        session.fireAllRules();

        GlobalResolver resolver = session.getGlobalResolver();

        // serialize session and rulebase out
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StatefulKnowledgeSessionImpl ksession = new StatefulKnowledgeSessionImpl( (ReteooStatefulSession) session );
        Marshaller marshaller = MarshallerFactory.newMarshaller( ksession.getKnowledgeBase() );
        marshaller.marshall( baos,
                             ksession );
        baos.close();

        byte[] serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

        session.dispose();

        assertEquals( 1,
                      results.size() );
        assertEquals( stilton1.getObject(),
                      results.get( 0 ) );

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
        marshaller = MarshallerFactory.newMarshaller( new KnowledgeBaseImpl( ruleBase ) );
        ksession = (StatefulKnowledgeSessionImpl) marshaller.unmarshall( new ByteArrayInputStream( baos.toByteArray() ),
                                                                         KnowledgeBaseFactory.newKnowledgeSessionConfiguration(),
                                                                         EnvironmentFactory.newEnvironment() );
        session = (ReteooStatefulSession) ksession.session;
        session.setGlobalResolver( resolver );

        results = (List) session.getGlobal( "results" );

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1_1.drl" ) ) );
        pkg = SerializationHelper.serializeObject( builder.getPackage() );
        ruleBase.addPackage( pkg );

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

        assertEquals( brie3.getObject(),
                      results.get( 2 ) );
        assertEquals( brie2.getObject(),
                      results.get( 3 ) );

        assertEquals( brie1.getObject(),
                      results.get( 4 ) );

        serializedRulebase = null;

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );
        serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

        session.dispose();
    }

    /*
     *  Works Fine if both the scenarios mentioned above are skipped.
     */
    @Test
    public void testSerializeAdd3() throws Exception {
        //Create a rulebase, a session, and test it
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1_0.drl" ) ) );
        Package pkg = SerializationHelper.serializeObject( builder.getPackage() );
        ruleBase.addPackage( pkg );

        List results = new ArrayList();
        StatefulSession session = ruleBase.newStatefulSession();
        session.setGlobal( "results",
                           results );

        InternalFactHandle stilton1 = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                       10 ) );
        InternalFactHandle brie1 = (InternalFactHandle) session.insert( new Cheese( "brie",
                                                                                    10 ) );
        session.fireAllRules();

        GlobalResolver resolver = session.getGlobalResolver();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StatefulKnowledgeSessionImpl ksession = new StatefulKnowledgeSessionImpl( (ReteooStatefulSession) session );
        Marshaller marshaller = MarshallerFactory.newMarshaller( ksession.getKnowledgeBase() );
        marshaller.marshall( baos,
                             ksession );
        baos.close();
        byte[] serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

        session.dispose();

        assertEquals( 1,
                      results.size() );
        assertEquals( stilton1.getObject(),
                      results.get( 0 ) );

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
        marshaller = MarshallerFactory.newMarshaller( new KnowledgeBaseImpl( ruleBase ) );
        ksession = (StatefulKnowledgeSessionImpl) marshaller.unmarshall( new ByteArrayInputStream( baos.toByteArray() ),
                                                                         KnowledgeBaseFactory.newKnowledgeSessionConfiguration(),
                                                                         EnvironmentFactory.newEnvironment() );
        session = (ReteooStatefulSession) ksession.session;
        session.setGlobalResolver( resolver );
        results = (List) session.getGlobal( "results" );

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3_0.drl" ) ) );
        pkg = SerializationHelper.serializeObject( builder.getPackage() );

        ruleBase.addPackage( pkg );

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

        serializedRulebase = null;

        resolver = session.getGlobalResolver();
        baos = new ByteArrayOutputStream();
        ksession = new StatefulKnowledgeSessionImpl( (ReteooStatefulSession) session );
        marshaller = MarshallerFactory.newMarshaller( ksession.getKnowledgeBase() );
        marshaller.marshall( baos,
                             ksession );
        baos.close();
        serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

        session.dispose();

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
        marshaller = MarshallerFactory.newMarshaller( new KnowledgeBaseImpl( ruleBase ) );
        ksession = (StatefulKnowledgeSessionImpl) marshaller.unmarshall( new ByteArrayInputStream( baos.toByteArray() ),
                                                                         KnowledgeBaseFactory.newKnowledgeSessionConfiguration(),
                                                                         EnvironmentFactory.newEnvironment() );
        session = (ReteooStatefulSession) ksession.session;
        session.setGlobalResolver( resolver );
        results = (List) session.getGlobal( "results" );

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1_2.drl" ) ) );
        pkg = SerializationHelper.serializeObject( builder.getPackage() );
        ruleBase.addPackage( pkg );

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

        serializedRulebase = null;

        resolver = session.getGlobalResolver();
        baos = new ByteArrayOutputStream();
        ksession = new StatefulKnowledgeSessionImpl( (ReteooStatefulSession) session );
        marshaller = MarshallerFactory.newMarshaller( ksession.getKnowledgeBase() );
        marshaller.marshall( baos,
                             ksession );
        baos.close();
        serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );
        session.dispose();

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
        marshaller = MarshallerFactory.newMarshaller( new KnowledgeBaseImpl( ruleBase ) );
        ksession = (StatefulKnowledgeSessionImpl) marshaller.unmarshall( new ByteArrayInputStream( baos.toByteArray() ),
                                                                         KnowledgeBaseFactory.newKnowledgeSessionConfiguration(),
                                                                         EnvironmentFactory.newEnvironment() );
        session = (ReteooStatefulSession) ksession.session;
        session.setGlobalResolver( resolver );
        results = (List) session.getGlobal( "results" );

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

        serializedRulebase = null;

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );

        serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

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
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1_0.drl" ) ) );
        Package pkg = SerializationHelper.serializeObject( builder.getPackage() );
        ruleBase.addPackage( pkg );

        List results = new ArrayList();
        StatefulSession session = ruleBase.newStatefulSession();
        session.setGlobal( "results",
                           results );

        InternalFactHandle stilton1 = (InternalFactHandle) session.insert( new Cheese( "stilton",
                                                                                       10 ) );
        InternalFactHandle brie1 = (InternalFactHandle) session.insert( new Cheese( "brie",
                                                                                    10 ) );
        session.fireAllRules();

        GlobalResolver resolver = session.getGlobalResolver();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StatefulKnowledgeSessionImpl ksession = new StatefulKnowledgeSessionImpl( (ReteooStatefulSession) session );
        Marshaller marshaller = MarshallerFactory.newMarshaller( ksession.getKnowledgeBase() );
        marshaller.marshall( baos,
                             ksession );
        baos.close();
        byte[] serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

        session.dispose();

        assertEquals( 1,
                      results.size() );
        assertEquals( stilton1.getObject(),
                      results.get( 0 ) );

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
        marshaller = MarshallerFactory.newMarshaller( new KnowledgeBaseImpl( ruleBase ) );
        ksession = (StatefulKnowledgeSessionImpl) marshaller.unmarshall( new ByteArrayInputStream( baos.toByteArray() ),
                                                                         KnowledgeBaseFactory.newKnowledgeSessionConfiguration(),
                                                                         EnvironmentFactory.newEnvironment() );
        session = (ReteooStatefulSession) ksession.session;
        results.clear();
        session.setGlobal( "results",
                           results );

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3_0.drl" ) ) );
        pkg = SerializationHelper.serializeObject( builder.getPackage() );

        ruleBase.addPackage( pkg );

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

        serializedRulebase = null;

        baos = new ByteArrayOutputStream();
        ksession = new StatefulKnowledgeSessionImpl( (ReteooStatefulSession) session );
        marshaller = MarshallerFactory.newMarshaller( ksession.getKnowledgeBase() );
        marshaller.marshall( baos,
                             ksession );
        baos.close();
        serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

        session.dispose();

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
        marshaller = MarshallerFactory.newMarshaller( new KnowledgeBaseImpl( ruleBase ) );
        ksession = (StatefulKnowledgeSessionImpl) marshaller.unmarshall( new ByteArrayInputStream( baos.toByteArray() ),
                                                                         KnowledgeBaseFactory.newKnowledgeSessionConfiguration(),
                                                                         EnvironmentFactory.newEnvironment() );
        session = (ReteooStatefulSession) ksession.session;
        results.clear();
        session.setGlobal( "results",
                           results );

        // CASE 1: remove rule
        ruleBase.removeRule( "org.drools.test",
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
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
        marshaller = MarshallerFactory.newMarshaller( new KnowledgeBaseImpl( ruleBase ) );
        ksession = (StatefulKnowledgeSessionImpl) marshaller.unmarshall( new ByteArrayInputStream( baos.toByteArray() ),
                                                                         KnowledgeBaseFactory.newKnowledgeSessionConfiguration(),
                                                                         EnvironmentFactory.newEnvironment() );
        session = (ReteooStatefulSession) ksession.session;
        results.clear();
        session.setGlobal( "results",
                           results );

        // CASE 2: remove pkg
        ruleBase.removePackage( "org.drools.test" );

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

        byte[] serializedSession = null;
        serializedRulebase = null;
        baos = new ByteArrayOutputStream();
        ksession = new StatefulKnowledgeSessionImpl( (ReteooStatefulSession) session );
        marshaller = MarshallerFactory.newMarshaller( ksession.getKnowledgeBase() );
        marshaller.marshall( baos,
                             ksession );
        baos.close();
        serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

        session.dispose();
        // Deserialize the rulebase and the session 
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
        marshaller = MarshallerFactory.newMarshaller( new KnowledgeBaseImpl( ruleBase ) );
        ksession = (StatefulKnowledgeSessionImpl) marshaller.unmarshall( new ByteArrayInputStream( baos.toByteArray() ),
                                                                         KnowledgeBaseFactory.newKnowledgeSessionConfiguration(),
                                                                         EnvironmentFactory.newEnvironment() );
        session = (ReteooStatefulSession) ksession.session;
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

        serializedSession = null;
        serializedRulebase = null;

        session = SerializationHelper.getSerialisedStatefulSession( session,
                                                                    ruleBase );

        serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

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

            //Compile a package
            PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1_0.drl" ) ) );

            // Test package serialization/deserialization
            Package pkg = SerializationHelper.serializeObject( builder.getPackage() );

            // Create a rulebase
            RuleBase ruleBase = RuleBaseFactory.newRuleBase();
            ruleBase.addPackage( pkg );

            // Test rulebase serialization/deserialization
            byte[] serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );
            ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
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

            //Compile a package
            PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1_0.drl" ) ) );

            try {
                // Test package serialization/deserialization
                Package pkg = SerializationHelper.serializeObject( builder.getPackage() );
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

            //Compile a package
            PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1_0.drl" ) ) );

            // Create a rulebase
            RuleBase ruleBase = RuleBaseFactory.newRuleBase();
            ruleBase.addPackage( builder.getPackage() );

            // Test rulebase serialization/deserialization
            byte[] serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

            try {
                ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
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

        //Compile a package
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1_0.drl" ) ) );

        // Create a rulebase
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        // Test rulebase serialization/deserialization
        byte[] serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

        try {
            // set only the deserialisation properties, but not the serialization
            setPublicKeyProperties();
            ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
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
        byte[] buf = new byte[ 1024 ];
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

        PackageBuilder builder = new PackageBuilder( new PackageBuilderConfiguration( loader ) );
        builder.addPackageFromDrl( new StringReader( drl ) );
        assertFalse( builder.hasErrors() );

        Package p = builder.getPackage();
        byte[] ser = DroolsStreamUtils.streamOut( p );

        //now read it back
        Package p_ = (Package) DroolsStreamUtils.streamIn( ser,
                                                           loader );
        assertNotNull( p_ );
    }

    @Test
    public void testEmptyRule() throws Exception {
        String rule = "package org.test;\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "when\n";
        rule += "then\n";
        rule += "    list.add( \"fired\" );\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );
        assertTrue( builder.getErrors().isEmpty() );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        // Make sure the rete node map is created correctly
        Map<Integer, BaseNode> nodes = RuleBaseNodes.getNodeMap( (InternalRuleBase) ruleBase );
        assertEquals( 2,
                      nodes.size() );
        assertEquals( "InitialFactImpl",
                      ((ClassObjectType) ((ObjectTypeNode) nodes.get( 2 )).getObjectType()).getClassType().getSimpleName() );
        assertEquals( "Rule 1",
                      ((RuleTerminalNode) nodes.get( 4 )).getRule().getName() );

        StatefulSession session = ruleBase.newStatefulSession();

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        session = getSerialisedStatefulSession( session );

        session.fireAllRules();

        session = getSerialisedStatefulSession( session );

        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( "fired",
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

    @Test
    public void testDynamicEmptyRule() throws Exception {
        String rule1 = "package org.test;\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "when\n";
        rule1 += "then\n";
        rule1 += "    list.add( \"fired1\" );\n";
        rule1 += "end";

        String rule2 = "package org.test;\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule \"Rule 2\"\n";
        rule2 += "when\n";
        rule2 += "then\n";
        rule2 += "    list.add( \"fired2\" );\n";
        rule2 += "end";

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule1 ) );
        Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        // Make sure the rete node map is created correctly
        Map<Integer, BaseNode> nodes = RuleBaseNodes.getNodeMap( (InternalRuleBase) ruleBase );
        assertEquals( 2,
                      nodes.size() );
        assertEquals( "InitialFactImpl",
                      ((ClassObjectType) ((ObjectTypeNode) nodes.get( 2 )).getObjectType()).getClassType().getSimpleName() );
        assertEquals( "Rule 1",
                      ((RuleTerminalNode) nodes.get( 4 )).getRule().getName() );

        StatefulSession session = ruleBase.newStatefulSession();

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );
        StatefulSession session1 = getSerialisedStatefulSession( session );
        session1.fireAllRules();

        assertEquals( 1,
                      ((List) session1.getGlobal( "list" )).size() );

        WorkingMemory session2 = getSerialisedStatefulSession( session1,
                                                               true );

        session.dispose();
        session1.dispose();

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule2 ) );
        pkg = builder.getPackage();

        ruleBase.addPackage( pkg );
        session2.fireAllRules();

        assertEquals( 2,
                      ((List) session2.getGlobal( "list" )).size() );
        assertEquals( "fired1",
                      ((List) session2.getGlobal( "list" )).get( 0 ) );
        assertEquals( "fired2",
                      ((List) session2.getGlobal( "list" )).get( 1 ) );
    }

    @Test
    public void testSinglePattern() throws Exception {
        String rule = "package org.test;\n";
        rule += "import org.drools.Person\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "when\n";
        rule += "    $p : Person( ) \n";
        rule += "then\n";
        rule += "    list.add( $p );\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        // Make sure the rete node map is created correctly
        Map<Integer, BaseNode> nodes = RuleBaseNodes.getNodeMap( (InternalRuleBase) ruleBase );
        assertEquals( 2,
                      nodes.size() );
        assertEquals( "Person",
                      ((ClassObjectType) ((ObjectTypeNode) nodes.get( 2 )).getObjectType()).getClassType().getSimpleName() );
        assertEquals( "Rule 1",
                      ((RuleTerminalNode) nodes.get( 4 )).getRule().getName() );

        StatefulSession session = ruleBase.newStatefulSession();

        List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        Person p = new Person( "bobba fet",
                               32 );
        session.insert( p );

        session = getSerialisedStatefulSession( session );
        session.fireAllRules();

        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( p,
                      ((List) session.getGlobal( "list" )).get( 0 ) );
    }

    @Test
    public void testSingleRuleSingleJoinNodePattern() throws Exception {
        String rule = "package org.test;\n";
        rule += "import org.drools.Person\n";
        rule += "import org.drools.Cheese\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "when\n";
        rule += "    $c : Cheese( ) \n";
        rule += "    $p : Person( cheese == $c ) \n";
        rule += "then\n";
        rule += "    list.add( $p );\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        // Make sure the rete node map is created correctly
        Map<Integer, BaseNode> nodes = RuleBaseNodes.getNodeMap( (InternalRuleBase) ruleBase );
        assertEquals( 4,
                      nodes.size() );
        assertEquals( "Cheese",
                      ((ClassObjectType) ((ObjectTypeNode) nodes.get( 2 )).getObjectType()).getClassType().getSimpleName() );
        assertEquals( "Person",
                      ((ClassObjectType) ((ObjectTypeNode) nodes.get( 4 )).getObjectType()).getClassType().getSimpleName() );
        assertEquals( "JoinNode",
                      nodes.get( 5 ).getClass().getSimpleName() );
        assertEquals( "Rule 1",
                      ((RuleTerminalNode) nodes.get( 6 )).getRule().getName() );

        StatefulSession session = ruleBase.newStatefulSession();

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

        session = getSerialisedStatefulSession( session );

        session.fireAllRules();

        assertEquals( 1,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( bobba,
                      ((List) session.getGlobal( "list" )).get( 0 ) );

        Person c3po = new Person( "c3p0",
                                  32 );
        c3po.setCheese( stilton );
        session.insert( c3po );

        session = getSerialisedStatefulSession( session );

        session.fireAllRules();

        assertEquals( 2,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( c3po,
                      ((List) session.getGlobal( "list" )).get( 1 ) );

        Person r2d2 = new Person( "r2d2",
                                  32 );
        r2d2.setCheese( brie );
        session.insert( r2d2 );

        session = getSerialisedStatefulSession( session );

        session.fireAllRules();

        assertEquals( 3,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( r2d2,
                      ((List) session.getGlobal( "list" )).get( 2 ) );
    }

    @Test
    public void testMultiRuleMultiJoinNodePatternsWithHalt() throws Exception {
        String rule1 = "package org.test;\n";
        rule1 += "import org.drools.Person\n";
        rule1 += "import org.drools.Cheese\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "when\n";
        rule1 += "    $c : Cheese( ) \n";
        rule1 += "    $p : Person( cheese == $c ) \n";
        rule1 += "then\n";
        rule1 += "    list.add( $p );\n";
        rule1 += "end";

        String rule2 = "package org.test;\n";
        rule2 += "import org.drools.Person\n";
        rule2 += "import org.drools.Cheese\n";
        rule2 += "import org.drools.Cell\n";
        rule2 += "global java.util.List list\n";
        rule2 += "rule \"Rule 2\"\n";
        rule2 += "when\n";
        rule2 += "    $c : Cheese( ) \n";
        rule2 += "    $p : Person( cheese == $c ) \n";
        rule2 += "    Cell( value == $p.age ) \n";
        rule2 += "then\n";
        rule2 += "    list.add( $p );\n";
        rule2 += "end";

        String rule3 = "package org.test;\n";
        rule3 += "import org.drools.FactA\n";
        rule3 += "import org.drools.FactB\n";
        rule3 += "import org.drools.FactC\n";
        rule3 += "import org.drools.Person\n";
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

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule1 ) );
        builder.addPackageFromDrl( new StringReader( rule2 ) );
        builder.addPackageFromDrl( new StringReader( rule3 ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        StatefulSession session = ruleBase.newStatefulSession();

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

        session = getSerialisedStatefulSession( session );
        session.fireAllRules();

        assertEquals( 3,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( r2d2,
                      ((List) session.getGlobal( "list" )).get( 0 ) );
        assertEquals( c3po,
                      ((List) session.getGlobal( "list" )).get( 1 ) );
        assertEquals( bobba,
                      ((List) session.getGlobal( "list" )).get( 2 ) );

        session = getSerialisedStatefulSession( session );

        session.insert( new Cell( 30 ) );
        session.insert( new Cell( 58 ) );

        session = getSerialisedStatefulSession( session );

        session.fireAllRules();

        assertEquals( 5,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( bobba,
                      ((List) session.getGlobal( "list" )).get( 4 ) );
        assertEquals( r2d2,
                      ((List) session.getGlobal( "list" )).get( 3 ) );

        session = getSerialisedStatefulSession( session );

        session.insert( new FactA( 15 ) );
        session.insert( new FactB( 20 ) );
        session.insert( new FactC( 27 ) );
        session.insert( new FactC( 52 ) );

        session = getSerialisedStatefulSession( session );

        session.fireAllRules();

        assertEquals( 6,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( new FactC( 52 ),
                      ((List) session.getGlobal( "list" )).get( 5 ) );

        session = getSerialisedStatefulSession( session );

        session.fireAllRules();

        assertEquals( 7,
                      ((List) session.getGlobal( "list" )).size() );
        assertEquals( new FactC( 27 ),
                      ((List) session.getGlobal( "list" )).get( 6 ) );
    }

    @Test
    public void testNot() throws Exception {
        String header = "package org.drools.test;\n";
        header += "import java.util.List;\n";
        header += "import org.drools.Person\n";
        header += "import org.drools.Cheese\n";
        header += "global java.util.List list;\n";

        String rule1 = "rule \"not rule test\"\n";
        rule1 += "salience 10\n";
        rule1 += "when\n";
        rule1 += "    Person()\n";
        rule1 += "    not Cheese( price >= 5 )\n";
        rule1 += "then\n";
        rule1 += "    list.add( new Integer( 5 ) );\n";
        rule1 += "end\n";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( (header + rule1).getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        Environment env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{
                    new IdentityPlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT)});
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(null, env);
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
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );

        // add another person, no cheese
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        Person darth = new Person( "darth vadar",
                                   200 );
        ksession.insert( darth );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );

        // add cheese 
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        Cheese stilton = new Cheese( "stilton",
                                     5 );
        ksession.insert( stilton );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );

        // remove cheese
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.retract( ksession.getFactHandle( stilton ) );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );

        // put 2 cheeses back in
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.insert( stilton );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        Cheese brie = new Cheese( "brie",
                                  18 );
        ksession.insert( brie );
        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );

        // now remove a cheese, should be no change
        ksession.retract( ksession.getFactHandle( stilton ) );
        //        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                          MarshallerFactory.newIdentityMarshallingStrategy(),
        //                                                          true );
        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );

        // now remove a person, should be no change
        ksession.retract( ksession.getFactHandle( bobba ) );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );

        //removal remaining cheese, should increase by one, as one person left
        ksession.retract( ksession.getFactHandle( brie ) );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.fireAllRules();
        assertEquals( 5,
                      list.size() );
    }

    @Test
    public void testExists() throws Exception {
        String header = "package org.drools.test;\n";
        header += "import java.util.List;\n";
        header += "import org.drools.Person\n";
        header += "import org.drools.Cheese\n";
        header += "global java.util.List list;\n";

        String rule1 = "rule \"not rule test\"\n";
        rule1 += "salience 10\n";
        rule1 += "when\n";
        rule1 += "    Person()\n";
        rule1 += "    exists Cheese( price >= 5 )\n";
        rule1 += "then\n";
        rule1 += "    list.add( new Integer( 5 ) );\n";
        rule1 += "end\n";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( (header + rule1).getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
         Environment env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{
                    new IdentityPlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT)});
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(null, env);
        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );

        // add a person, no cheese
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        Person bobba = new Person( "bobba fet",
                                   50 );
        ksession.insert( bobba );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.fireAllRules();
        assertEquals( 0,
                      list.size() );

        // add another person, no cheese
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        Person darth = new Person( "darth vadar",
                                   200 );
        ksession.insert( darth );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.fireAllRules();
        assertEquals( 0,
                      list.size() );

        // add cheese 
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        Cheese stilton = new Cheese( "stilton",
                                     5 );
        ksession.insert( stilton );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );

        // remove cheese
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.retract( ksession.getFactHandle( stilton ) );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );

        // put 2 cheeses back in
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.insert( stilton );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
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
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );

        // now remove a person, should be no change
        ksession.retract( ksession.getFactHandle( bobba ) );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );

        //removal remaining cheese, no
        ksession.retract( ksession.getFactHandle( brie ) );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );

        // put one cheese back in, with one person should increase by one
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
        //                                                  MarshallerFactory.newIdentityMarshallingStrategy(),
                                                          true );
        ksession.insert( stilton );
        ksession.fireAllRules();
        assertEquals( 5,
                      list.size() );
    }

    @Test
    public void testTruthMaintenance() throws Exception {
        String header = "package org.drools.test;\n";
        header += "import java.util.List;\n";
        header += "import org.drools.Person\n";
        header += "import org.drools.Cheese\n";
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

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( header + rule1 ) );
        builder.addPackageFromDrl( new StringReader( header + rule2 ) );
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );

        StatefulSession session = ruleBase.newStatefulSession();

        final List list = new ArrayList();

        final Person person = new Person( "person" );
        final Cheese cheese = new Cheese( "cheese",
                                          0 );
        session.setGlobal( "cheese",
                           cheese );
        session.setGlobal( "person",
                           person );
        session.setGlobal( "list",
                           list );
        session.fireAllRules();
        assertEquals( 1,
                      list.size() );

        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 2,
                      list.size() );

        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 3,
                      list.size() );

        // should not grow any further
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( 3,
                      list.size() );
    }

    @Test
    public void testActivationGroups() throws Exception {
        String rule1 = "package org.test;\n";
        rule1 += "import org.drools.Cheese\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "    activation-group \"activation-group-1\"\n";
        rule1 += "when\n";
        rule1 += "    $c : Cheese( ) \n";
        rule1 += "then\n";
        rule1 += "    list.add( \"rule1\" );\n";
        rule1 += "    drools.halt();\n";
        rule1 += "end";

        String rule2 = "package org.test;\n";
        rule2 += "import org.drools.Cheese\n";
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

        String rule3 = "package org.test;\n";
        rule3 += "import org.drools.Cheese\n";
        rule3 += "global java.util.List list\n";
        rule3 += "rule \"Rule 3\"\n";
        rule3 += "    activation-group \"activation-group-1\"\n";
        rule3 += "when\n";
        rule3 += "    $c : Cheese( ) \n";
        rule3 += "then\n";
        rule3 += "    list.add( \"rule3\" );\n";
        rule3 += "    drools.halt();\n";
        rule3 += "end";

        String rule4 = "package org.test;\n";
        rule4 += "import org.drools.Cheese\n";
        rule4 += "global java.util.List list\n";
        rule4 += "rule \"Rule 4\"\n";
        rule4 += "    activation-group \"activation-group-2\"\n";
        rule4 += "when\n";
        rule4 += "    $c : Cheese( ) \n";
        rule4 += "then\n";
        rule4 += "    list.add( \"rule4\" );\n";
        rule4 += "    drools.halt();\n";
        rule4 += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule1 ) );
        builder.addPackageFromDrl( new StringReader( rule2 ) );
        builder.addPackageFromDrl( new StringReader( rule3 ) );
        builder.addPackageFromDrl( new StringReader( rule4 ) );

        final Package pkg = builder.getPackage();

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();
        session = getSerialisedStatefulSession( session );

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        session.insert( brie );

        session = getSerialisedStatefulSession( session );
        session.fireAllRules();

        session = getSerialisedStatefulSession( session );
        session.fireAllRules();

        assertEquals( 2,
                      list.size() );
        assertEquals( "rule2",
                      list.get( 0 ) );
        assertEquals( "rule4",
                      list.get( 1 ) );
    }

    @Test
    public void testAgendaGroups() throws Exception {
        String rule1 = "package org.test;\n";
        rule1 += "import org.drools.Cheese\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "    agenda-group \"agenda-group-1\"\n";
        rule1 += "when\n";
        rule1 += "    $c : Cheese( ) \n";
        rule1 += "then\n";
        rule1 += "    list.add( \"rule1\" );\n";
        rule1 += "    drools.halt();\n";
        rule1 += "end";

        String rule2 = "package org.test;\n";
        rule2 += "import org.drools.Cheese\n";
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

        String rule3 = "package org.test;\n";
        rule3 += "import org.drools.Cheese\n";
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

        String rule4 = "package org.test;\n";
        rule4 += "import org.drools.Cheese\n";
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

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule1 ) );
        builder.addPackageFromDrl( new StringReader( rule2 ) );
        builder.addPackageFromDrl( new StringReader( rule3 ) );
        builder.addPackageFromDrl( new StringReader( rule4 ) );

        final Package pkg = builder.getPackage();

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        session = getSerialisedStatefulSession( session );

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        session.insert( brie );

        session = getSerialisedStatefulSession( session );
        session.setFocus( "agenda-group-1" );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( "rule2",
                      list.get( 0 ) );

        session = getSerialisedStatefulSession( session );
        session.setFocus( "agenda-group-2" );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( "rule3",
                      list.get( 1 ) );

        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( "rule1",
                      list.get( 2 ) );
    }

    @Test
    public void testRuleFlowGroups() throws Exception {
        String rule1 = "package org.test;\n";
        rule1 += "import org.drools.Cheese\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule \"Rule 1\"\n";
        rule1 += "    ruleflow-group \"ruleflow-group-1\"\n";
        rule1 += "when\n";
        rule1 += "    $c : Cheese( ) \n";
        rule1 += "then\n";
        rule1 += "    list.add( \"rule1\" );\n";
        rule1 += "    drools.halt();\n";
        rule1 += "end";

        String rule2 = "package org.test;\n";
        rule2 += "import org.drools.Cheese\n";
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

        String rule3 = "package org.test;\n";
        rule3 += "import org.drools.Cheese\n";
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

        String rule4 = "package org.test;\n";
        rule4 += "import org.drools.Cheese\n";
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

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule1 ) );
        builder.addPackageFromDrl( new StringReader( rule2 ) );
        builder.addPackageFromDrl( new StringReader( rule3 ) );
        builder.addPackageFromDrl( new StringReader( rule4 ) );

        final Package pkg = builder.getPackage();

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();

        session = getSerialisedStatefulSession( session );

        final List list = new ArrayList();
        session.setGlobal( "list",
                           list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        session.insert( brie );

        session = getSerialisedStatefulSession( session );
        session.getAgenda().activateRuleFlowGroup( "ruleflow-group-1" );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( "rule2",
                      list.get( 0 ) );

        session = getSerialisedStatefulSession( session );
        session.getAgenda().activateRuleFlowGroup( "ruleflow-group-2" );
        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( "rule3",
                      list.get( 1 ) );

        session = getSerialisedStatefulSession( session );
        session.fireAllRules();
        assertEquals( "rule1",
                      list.get( 2 ) );
    }

    @Test
    public void testAccumulate() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader( "package org.drools\n" + "\n" + "import org.drools.Message\n" + "global java.util.List results\n" + "\n" + "rule MyRule\n" + "  when\n"
                                          + "    $n : Number( intValue >= 2 ) from accumulate ( m: Message( ), count( m ) )\n" + "  then\n" + "    results.add($n);\n" + "end" );
        builder.addPackageFromDrl( source );
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();
        session.setGlobal( "results",
                           new ArrayList() );

        session = getSerialisedStatefulSession( session );
        session.insert( new Message() );
        session = getSerialisedStatefulSession( session );
        List results = (List) session.getGlobal( "results" );

        session.insert( new Message() );
        session.insert( new Message() );
        session.fireAllRules();
        assertEquals( 3,
                      ((Number) results.get( 0 )).intValue() );

        session = getSerialisedStatefulSession( session );

        session.insert( new Message() );
        session.insert( new Message() );
        session = getSerialisedStatefulSession( session );

        assertEquals( 1,
                      session.getAgenda().getActivations().length );
        session.fireAllRules();
        assertEquals( 5,
                      ((Number) results.get( 1 )).intValue() );
    }

    @Test
    public void testAccumulate2() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader( "package org.drools\n" + "\n" + "import org.drools.Message\n" + "\n" + "rule MyRule\n" + "  when\n" + "    Number( intValue >= 5 ) from accumulate ( m: Message( ), count( m ) )\n" + "  then\n"
                                          + "    System.out.println(\"Found messages\");\n" + "end" );
        builder.addPackageFromDrl( source );
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        StatefulSession session = ruleBase.newStatefulSession();
        session.fireAllRules();

        session = getSerialisedStatefulSession( session );
        session.insert( new Message() );
        session.insert( new Message() );
        session.insert( new Message() );
        session.insert( new Message() );
        session.insert( new Message() );

        assertEquals( 1,
                      session.getAgenda().getActivations().length );
    }

    @Test
    public void testAccumulateSessionSerialization() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "test_AccumulateSerialization.drl" ) ),
                      ResourceType.DRL );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

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
     * @see JBRULES-2048
     * 
     * @throws Exception
     */
    @Test @Ignore
    public void testDroolsObjectOutputInputStream() throws Exception {
        Person bob = new Person();

        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newClassPathResource( "org/drools/integrationtests/test_Serializable.drl" ),
                              ResourceType.DRL );

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );

        StatefulKnowledgeSession session = knowledgeBase.newStatefulKnowledgeSession();
        session.insert( bob );

        assertSame( "these two object references should be same",
                    bob,
                    session.getObjects().iterator().next() );

        Marshaller marshaller = createSerializableMarshaller( knowledgeBase );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new DroolsObjectOutputStream( baos );
        out.writeObject( bob );
        out.writeObject( knowledgeBase );
        marshaller.marshall( out,
                             session );
        out.flush();
        out.close();

        ObjectInputStream in = new DroolsObjectInputStream( new ByteArrayInputStream( baos.toByteArray() ) );
        Person deserializedBob = (Person) in.readObject();
        knowledgeBase = (KnowledgeBase) in.readObject();
        marshaller = createSerializableMarshaller( knowledgeBase );
        session = marshaller.unmarshall( in );
        assertSame( "these two object references should be same",
                    deserializedBob,
                    session.getObjects().iterator().next() );
        in.close();
    }

    @Test
    public void testAccumulateSerialization() {
        try {
            KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            knowledgeBuilder.add( ResourceFactory.newClassPathResource( "org/drools/integrationtests/test_SerializableAccumulate.drl" ),
                                  ResourceType.DRL );

            if ( knowledgeBuilder.hasErrors() ) {
                fail( knowledgeBuilder.getErrors().toString() );
            }

            KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
            knowledgeBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
            StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();
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
            Marshaller marshaller = createSerializableMarshaller( knowledgeBase );
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new DroolsObjectOutputStream( baos );
            out.writeObject( knowledgeBase );
            marshaller.marshall( out,
                                 ksession );
            out.flush();
            out.close();

            ObjectInputStream in = new DroolsObjectInputStream( new ByteArrayInputStream( baos.toByteArray() ) );
            knowledgeBase = (KnowledgeBase) in.readObject();
            marshaller = createSerializableMarshaller( knowledgeBase );
            ksession = marshaller.unmarshall( in );
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

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

    @Test
    public void testJBRULES_1946() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "Sample.drl" ) ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream( baos );

            oos.writeObject( kbase );
            oos.flush();
            oos.close();
            baos.flush();
            baos.close();

            byte[] serializedKb = baos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream( serializedKb );
            ObjectInputStream ois = new ObjectInputStream( bais );

            KnowledgeBase kb2 = (KnowledgeBase) ois.readObject();
        } catch ( OptionalDataException ode ) {
            ode.printStackTrace();
            fail( "EOF? " + ode.eof );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Unexpected exception: " + e.getMessage() );
        }
    }

    @Test
    public void testJBRULES_1946_2() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "Sample.drl" ) ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DroolsObjectOutputStream oos = new DroolsObjectOutputStream( baos );

            oos.writeObject( kbase );
            oos.flush();
            oos.close();
            baos.flush();
            baos.close();

            byte[] serializedKb = baos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream( serializedKb );
            DroolsObjectInputStream ois = new DroolsObjectInputStream( bais );

            KnowledgeBase kb2 = (KnowledgeBase) ois.readObject();
        } catch ( OptionalDataException ode ) {
            ode.printStackTrace();
            fail( "EOF? " + ode.eof );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Unexpected exception: " + e.getMessage() );
        }
    }

    @Test
    public void testJBRULES_1946_3() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource( getClass().getResourceAsStream( "Sample.drl" ) ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DroolsObjectOutputStream oos = new DroolsObjectOutputStream( baos );

            oos.writeObject( kbase );
            oos.flush();
            oos.close();
            baos.flush();
            baos.close();

            byte[] serializedKb = baos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream( serializedKb );
            ObjectInputStream ois = new ObjectInputStream( bais );

            KnowledgeBase kb2 = (KnowledgeBase) ois.readObject();
            fail( "Should have raised an IllegalArgumentException since the kbase was serialized with a Drools Stream but deserialized with a regular stream" );
        } catch ( IllegalArgumentException ode ) {
            // success
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Unexpected exception: " + e.getMessage() );
        }
    }

    @Test
    public void testJBRULES_2331() throws Exception {
        String source = "package test.drl\n";
        source += "rule dummy_rule\n";
        source += "when\n";
        source += "eval( false )\n";
        source += "then\n";
        source += "end\n";
        Reader reader = new StringReader( source );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( reader ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,
                                                                              true );

        assertNotNull( ksession );
        ksession.dispose();
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
            
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newReaderResource(new StringReader(str)),
                ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            throw new RuntimeException(kbuilder.getErrors().toString());
        }
        KnowledgeBaseConfiguration config = KnowledgeBaseFactory
                .newKnowledgeBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(config);
        knowledgeBase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();
        ksession.insert(new A());
        MarshallerFactory.newMarshaller(knowledgeBase).marshall(new ByteArrayOutputStream(), ksession);
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( str ) ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( kbuilder.getErrors().toString() );
        }
        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( config );
        knowledgeBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( "pseudo" ) );
        ((SessionConfiguration) ksconf).setTimerJobFactoryManager( new TrackableTimeJobFactoryManager( ) );
        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession(ksconf, null);
        
        ksession.insert( new A() );

        ksession = marsallStatefulKnowledgeSession(ksession);   
        
        ksession.insert( new B() );
        
        ksession = marsallStatefulKnowledgeSession(ksession);   
        
        ksession.fireAllRules();
        assertEquals( 2,
                      ksession.getObjects().size() );
    }
    
    @Test
    public void testScheduledActivation() {
        KnowledgeBaseImpl knowledgeBase = (KnowledgeBaseImpl) KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgePackageImp impl = new KnowledgePackageImp();
        impl.pkg = new org.drools.rule.Package("test");

        BuildContext buildContext = new BuildContext((InternalRuleBase) knowledgeBase.getRuleBase(), ((ReteooRuleBase) knowledgeBase.getRuleBase())
                .getReteooBuilder().getIdGenerator());
        //simple rule that fires after 10 seconds
        final Rule rule = new Rule("test-rule");
        new RuleTerminalNode(1,new MockTupleSource(2), rule, rule.getLhs(), 0, buildContext);
        
        final List<String> fired = new ArrayList<String>();
        
        rule.setConsequence( new Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) throws Exception {
                fired.add("a");
            }

            public String getName() {
                return "default";
            }
        } );
                
        rule.setTimer( new DurationTimer( 10000 ) );
        rule.setPackage("test");
        impl.pkg.addRule(rule);
        
        knowledgeBase.addKnowledgePackages(Collections.singleton((KnowledgePackage) impl));
        SessionConfiguration config = new SessionConfiguration();
        config.setClockType(ClockType.PSEUDO_CLOCK);
        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession(config, KnowledgeBaseFactory.newEnvironment());
        PseudoClockScheduler scheduler = ksession.getSessionClock();
        Marshaller marshaller = MarshallerFactory.newMarshaller(knowledgeBase);
        


        ksession.insert("cheese");
        assertTrue(fired.isEmpty());
        //marshall, then unmarshall session
        readWrite(knowledgeBase, ksession, config);
        //the activations should fire after 10 seconds
        assertTrue(fired.isEmpty());
        scheduler.advanceTime(12, TimeUnit.SECONDS);
        assertFalse(fired.isEmpty());

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
                        ""+
                        "rule b1\n" +
                        "when\n" +
                        "   $b : B() from entry-point 'b-ep'\n" +
                        "then\n" +
                        "list.add( $b );" +
                        "end\n" +  
                        ""+
                        "rule c1\n" +
                        "when\n" +
                        "   $c : C() from entry-point 'c-ep'\n" +
                        "then\n" +
                        "list.add( $c );" +
                        "end\n";           

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( str ) ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( kbuilder.getErrors().toString() );
        }                
        
        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( config );
        knowledgeBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        
        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( "pseudo" ) );
        ((SessionConfiguration) ksconf).setTimerJobFactoryManager( new TrackableTimeJobFactoryManager( ) );
        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession(ksconf, null);
        
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        WorkingMemoryEntryPoint aep = ksession.getWorkingMemoryEntryPoint( "a-ep" );
        aep.insert( new A() );
        
        ksession = marsallStatefulKnowledgeSession(ksession);        
        
        WorkingMemoryEntryPoint bep = ksession.getWorkingMemoryEntryPoint( "b-ep" );
        bep.insert( new B() );
        
        ksession = marsallStatefulKnowledgeSession(ksession);       
        
        WorkingMemoryEntryPoint cep = ksession.getWorkingMemoryEntryPoint( "c-ep" );
        cep.insert( new C() );

        ksession = marsallStatefulKnowledgeSession(ksession);     
        
        ksession.fireAllRules();
        
        ksession = marsallStatefulKnowledgeSession(ksession);          
        
        assertEquals( 3,
                      list.size() );
        
        aep = ksession.getWorkingMemoryEntryPoint( "a-ep" );
        assertEquals( 1, aep.getFactHandles().size() );
        
        bep = ksession.getWorkingMemoryEntryPoint( "b-ep" );
        assertEquals( 1, bep.getFactHandles().size() );
        
        cep = ksession.getWorkingMemoryEntryPoint( "c-ep" );
        assertEquals( 1, cep.getFactHandles().size() );
        
        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.getSessionClock();        
        timeService.advanceTime( 11, TimeUnit.SECONDS );       
        
        ksession = marsallStatefulKnowledgeSession(ksession);
        
        ksession.fireAllRules();
        
        ksession = marsallStatefulKnowledgeSession(ksession);        
        
        aep = ksession.getWorkingMemoryEntryPoint( "a-ep" );
        assertEquals( 0, aep.getFactHandles().size() );
        
        bep = ksession.getWorkingMemoryEntryPoint( "b-ep" );
        assertEquals( 0, bep.getFactHandles().size() );
        
        cep = ksession.getWorkingMemoryEntryPoint( "c-ep" );
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( str ) ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( kbuilder.getErrors().toString() );
        }                
        
        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( config );
        knowledgeBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        
        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( "pseudo" ) );
        ((SessionConfiguration) ksconf).setTimerJobFactoryManager( new TrackableTimeJobFactoryManager( ) );
        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession(ksconf, null);
        
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        WorkingMemoryEntryPoint aep = ksession.getWorkingMemoryEntryPoint( "a-ep" );
        aep.insert( new A() );
        
        ksession = marsallStatefulKnowledgeSession(ksession);
        
        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.getSessionClock();        
        timeService.advanceTime( 3, TimeUnit.SECONDS );  
        
        ksession = marsallStatefulKnowledgeSession(ksession);
        
        ksession.fireAllRules();
        
        ksession = marsallStatefulKnowledgeSession(ksession);
        
        assertEquals( 0,
                      list.size() );                
    }        
    
    @Test
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( str ) ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( kbuilder.getErrors().toString() );
        }                
        
        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( config );
        knowledgeBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        
        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( "pseudo" ) );
        ((SessionConfiguration) ksconf).setTimerJobFactoryManager( new TrackableTimeJobFactoryManager( ) );
        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession(ksconf, null);
        
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        WorkingMemoryEntryPoint aep = ksession.getWorkingMemoryEntryPoint( "a-ep" );
        aep.insert( new A() );
        ksession = marsallStatefulKnowledgeSession(ksession);
        
        aep = ksession.getWorkingMemoryEntryPoint( "a-ep" );
        aep.insert( new A() );
        ksession = marsallStatefulKnowledgeSession(ksession);
        
        list.clear();
        ksession.fireAllRules();
        ksession = marsallStatefulKnowledgeSession(ksession);
        assertEquals( 2, ((List)list.get( 0 )).size() );
        
        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.getSessionClock();        
        timeService.advanceTime( 15, TimeUnit.SECONDS );          
        ksession = marsallStatefulKnowledgeSession(ksession);
        
        aep = ksession.getWorkingMemoryEntryPoint( "a-ep" );
        aep.insert( new A() );
        ksession = marsallStatefulKnowledgeSession(ksession);
        
        aep = ksession.getWorkingMemoryEntryPoint( "a-ep" );
        aep.insert( new A() );
        ksession = marsallStatefulKnowledgeSession(ksession);
        
        list.clear();
        ksession.fireAllRules();
        ksession = marsallStatefulKnowledgeSession(ksession);
        assertEquals( 4, ((List)list.get( 0 )).size() );
        
        timeService = ( PseudoClockScheduler ) ksession.getSessionClock();        
        timeService.advanceTime( 20, TimeUnit.SECONDS );
        ksession = marsallStatefulKnowledgeSession(ksession);
        
        list.clear();
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( str ) ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( kbuilder.getErrors().toString() );
        }                
        
        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption( EventProcessingOption.STREAM );
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase( config );
        knowledgeBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        
        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconf.setOption( ClockTypeOption.get( "pseudo" ) );
        ((SessionConfiguration) ksconf).setTimerJobFactoryManager( new TrackableTimeJobFactoryManager( ) );
        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession(ksconf, null);
        
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        WorkingMemoryEntryPoint aep = ksession.getWorkingMemoryEntryPoint( "a-ep" );
        aep.insert( new A() );
        ksession = marsallStatefulKnowledgeSession(ksession);
        
        aep = ksession.getWorkingMemoryEntryPoint( "a-ep" );
        aep.insert( new A() );
        ksession = marsallStatefulKnowledgeSession(ksession);
        
        list.clear();
        ksession.fireAllRules();
        ksession = marsallStatefulKnowledgeSession(ksession);
        assertEquals( 2, ((List)list.get( 0 )).size() );
        
        aep = ksession.getWorkingMemoryEntryPoint( "a-ep" );
        aep.insert( new A() );
        ksession = marsallStatefulKnowledgeSession(ksession);
        
        aep = ksession.getWorkingMemoryEntryPoint( "a-ep" );
        aep.insert( new A() );
        ksession = marsallStatefulKnowledgeSession(ksession);
        
        list.clear();
        ksession.fireAllRules();
        ksession = marsallStatefulKnowledgeSession(ksession);
        assertEquals( 3, ((List)list.get( 0 )).size() );       
    }    
    
    private StatefulKnowledgeSession marsallStatefulKnowledgeSession(StatefulKnowledgeSession ksession) throws IOException, ClassNotFoundException {
        Globals globals = ksession.getGlobals();
        
        KnowledgeBase kbase = ksession.getKnowledgeBase();        
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MarshallerFactory.newMarshaller( kbase ).marshall( out,
                                                           ksession );
        
        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();        
        ((SessionConfiguration) ksconf).setTimerJobFactoryManager( new TrackableTimeJobFactoryManager( ) );
        ksconf.setOption( ClockTypeOption.get( "pseudo" ) ); 
        
        
        ksession = MarshallerFactory.newMarshaller( kbase ).unmarshall( new ByteArrayInputStream( out.toByteArray() ), ksconf, null );
        ((StatefulKnowledgeSessionImpl)ksession).session.setGlobalResolver( (GlobalResolver) globals );  
        
        return ksession;
    }
    
    private void readWrite(KnowledgeBase knowledgeBase, StatefulKnowledgeSession ksession, KnowledgeSessionConfiguration config) {
        try {
            Marshaller marshaller = MarshallerFactory.newMarshaller(knowledgeBase);
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            marshaller.marshall(o, ksession);
            ksession = marshaller.unmarshall(new ByteArrayInputStream(o.toByteArray()), config, KnowledgeBaseFactory.newEnvironment());
            ksession.fireAllRules();
            //scheduler = ksession.getSessionClock();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
         

    private Marshaller createSerializableMarshaller(KnowledgeBase knowledgeBase) {
        ObjectMarshallingStrategyAcceptor acceptor = MarshallerFactory.newClassFilterAcceptor( new String[]{ "*.*" } );
        ObjectMarshallingStrategy strategy = MarshallerFactory.newSerializeMarshallingStrategy( acceptor );
        Marshaller marshaller = MarshallerFactory.newMarshaller( knowledgeBase,
                                                                 new ObjectMarshallingStrategy[]{ strategy } );
        return marshaller;
    }

    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(Package pkg) throws Exception {
        RuleBase ruleBase = getRuleBase();

        ruleBase.addPackage( pkg );
        return SerializationHelper.serializeObject( ruleBase );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

}
