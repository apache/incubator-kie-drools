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

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.drools.WorkingMemory;

/**
 * 
 * @author Alexander Bagerman
 *
 */
public class LeapsWaltzTest extends BaseWaltzTest {

    public void testWaltz() throws Exception {

        final org.drools.leaps.LeapsRuleBase ruleBase = new org.drools.leaps.LeapsRuleBase();
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

        System.out.println( "Elapsed time - " + ((System.currentTimeMillis() - start) / 1000.) + " sec." );

    }

    public static void main(final String[] argv) throws Exception {
        final LeapsWaltzTest waltz = new LeapsWaltzTest();
        waltz.setUp();

        final org.drools.leaps.LeapsRuleBase ruleBase = new org.drools.leaps.LeapsRuleBase();
        ruleBase.addPackage( waltz.pkg );
        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final InputStream is = waltz.getClass().getResourceAsStream( "/waltz12.dat" );
        final List list = waltz.getInputObjects( is );
        for ( final Iterator it = list.iterator(); it.hasNext(); ) {
            final Object object = it.next();
            workingMemory.assertObject( object );
        }

        //		workingMemory.assertObject(new Stage(Stage.START));

        final long start = System.currentTimeMillis();
        workingMemory.fireAllRules();
        System.err.println( System.currentTimeMillis() - start );

    }
}