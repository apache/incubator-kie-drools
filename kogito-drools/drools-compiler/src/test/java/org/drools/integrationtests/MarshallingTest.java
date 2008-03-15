package org.drools.integrationtests;

import junit.framework.TestCase;
import org.drools.Cheese;
import org.drools.Person;
import org.drools.Primitives;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.drools.rule.Rule;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(Package pkg) throws Exception {
        RuleBase    ruleBase    = getRuleBase();

        ruleBase.addPackage(pkg);
        return SerializationHelper.serializeObject(ruleBase);
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }
}
