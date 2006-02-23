package org.drools.examples.waltz;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.drools.FactException;
import org.drools.PackageIntegrationException;
import org.drools.RuleIntegrationException;
import org.drools.WorkingMemory;
import org.drools.rule.DuplicateRuleNameException;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.InvalidRuleException;

public class LeapsWaltzTest extends BaseWaltzTest {
    
    public void testManners() throws DuplicateRuleNameException,
                             InvalidRuleException,
                             IntrospectionException,
                             RuleIntegrationException,
                             PackageIntegrationException,
                             InvalidPatternException,
                             FactException,
                             IOException,
                             InterruptedException {

        final org.drools.leaps.RuleBaseImpl ruleBase = new org.drools.leaps.RuleBaseImpl();
        ruleBase.addRuleSet( this.pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

//        InputStream is = getClass().getResourceAsStream( "/manners64.dat" );
//        List list = getInputObjects( is );
//        for ( Iterator it = list.iterator(); it.hasNext(); ) {
//            Object object = it.next();
//            workingMemory.assertObject( object );
//        }
//
        workingMemory.assertObject( new Stage(Stage.START) );

        long start = System.currentTimeMillis();
        workingMemory.fireAllRules();
        System.err.println( System.currentTimeMillis() - start );

    }
}
