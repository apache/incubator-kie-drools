package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.Person;

import org.drools.WorkingMemory;
import org.drools.compiler.DrlParser;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;

public class DynamicRuleTest extends TestCase {
        public void testDrl() throws Exception {
            Reader reader = new InputStreamReader( getClass().getResourceAsStream( "dynamic1.drl" ) );
                                                
            PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( reader );            
            Package pkg1 = builder.getPackage();
            
            org.drools.reteoo.RuleBaseImpl ruleBase = new org.drools.reteoo.RuleBaseImpl();
            ruleBase.addPackage( pkg1 );
            WorkingMemory workingMemory = ruleBase.newWorkingMemory();
            workingMemory.setGlobal( "total", new Integer(0) );
            
            Cheese stilton = new Cheese("stilton", 5);
            workingMemory.assertObject( stilton );
            
            Cheese cheddar = new Cheese("cheddar", 5);
            workingMemory.assertObject( cheddar );            
            workingMemory.fireAllRules();
            
            reader = new InputStreamReader( getClass().getResourceAsStream( "dynamic2.drl" ) );
            builder = new PackageBuilder();
            builder.addPackageFromDrl( reader );
            Package pkg2 = builder.getPackage();
            ruleBase.addPackage( pkg2 );
            
            reader = new InputStreamReader( getClass().getResourceAsStream( "dynamic3.drl" ) );
            builder = new PackageBuilder();
            builder.addPackageFromDrl( reader );
            Package pkg3 = builder.getPackage();
            ruleBase.addPackage( pkg3 );      
            
            Person bob = new Person("bob", "stilton");
            workingMemory.assertObject( bob );  
            
            workingMemory.fireAllRules();
            
            reader = new InputStreamReader( getClass().getResourceAsStream( "dynamic4.drl" ) );
            builder = new PackageBuilder();
            builder.addPackageFromDrl( reader );
            Package pkg4 = builder.getPackage();
            ruleBase.addPackage( pkg4 );              
        }
}
