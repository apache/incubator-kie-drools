package org.drools.integrationtests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
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
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.InternalFactHandle;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.rule.MapBackedClassLoader;
import org.drools.rule.Package;
import org.drools.rule.Rule;

public class MarshallingTest extends TestCase {
    public void testSerializable() throws Exception {

        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Serializable.drl" ) );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg = SerializationHelper.serializeObject( builder.getPackage());

        assertEquals( 0,
                      builder.getErrors().getErrors().length );

        RuleBase ruleBase = getRuleBase(pkg);// RuleBaseFactory.newRuleBase();

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

        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        workingMemory.setGlobal( "list",
                                 new ArrayList() );

        final Person bob = new Person( "bob" );
        workingMemory.insert( bob );

        final byte[] wm = SerializationHelper.serializeOut( workingMemory );

        workingMemory = ruleBase.newStatefulSession( new ByteArrayInputStream( wm ) );

        assertEquals( 1,
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );
        assertEquals( bob,
                      IteratorToList.convert( workingMemory.iterateObjects() ).get( 0 ) );

        assertEquals( 2,
                      workingMemory.getAgenda().agendaSize() );

        workingMemory.fireAllRules();

        final List list = (List) workingMemory.getGlobal( "list" );

        assertEquals( 3,
                      list.size() );
        // because of agenda-groups
        assertEquals( new Integer( 4 ),
                      list.get( 0 ) );

        assertEquals( 2,
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );
        assertTrue( IteratorToList.convert( workingMemory.iterateObjects() ).contains( bob ) );
        assertTrue( IteratorToList.convert( workingMemory.iterateObjects() ).contains( new Person( "help" ) ) );
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


        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        Map map = new HashMap();
        map.put( "x",
                 ruleBase );
        map = SerializationHelper.serializeObject(map);
        ruleBase = (RuleBase) map.get( "x" );

        final byte[] wm = SerializationHelper.serializeOut( workingMemory );

        workingMemory = ruleBase.newStatefulSession( new ByteArrayInputStream( wm ) );

        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);

        workingMemory.setGlobal( "list",
                                 new ArrayList() );

