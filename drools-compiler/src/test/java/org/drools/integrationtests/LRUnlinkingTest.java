package org.drools.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.junit.Test;

public class LRUnlinkingTest {

    @Test
    public void multipleJoinsUsingSameOTN() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_LRUnlinking.drl" ) ) );
        final Package pkg = builder.getPackage();

        final RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setLRUnlinkingEnabled( true );
        RuleBase ruleBase = RuleBaseFactory.newRuleBase( conf );

        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        final WorkingMemory wmOne = ruleBase.newStatefulSession();
        final WorkingMemory wmTwo = ruleBase.newStatefulSession();

        final List<Person> listOne = new ArrayList<Person>();
        final List<Person> listTwo = new ArrayList<Person>();

        wmOne.setGlobal( "results",
                         listOne );
        wmTwo.setGlobal( "results",
                         listTwo );

        Person name = new Person();
        Person likes = new Person();
        Person age = new Person();
        Person hair = new Person();
        Person happy = new Person();
        Person match = new Person();

        name.setName( "Ana" );
        likes.setLikes( "Chocolate" );
        age.setAge( 30 );
        hair.setHair( "brown" );
        happy.setHappy( true );

        match.setName( "Leo" );
        match.setLikes( "Chocolate" );
        match.setAge( 30 );
        match.setHair( "brown" );
        match.setHappy( true );
        
        // WM One - first round of inserts
        wmOne.insert( name );
        wmOne.insert( likes );
        wmOne.insert( age );

        wmOne.fireAllRules();

        assertEquals( "Should not have fired",
                      0,
                      listOne.size() );

        // WM Two - first round o inserts
        wmTwo.insert( name );
        wmTwo.insert( likes );
        wmTwo.insert( age );

        wmTwo.fireAllRules();

        assertEquals( "Should not have fired",
                      0,
                      listTwo.size() );
        
        wmOne.insert( hair );
        wmOne.insert( happy );
        InternalFactHandle matchHandle = (InternalFactHandle) wmOne.insert( match );
        
        wmOne.fireAllRules();
        
        assertTrue( "Should have fired",
                      listOne.size() > 0);
                
        assertEquals("Should have inserted the match Person",
                     matchHandle.getObject(),
                     listOne.get( 0 ));
        
        wmTwo.fireAllRules();
        
        assertEquals( "Should not have fired",
                      0,
                      listTwo.size() );
        
        wmTwo.insert( hair );
        wmTwo.insert( happy );
        wmTwo.insert( match );
        
        wmTwo.fireAllRules();

        assertTrue( "Should have fired",
                    listTwo.size() > 0);

    }

}
