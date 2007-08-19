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
        
        shell.evalString( "(defrule xxx (Person (name ?name&bob) (age 30) )=> (printout t xx \" \" (eq 1 1) ) )" );
        
        Package pkg = shell.getWorkingMemory().getRuleBase().getPackage( "MAIN" );
        Rule rule = pkg.getRule( "xxx" );
        assertEquals( "xxx", rule.getName() );
        
        shell.evalString( "(defrule yyy => (printout t yy (eq 1 1) ) ) )" );
        rule = pkg.getRule( "yyy" );
        assertEquals( "yyy", rule.getName() );
        
        assertEquals( 2, pkg.getRules().length );
        
        assertTrue( pkg.getImports().contains( "org.drools.Person" ) );
        
        WorkingMemory wm = shell.getWorkingMemory();
        wm.insert( new Person("bob", "cheddar", 30) );
        wm.fireAllRules();
        
    }
}