        final Person bob = new Person( "bob" );
        workingMemory.insert( bob );

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
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );
        assertEquals( bob,
                      IteratorToList.convert( workingMemory.iterateObjects() ).get( 0 ) );

        assertEquals( 2,
                      workingMemory.getAgenda().agendaSize() );

        workingMemory.fireAllRules();

        final List list = (List) workingMemory.getGlobal( "list" );

        assertEquals( 3,
                      list.size() );
        // because of agenda-groups
        assertEquals( new Integer( 4 ),
                      list.get( 0 ) );

        assertEquals( 2,
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );
        assertTrue( IteratorToList.convert( workingMemory.iterateObjects() ).contains( bob ) );
        assertTrue( IteratorToList.convert( workingMemory.iterateObjects() ).contains( new Person( "help" ) ) );

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

        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        // serialise the working memory before population
        final byte[] wm = SerializationHelper.serializeOut( workingMemory );
        workingMemory = ruleBase.newStatefulSession( new ByteArrayInputStream( wm ) );

        ruleBase.addPackage( pkg );

        workingMemory.setGlobal( "list",
                                 new ArrayList() );

        final Person bob = new Person( "bob" );
        workingMemory.insert( bob );

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
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );
        assertEquals( bob,
                      IteratorToList.convert( workingMemory.iterateObjects() ).get( 0 ) );

        assertEquals( 2,
                      workingMemory.getAgenda().agendaSize() );

        workingMemory.fireAllRules();

        final List list = (List) workingMemory.getGlobal( "list" );

        assertEquals( 3,
                      list.size() );
        // because of agenda-groups
        assertEquals( new Integer( 4 ),
                      list.get( 0 ) );

        assertEquals( 2,
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );
        assertTrue( IteratorToList.convert( workingMemory.iterateObjects() ).contains( bob ) );
        assertTrue( IteratorToList.convert( workingMemory.iterateObjects() ).contains( new Person( "help" ) ) );
    }

    public void FIXME_testSerializeWorkingMemoryAndRuleBase3() throws Exception {
        // has the first newStatefulSession after the ruleBase is serialised
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Serializable.drl" ) );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg = SerializationHelper.serializeObject( builder.getPackage());

        assertEquals( 0,
                      builder.getErrors().getErrors().length );

        RuleBase ruleBase = getRuleBase();
        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        ruleBase.addPackage( pkg );

        workingMemory.setGlobal( "list",
                                 new ArrayList() );

        final Person bob = new Person( "bob" );
        workingMemory.insert( bob );

        // serialise a hashmap with the RuleBase as a key, after WM population
        Map map = new HashMap();
        map.put( "x",
                 ruleBase );
        map = SerializationHelper.serializeObject( map );
        ruleBase = (RuleBase) map.get( "x" );

        // now try serialising with a fully populated wm from a serialised rulebase
        final byte[] wm = SerializationHelper.serializeOut( workingMemory );
        workingMemory = ruleBase.newStatefulSession( new ByteArrayInputStream( wm ) );

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
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );
        assertEquals( bob,
                      IteratorToList.convert( workingMemory.iterateObjects() ).get( 0 ) );

        assertEquals( 2,
                      workingMemory.getAgenda().agendaSize() );

        workingMemory.fireAllRules();

        final List list = (List) workingMemory.getGlobal( "list" );

        assertEquals( 3,
                      list.size() );
        // because of agenda-groups
        assertEquals( new Integer( 4 ),
                      list.get( 0 ) );

        assertEquals( 2,
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );
        assertTrue( IteratorToList.convert( workingMemory.iterateObjects() ).contains( bob ) );
        assertTrue( IteratorToList.convert( workingMemory.iterateObjects() ).contains( new Person( "help" ) ) );
    }

    public void testSerializeAdd() throws Exception {

        //Create a rulebase, a session, and test it
        RuleBase ruleBase = RuleBaseFactory.newRuleBase( );
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1.drl" ) ) );
        Package pkg = SerializationHelper.serializeObject( builder.getPackage());
        ruleBase.addPackage( pkg );
        ruleBase    = SerializationHelper.serializeObject(ruleBase);

        StatefulSession session = ruleBase.newStatefulSession();
        List list = new ArrayList();
        session.setGlobal( "list", list );

        InternalFactHandle stilton = (InternalFactHandle) session.insert( new Cheese( "stilton", 10 ) );
        InternalFactHandle brie = (InternalFactHandle) session.insert( new Cheese( "brie", 10 ) );
        session.fireAllRules();

        assertEquals( list.size(), 1 );
        assertEquals( "stilton", list.get( 0 ));

        byte[] serializedSession = SerializationHelper.serializeOut( session );
        session.dispose();

        // now recreate the rulebase, deserialize the session and test it
        session = ruleBase.newStatefulSession( new ByteArrayInputStream( serializedSession ) );
        list = (List) session.getGlobal( "list" );

        assertNotNull( list );
        assertEquals( list.size(), 1 );
        assertEquals( "stilton", list.get( 0 ));

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3.drl" ) ) );
        pkg = SerializationHelper.serializeObject( builder.getPackage());
        ruleBase.addPackage( pkg );

        InternalFactHandle stilton2 = (InternalFactHandle) session.insert( new Cheese( "stilton", 10 ) );
        InternalFactHandle brie2 = (InternalFactHandle) session.insert( new Cheese( "brie", 10 ) );
        InternalFactHandle bob = (InternalFactHandle) session.insert( new Person( "bob", 30 ) );
        session.fireAllRules();

        assertEquals( list.size(), 3 );
        assertEquals( bob.getObject(), list.get( 1 ));
        assertEquals( "stilton", list.get( 2 ));

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

        RuleBase ruleBase = getRuleBase(pkg);// RuleBaseFactory.newRuleBase();

        Map map = new HashMap();
        map.put( "x",
                 ruleBase );
        map = SerializationHelper.serializeObject( map );
        ruleBase = (RuleBase) map.get( "x" );
        final Rule[] rules = ruleBase.getPackages()[0].getRules();
        assertEquals( 3,
                      rules.length );

        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        workingMemory.setGlobal( "list",
                                 new ArrayList() );

        final Primitives p = new Primitives( );
        p.setBytePrimitive( (byte) 1 );
        p.setShortPrimitive( (short) 2 );
        p.setIntPrimitive( (int) 3 );
        workingMemory.insert( p );

        final byte[] wm = SerializationHelper.serializeOut( workingMemory );

        workingMemory = ruleBase.newStatefulSession( new ByteArrayInputStream( wm ) );

        assertEquals( 1,
                      IteratorToList.convert( workingMemory.iterateObjects() ).size() );
        assertEquals( p,
                      IteratorToList.convert( workingMemory.iterateObjects() ).get( 0 ) );

        assertEquals( 3,
                      workingMemory.getAgenda().agendaSize() );

        workingMemory.fireAllRules();

        final List list = (List) workingMemory.getGlobal( "list" );

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
        byte[] serializedSession = SerializationHelper.serializeOut( session );
        session.dispose();

        // now deserialize the rulebase, deserialize the session and test it
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        session = ruleBase.newStatefulSession( new ByteArrayInputStream( serializedSession ) );

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

        serializedSession = SerializationHelper.serializeOut( session );
        SerializationHelper.serializeOut( ruleBase );

        // dispose session
        session.dispose();

    }

    /**
     * In this case we are dealing with facts which are not on the systems classpath.
     *
     */
    public void testSerializabilityWithJarFacts() throws Exception {
        MapBackedClassLoader loader = new MapBackedClassLoader( this.getClass().getClassLoader() );

        JarInputStream jis = new JarInputStream(this.getClass().getResourceAsStream("/billasurf.jar"));

        JarEntry entry = null;
        byte[] buf = new byte[1024];
        int len = 0;
        while ( (entry = jis.getNextJarEntry()) != null ) {
            if ( !entry.isDirectory() ) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while ( (len = jis.read( buf )) >= 0 ) {
                    out.write( buf, 0, len );
                }
                loader.addResource( entry.getName() , out.toByteArray() );
            }
        }

        String drl = "package foo.bar \n" +
        			"import com.billasurf.Board\n" +
        			"rule 'MyGoodRule' \n dialect 'mvel' \n when Board() then System.err.println(42); \n end\n";

        PackageBuilder builder = new PackageBuilder(new PackageBuilderConfiguration(loader));
        builder.addPackageFromDrl(new StringReader(drl));
        assertFalse(builder.hasErrors());

        Package p = builder.getPackage();
        byte[] ser = SerializationHelper.serializeOut(p);

        //now read it back
        DroolsObjectInputStream in = new DroolsObjectInputStream(new ByteArrayInputStream(ser), loader);
        Package p_ = (Package) in.readObject();
        assertNotNull(p_);

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

        byte[] serializedSession = SerializationHelper.serializeOut( session );
        byte[] serializedRulebase = SerializationHelper.serializeOut( ruleBase );

        session.dispose();

        assertEquals( 1,
                      results.size() );
        assertEquals( stilton1.getObject(),
                      results.get( 0 ) );

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) SerializationHelper.serializeIn( serializedRulebase );
        session = ruleBase.newStatefulSession( new ByteArrayInputStream( serializedSession ) );
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

        serializedSession = null;
        serializedRulebase = null;

        serializedSession = SerializationHelper.serializeOut( session );
        serializedRulebase = SerializationHelper.serializeOut( ruleBase );

        session.dispose();
    }

    /*
     *  Works Fine if both the scenarios mentioned above are skipped.
     */
    public void testSerializeAdd3() throws Exception {
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

        byte[] serializedSession = SerializationHelper.serializeOut( session );
        byte[] serializedRulebase = SerializationHelper.serializeOut( ruleBase );

        session.dispose();

        assertEquals( 1,
                      results.size() );
        assertEquals( stilton1.getObject(),
                      results.get( 0 ) );

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) SerializationHelper.serializeIn( serializedRulebase );
        session = ruleBase.newStatefulSession( new ByteArrayInputStream( serializedSession ) );
        results = (List) session.getGlobal( "results" );

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3_0.drl" ) ) );
        pkg = SerializationHelper.serializeObject( builder.getPackage() );

        ruleBase.addPackage( pkg );

        InternalFactHandle stilton2 = (InternalFactHandle) session.insert( new Cheese( "stilton", 20 ) );
        InternalFactHandle brie2 = (InternalFactHandle) session.insert( new Cheese( "brie", 20 ) );
        InternalFactHandle bob1 = (InternalFactHandle) session.insert( new Person( "bob", 20 ) );
        InternalFactHandle bob2 = (InternalFactHandle) session.insert( new Person( "bob", 30 ) );
        session.fireAllRules();

        assertEquals( 4,
                      results.size() );
        assertEquals( stilton2.getObject(),
                      results.get( 1 ) );
        assertEquals( bob2.getObject(),
                      results.get( 2 ) );
        assertEquals( bob1.getObject(),
                      results.get( 3 ) );

        serializedSession = null;
        serializedRulebase = null;

        serializedSession = SerializationHelper.serializeOut( session );
        serializedRulebase = SerializationHelper.serializeOut( ruleBase );

        session.dispose();

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) SerializationHelper.serializeIn( serializedRulebase );
        session = ruleBase.newStatefulSession( new ByteArrayInputStream( serializedSession ) );
        results = (List) session.getGlobal( "results" );

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic1_2.drl" ) ) );
        pkg = SerializationHelper.serializeObject( builder.getPackage() );
        ruleBase.addPackage( pkg );

        InternalFactHandle stilton3 = (InternalFactHandle) session.insert( new Cheese( "stilton", 40 ) );
        InternalFactHandle brie3 = (InternalFactHandle) session.insert( new Cheese( "brie", 40 ) );
        InternalFactHandle bob3 = (InternalFactHandle) session.insert( new Person( "bob", 40 ) );
        InternalFactHandle bob4 = (InternalFactHandle) session.insert( new Person( "bob", 40 ) );
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

        serializedSession = null;
        serializedRulebase = null;

        serializedSession = SerializationHelper.serializeOut( session );
        serializedRulebase = SerializationHelper.serializeOut( ruleBase );

        session.dispose();

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) SerializationHelper.serializeIn( serializedRulebase );
        session = ruleBase.newStatefulSession( new ByteArrayInputStream( serializedSession ) );
        results = (List) session.getGlobal( "results" );

        InternalFactHandle stilton4 = (InternalFactHandle) session.insert( new Cheese( "stilton", 50 ) );
        InternalFactHandle brie4 = (InternalFactHandle) session.insert( new Cheese( "brie", 50 ) );
        InternalFactHandle bob5 = (InternalFactHandle) session.insert( new Person( "bob", 50 ) );
        InternalFactHandle bob6 = (InternalFactHandle) session.insert( new Person( "bob", 50 ) );
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

        serializedSession = null;
        serializedRulebase = null;

        serializedSession = SerializationHelper.serializeOut( session );
        serializedRulebase = SerializationHelper.serializeOut( ruleBase );

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

        byte[] serializedSession = SerializationHelper.serializeOut( session );
        byte[] serializedRulebase = SerializationHelper.serializeOut( ruleBase );

        session.dispose();

        assertEquals( 1,
                      results.size() );
        assertEquals( stilton1.getObject(),
                      results.get( 0 ) );

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) SerializationHelper.serializeIn( serializedRulebase );
        session = ruleBase.newStatefulSession( new ByteArrayInputStream( serializedSession ) );
        results = (List) session.getGlobal( "results" );

        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3_0.drl" ) ) );
        pkg = SerializationHelper.serializeObject( builder.getPackage() );

        ruleBase.addPackage( pkg );

        InternalFactHandle stilton2 = (InternalFactHandle) session.insert( new Cheese( "stilton", 20 ) );
        InternalFactHandle brie2 = (InternalFactHandle) session.insert( new Cheese( "brie", 20 ) );
        InternalFactHandle bob1 = (InternalFactHandle) session.insert( new Person( "bob", 20 ) );
        InternalFactHandle bob2 = (InternalFactHandle) session.insert( new Person( "bob", 30 ) );
        session.fireAllRules();

        assertEquals( 4,
                      results.size() );
        assertEquals( stilton2.getObject(),
                      results.get( 1 ) );
        assertEquals( bob2.getObject(),
                      results.get( 2 ) );
        assertEquals( bob1.getObject(),
                      results.get( 3 ) );

        serializedSession = null;
        serializedRulebase = null;

        serializedSession = SerializationHelper.serializeOut( session );
        serializedRulebase = SerializationHelper.serializeOut( ruleBase );

        session.dispose();

        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) SerializationHelper.serializeIn( serializedRulebase );
        session = ruleBase.newStatefulSession( new ByteArrayInputStream( serializedSession ) );
        results = (List) session.getGlobal( "results" );

        // CASE 1: remove rule
        ruleBase.removeRule("org.drools.test", "like stilton");

        InternalFactHandle stilton3 = (InternalFactHandle) session.insert( new Cheese( "stilton", 20 ) );
        InternalFactHandle brie3 = (InternalFactHandle) session.insert( new Cheese( "brie", 20 ) );
        InternalFactHandle bob3 = (InternalFactHandle) session.insert( new Person( "bob", 20 ) );
        InternalFactHandle bob4 = (InternalFactHandle) session.insert( new Person( "bob", 30 ) );
        session.fireAllRules();

        assertEquals( 6,
                      results.size() );
        assertEquals( bob4.getObject(),
                      results.get( 4 ) );
        assertEquals( bob3.getObject(),
                      results.get( 5 ) );


        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) SerializationHelper.serializeIn( serializedRulebase );
        session = ruleBase.newStatefulSession( new ByteArrayInputStream( serializedSession ) );
        results = (List) session.getGlobal( "results" );

        // CASE 2: remove pkg
        ruleBase.removePackage("org.drools.test");

        InternalFactHandle stilton4 = (InternalFactHandle) session.insert( new Cheese( "stilton", 20 ) );
        InternalFactHandle brie4 = (InternalFactHandle) session.insert( new Cheese( "brie", 20 ) );
        InternalFactHandle bob5 = (InternalFactHandle) session.insert( new Person( "bob", 20 ) );
        InternalFactHandle bob6 = (InternalFactHandle) session.insert( new Person( "bob", 30 ) );
        session.fireAllRules();

        assertEquals( 6,
                      results.size() );
        assertEquals( bob6.getObject(),
                      results.get( 4 ) );
        assertEquals( bob5.getObject(),
                      results.get( 5 ) );

        serializedSession = null;
        serializedRulebase = null;

        serializedSession = SerializationHelper.serializeOut( session );
        serializedRulebase = SerializationHelper.serializeOut( ruleBase );

        session.dispose();

    }



    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    protected RuleBase getRuleBase(Package pkg) throws Exception {
        RuleBase    ruleBase    = getRuleBase();

        ruleBase.addPackage(pkg);
        return SerializationHelper.serializeObject(ruleBase);
     }

}
