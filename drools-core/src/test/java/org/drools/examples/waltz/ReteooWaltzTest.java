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

public class ReteooWaltzTest extends BaseWaltzTest {
    
    public void testWaltz() throws DuplicateRuleNameException,
                             InvalidRuleException,
                             IntrospectionException,
                             RuleIntegrationException,
                             PackageIntegrationException,
                             InvalidPatternException,
                             FactException,
                             IOException,
                             InterruptedException {

        final org.drools.reteoo.RuleBaseImpl ruleBase = new org.drools.reteoo.RuleBaseImpl();
        ruleBase.addRuleSet( this.pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

//        InputStream is = getClass().getResourceAsStream( "/waltz12.dat" );
//        List list = getInputObjects( is );
//        for ( Iterator it = list.iterator(); it.hasNext(); ) {
//            Object object = it.next();
//            workingMemory.assertObject( object );
//        }

        workingMemory.assertObject( new Stage(Stage.START) );

        long start = System.currentTimeMillis();
        workingMemory.fireAllRules();
        System.err.println( System.currentTimeMillis() - start );

    }
}
