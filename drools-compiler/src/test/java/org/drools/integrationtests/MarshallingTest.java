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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.Cheese;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.drools.rule.Rule;

import junit.framework.TestCase;

public class MarshallingTest extends TestCase {
    public void testSerializable() throws Exception {

        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Serializable.drl" ) );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg = serialisePackage( builder.getPackage() );

        assertEquals( 0,
                      builder.getErrors().getErrors().length );

        RuleBase ruleBase = getRuleBase();// RuleBaseFactory.newRuleBase();

        ruleBase.addPackage( pkg );

        Map map = new HashMap();
        map.put( "x",
                 ruleBase );
        final byte[] ast = serializeOut( map );
        map = (Map) serializeIn( ast );
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

        final byte[] wm = serializeOut( workingMemory );

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
        final Package pkg = serialisePackage( builder.getPackage() );

        assertEquals( 0,
                      builder.getErrors().getErrors().length );

        RuleBase ruleBase = getRuleBase();// RuleBaseFactory.newRuleBase();

        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        Map map = new HashMap();
        map.put( "x",
                 ruleBase );
        final byte[] ast = serializeOut( map );
        map = (Map) serializeIn( ast );
        ruleBase = (RuleBase) map.get( "x" );

        final byte[] wm = serializeOut( workingMemory );

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

    public void testSerializeWorkingMemoryAndRuleBase2() throws Exception {
        // has the first newStatefulSession after the ruleBase is serialised
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_Serializable.drl" ) );

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( reader );
        final Package pkg = serialisePackage( builder.getPackage() );

        assertEquals( 0,
                      builder.getErrors().getErrors().length );

        RuleBase ruleBase = getRuleBase();// RuleBaseFactory.newRuleBase();   

        // serialise a hashmap with the RuleBase as a key
        Map map = new HashMap();
        map.put( "x",
                 ruleBase );
        final byte[] ast = serializeOut( map );
        map = (Map) serializeIn( ast );
        ruleBase = (RuleBase) map.get( "x" );

        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        // serialise the working memory before population
        final byte[] wm = serializeOut( workingMemory );
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
        final Package pkg = serialisePackage( builder.getPackage() );

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
        final byte[] ast = serializeOut( map );
        map = (Map) serializeIn( ast );
        ruleBase = (RuleBase) map.get( "x" );

        // now try serialising with a fully populated wm from a serialised rulebase
        final byte[] wm = serializeOut( workingMemory );
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
        Package pkg = serialisePackage( builder.getPackage() );
        ruleBase.addPackage( pkg );
        
        StatefulSession session = ruleBase.newStatefulSession();
        List list = new ArrayList();
        session.setGlobal( "list", list );
        
        InternalFactHandle stilton = (InternalFactHandle) session.insert( new Cheese( "stilton", 10 ) );
        InternalFactHandle brie = (InternalFactHandle) session.insert( new Cheese( "brie", 10 ) );
        session.fireAllRules();
        
        assertEquals( list.size(), 1 );
        assertEquals( "stilton", list.get( 0 ));
        
        byte[] serializedSession = serializeOut( session );
        session.dispose();
        
        byte[] serializedRulebase = serializeOut( ruleBase );
        
        // now recreate the rulebase, deserialize the session and test it
        ruleBase = (RuleBase) serializeIn( serializedRulebase );
        
        session = ruleBase.newStatefulSession( new ByteArrayInputStream( serializedSession ) );
        list = (List) session.getGlobal( "list" );
        
        assertNotNull( list );
        assertEquals( list.size(), 1 );
        assertEquals( "stilton", list.get( 0 ));
        
        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Dynamic3.drl" ) ) );
        pkg = serialisePackage( builder.getPackage() );
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

    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }
    
    protected Package serialisePackage(Package pkg) {
        try {
            byte[] bytes = serializeOut( pkg );
            return (Package) serializeIn( bytes );
        } catch ( Exception e ) {
            throw new RuntimeException( "trouble serialising package.",
                                        e );
        }
    }    

    protected Object serializeIn(final byte[] bytes) throws IOException,
                                                    ClassNotFoundException {
        final ObjectInput in = new ObjectInputStream( new ByteArrayInputStream( bytes ) );
        final Object obj = in.readObject();
        in.close();
        return obj;
    }

    protected byte[] serializeOut(final Object obj) throws IOException {
        // Serialize to a byte array
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutput out = new ObjectOutputStream( bos );
        out.writeObject( obj );
        out.close();

        // Get the bytes of the serialized object
        final byte[] bytes = bos.toByteArray();
        return bytes;
    }
}
