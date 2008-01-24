package org.drools.clp;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.rule.Package;
import org.drools.rule.Rule;

public class ShellTest extends TestCase {
    public void test1() {
        Shell shell = new Shell();
        
        shell.evalString( "(import org.drools.Person)" );
        
        shell.evalString( "(defrule yyy  => (printout t yy \" \" (eq 1 1) ) ) )" );        
        Package pkg = shell.getWorkingMemory().getRuleBase().getPackage( "MAIN" );
        
        Rule rule = pkg.getRule( "yyy" );
        assertEquals( "yyy", rule.getName() );
        
        shell.evalString( "(defrule xxx (Person (name ?name&bob) (age 30) ) (Person  (name ?name) (age 35)) => (printout t xx \" \" (eq 1 1) ) )" );
        
        
        rule = pkg.getRule( "xxx" );
        assertEquals( "xxx", rule.getName() );
        

        
        assertEquals( 2, pkg.getRules().length );
        
        assertTrue( pkg.getImports().containsKey( "org.drools.Person" ) );
        
        WorkingMemory wm = shell.getWorkingMemory();
        wm.insert( new Person("bob", "cheddar", 30) );
        wm.insert( new Person("bob", "stilton", 35) );        
        wm.fireAllRules();
        
    }
}
