package org.drools.examples.waltz;

/*
 * Copyright 2005 JBoss Inc
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

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;

/**
 * 
 * @author Alexander Bagerman
 *
 */
public class ReteooWaltzTest extends BaseWaltzTest {

    public void testWaltz() throws Exception {

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase( RuleBase.RETEOO );
        ruleBase.addPackage( this.pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        //        InputStream is = getClass().getResourceAsStream( "/waltz12.dat" );
        //        List list = getInputObjects( is );
        //        for ( Iterator it = list.iterator(); it.hasNext(); ) {
        //            Object object = it.next();
        //            workingMemory.assertObject( object );
        //        }

        workingMemory.assertObject( new Stage( Stage.START ) );

        final long start = System.currentTimeMillis();
        workingMemory.fireAllRules();
        System.err.println( System.currentTimeMillis() - start );

    }
}