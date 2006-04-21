package com.sample;

import java.io.InputStreamReader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.compiler.PackageBuilder;

public class StateExampleWithDynamicRules {

    /**
     * @param args
     */
    public static void main(String[] args) throws  Exception {
        
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( StateExampleWithDynamicRules.class.getResourceAsStream( "/StateExampleUsingSalience.drl" )) );        
        
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        
        WorkingMemory workingMemory = ruleBase.newWorkingMemory( );
        
        WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger(workingMemory);
        logger.setFileName("log/state");

        State a = new State( "A" );
        State b = new State( "B" );
        State c = new State( "C" );
        State d = new State( "D" );
        State e = new State( "E" );

        // By setting dynamic to TRUE, Drools will use JavaBean
        // PropertyChangeListeners so you don't have to call modifyObject().
        boolean dynamic = true;

        workingMemory.assertObject( a, dynamic );
        workingMemory.assertObject( b, dynamic );
        workingMemory.assertObject( c, dynamic );
        workingMemory.assertObject( d, dynamic );
        workingMemory.assertObject( e, dynamic );

        workingMemory.fireAllRules( );
        
        builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( StateExampleWithDynamicRules.class.getResourceAsStream( "/StateExampleDynamicRule.drl" )) );
        ruleBase.addPackage( builder.getPackage() );
        
        workingMemory.fireAllRules( );
        
        logger.writeToDisk();
    }

}
