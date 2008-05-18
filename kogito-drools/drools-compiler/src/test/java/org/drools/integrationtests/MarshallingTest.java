package org.drools.integrationtests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import junit.framework.TestCase;

import org.drools.Address;
import org.drools.Cheese;
import org.drools.Person;
import org.drools.Primitives;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.base.MapGlobalResolver;
import org.drools.common.InternalFactHandle;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.marshalling.Marshaller;
import org.drools.rule.MapBackedClassLoader;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.spi.GlobalResolver;
import org.drools.util.DroolsStreamUtils;

public class MarshallingTest extends TestCase {

    public void testSerializable() throws Exception {

        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Serializable.drl" ) );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
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
        
        session = SerializationHelper.getSerialisedStatefulSession( session, ruleBase );  
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
        session = SerializationHelper.getSerialisedStatefulSession( session, ruleBase );

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
        session = SerializationHelper.getSerialisedStatefulSession( session, ruleBase );

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

        session = SerializationHelper.getSerialisedStatefulSession( session, ruleBase );
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
        session = SerializationHelper.getSerialisedStatefulSession( session, ruleBase );
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

        session = SerializationHelper.getSerialisedStatefulSession( session, ruleBase );

        assertEquals( 1,
                      IteratorToList.convert( session.iterateObjects() ).size() );
        assertEquals( p,
                      IteratorToList.convert( session.iterateObjects() ).get( 0 ) );

        assertEquals( 3,
                      session.getAgenda().agendaSize() );

        session = SerializationHelper.getSerialisedStatefulSession( session, ruleBase );
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
        Marshaller marshaller = new Marshaller();
        ruleBase.writeStatefulSession( session, baos, marshaller );
        GlobalResolver resolver = session.getGlobalResolver();
        byte[] serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );
        session.dispose();

        // now deserialize the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
        session = ruleBase.readStatefulSession( new ByteArrayInputStream( baos.toByteArray() ), marshaller );
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
        assertEquals( bob.getObject(),
                      results.get( 1 ) );
        assertEquals( stilton2.getObject(),
                      results.get( 2 ) );
        assertEquals( mark.getObject(),
                      results.get( 3 ) );

        serializedRulebase = null;

        session = SerializationHelper.getSerialisedStatefulSession( session, ruleBase );
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Marshaller marshaller = new Marshaller();        
        ruleBase.writeStatefulSession( session, baos, marshaller );
        byte[] serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

        session.dispose();

        assertEquals( 1,
                      results.size() );
        assertEquals( stilton1.getObject(),
                      results.get( 0 ) );

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
        session = ruleBase.readStatefulSession( new ByteArrayInputStream( baos.toByteArray() ), marshaller );
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
        assertEquals( brie1.getObject(),
                      results.get( 1 ) );
        assertEquals( stilton2.getObject(),
                      results.get( 2 ) );
        assertEquals( brie2.getObject(),
                      results.get( 4 ) );
        assertEquals( brie3.getObject(),
                      results.get( 3 ) );

        serializedRulebase = null;

        session = SerializationHelper.getSerialisedStatefulSession( session, ruleBase );
        serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

