package org.drools.examples.waltz;

/*
 * Copyright 2006 Alexander Bagerman
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * @author Alexander Bagerman
 * 
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.drools.FactException;
import org.drools.PackageIntegrationException;
import org.drools.WorkingMemory;
import org.drools.rule.InvalidPatternException;

/**
 * 
 * @author Alexander Bagerman
 *
 */
public class LeapsWaltzTest extends BaseWaltzTest {
    
    public void testWaltz() throws PackageIntegrationException,
                             InvalidPatternException,
                             FactException,
                             IOException {

        final org.drools.leaps.RuleBaseImpl ruleBase = new org.drools.leaps.RuleBaseImpl();
        ruleBase.addRuleSet( this.pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        InputStream is = getClass().getResourceAsStream( "/waltz12.dat" );
        List list = getInputObjects( is );
        for ( Iterator it = list.iterator(); it.hasNext(); ) {
            Object object = it.next();
            workingMemory.assertObject( object );
        }

//        workingMemory.assertObject( new Stage(Stage.START) );

        long start = System.currentTimeMillis();
        workingMemory.fireAllRules();
//		System.out.println(workingMemory);
        System.out.println( "Elapsed time - " +( ( System.currentTimeMillis() - start ) / 1000.) + " sec.");

    }
    
    public static void main(String[] argv) throws Exception, PackageIntegrationException,
			InvalidPatternException, FactException {
		LeapsWaltzTest waltz = new LeapsWaltzTest();
		waltz.setUp();

		final org.drools.leaps.RuleBaseImpl ruleBase = new org.drools.leaps.RuleBaseImpl();
		ruleBase.addRuleSet(waltz.pkg);
		WorkingMemory workingMemory = ruleBase.newWorkingMemory();
		
		        InputStream is = waltz.getClass().getResourceAsStream( "/waltz12.dat" );
		        List list = waltz.getInputObjects( is );
		        for ( Iterator it = list.iterator(); it.hasNext(); ) {
		            Object object = it.next();
		            workingMemory.assertObject( object );
		        }

//		workingMemory.assertObject(new Stage(Stage.START));

		long start = System.currentTimeMillis();
		workingMemory.fireAllRules();
		System.err.println(System.currentTimeMillis() - start);

	}
}