        session.dispose();
    }

    /*
     *  Works Fine if both the scenarios mentioned above are skipped.
     */
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
        Marshaller marshaller = new Marshaller();          
        ruleBase.writeStatefulSession( session, baos, marshaller );
        byte[] serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

        session.dispose();

        assertEquals( 1,
                      results.size() );
        assertEquals( stilton1.getObject(),
                      results.get( 0 ) );

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
        session = ruleBase.readStatefulSession( new ByteArrayInputStream( baos.toByteArray() ), marshaller );
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
        marshaller = new Marshaller();
        ruleBase.writeStatefulSession( session, baos, marshaller );        
        serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

        session.dispose();

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase ); 
        session = ruleBase.readStatefulSession( new ByteArrayInputStream( baos.toByteArray() ), marshaller );
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
        marshaller = new Marshaller();
        ruleBase.writeStatefulSession( session, baos, marshaller );
        serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );
        session.dispose();

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
        session = ruleBase.readStatefulSession( new ByteArrayInputStream( baos.toByteArray() ), marshaller );
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

        session = SerializationHelper.getSerialisedStatefulSession( session, ruleBase );
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
     *
     */
    public void testSerializeAddRemove_NoClassDefFoundError() throws Exception {

        //Create a rulebase, a session, and test it
        RuleBase ruleBase = RuleBaseFactory.newRuleBase( );
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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Marshaller marshaller = new Marshaller();
        ruleBase.writeStatefulSession( session, baos, marshaller );
        byte[] serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

        session.dispose();

        assertEquals( 1,
                      results.size() );
        assertEquals( stilton1.getObject(),
                      results.get( 0 ) );

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
        session = ruleBase.readStatefulSession( new ByteArrayInputStream( baos.toByteArray() ), marshaller );
        results.clear();
        session.setGlobal( "results", results );

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3_0.drl" ) ) );
        pkg = SerializationHelper.serializeObject( builder.getPackage() );

        ruleBase.addPackage( pkg );

        InternalFactHandle stilton2 = (InternalFactHandle) session.insert( new Cheese( "stilton", 20 ) );
        InternalFactHandle brie2 = (InternalFactHandle) session.insert( new Cheese( "brie", 20 ) );
        InternalFactHandle bob1 = (InternalFactHandle) session.insert( new Person( "bob", 20 ) );
        InternalFactHandle bob2 = (InternalFactHandle) session.insert( new Person( "bob", 30 ) );
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
        marshaller = new Marshaller();
        ruleBase.writeStatefulSession( session, baos, marshaller );
        serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

        session.dispose();

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
        session = ruleBase.readStatefulSession( new ByteArrayInputStream( baos.toByteArray() ), marshaller );
        results.clear();
        session.setGlobal( "results", results );

        // CASE 1: remove rule
        ruleBase.removeRule("org.drools.test", "like stilton");

        InternalFactHandle stilton3 = (InternalFactHandle) session.insert( new Cheese( "stilton", 20 ) );
        InternalFactHandle brie3 = (InternalFactHandle) session.insert( new Cheese( "brie", 20 ) );
        InternalFactHandle bob3 = (InternalFactHandle) session.insert( new Person( "bob", 20 ) );
        InternalFactHandle bob4 = (InternalFactHandle) session.insert( new Person( "bob", 30 ) );
        session.fireAllRules();

        assertEquals( 2,
                      results.size() );
        assertEquals( bob4.getObject(),
                      results.get( 0 ) );
        assertEquals( bob3.getObject(),
                      results.get( 1 ) );


        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
        session = ruleBase.readStatefulSession( new ByteArrayInputStream( baos.toByteArray() ), marshaller );
        results.clear();
        session.setGlobal( "results", results );

        // CASE 2: remove pkg
        ruleBase.removePackage("org.drools.test");

        InternalFactHandle stilton4 = (InternalFactHandle) session.insert( new Cheese( "stilton", 20 ) );
        InternalFactHandle brie4 = (InternalFactHandle) session.insert( new Cheese( "brie", 20 ) );
        InternalFactHandle bob5 = (InternalFactHandle) session.insert( new Person( "bob", 20 ) );
        InternalFactHandle bob6 = (InternalFactHandle) session.insert( new Person( "bob", 30 ) );
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
        marshaller = new Marshaller();
        ruleBase.writeStatefulSession( session, baos,marshaller  );
        serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );

        session.dispose();
        // Deserialize the rulebase and the session 
        ruleBase = (RuleBase) DroolsStreamUtils.streamIn( serializedRulebase );
        session = ruleBase.readStatefulSession( new ByteArrayInputStream( baos.toByteArray() ), marshaller );    //  throws java.lang.ClassNotFoundException Exception
        results.clear();
        session.setGlobal( "results", results );
             
        InternalFactHandle stilton5 = (InternalFactHandle) session.insert( new Cheese( "stilton", 30 ) );
        InternalFactHandle brie5 = (InternalFactHandle) session.insert( new Cheese( "brie", 30 ) );
        InternalFactHandle bob7 = (InternalFactHandle) session.insert( new Person( "bob", 30 ) );
        InternalFactHandle bob8 = (InternalFactHandle) session.insert( new Person( "bob", 40 ) );
        session.fireAllRules();
 
        assertEquals( 2,
                      results.size() );
        assertEquals( bob8.getObject(),
                      results.get( 0 ) );
        assertEquals( bob7.getObject(),
                      results.get( 1 ) );
       
        serializedSession = null;
        serializedRulebase = null;
       
        session = SerializationHelper.getSerialisedStatefulSession( session, ruleBase );
        serializedRulebase = DroolsStreamUtils.streamOut( ruleBase );
       
        session.dispose();        
       
    }    

    /**
     * In this case we are dealing with facts which are not on the systems classpath.
     *
     */
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

        String drl = "package foo.bar \n" + "import com.billasurf.Board\n" + "rule 'MyGoodRule' \n dialect 'mvel' \n when Board() then System.err.println(42); \n end\n";

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
