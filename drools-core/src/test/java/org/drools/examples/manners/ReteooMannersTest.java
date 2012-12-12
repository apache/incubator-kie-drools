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

package org.drools.examples.manners;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.DefaultAgendaEventListener;
import org.junit.Test;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class ReteooMannersTest extends BaseMannersTest {

    @Test
    public void testManners() throws Exception {

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase( RuleBase.RETEOO );
        ruleBase.addPackage( this.pkg );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final DefaultAgendaEventListener listener = new DefaultAgendaEventListener() {
            private int counter = 0;

            //           public void matchCreated(ActivationCreatedEvent event) {
            //                super.matchCreated( event );
            //                System.out.println( event );
            //            }
            //           
            //           public void matchCancelled(ActivationCancelledEvent event) {
            //               super.matchCancelled( event );
            //               System.out.println( event );
            //           }
            //           
            //           public void beforeMatchFired(BeforeActivationFiredEvent event) {
            //               super.beforeMatchFired( event );
            //               System.out.println( event );
            //           }

            public void afterActivationFired(AfterActivationFiredEvent event) {
                this.counter++;
                //super.afterMatchFired( event );
                //System.out.println( event );
            }

            public String toString() {
                return "fired :  " + this.counter;
            }

        };

        //workingMemory.addEventListener(listener );
        final InputStream is = getClass().getResourceAsStream( "/manners5.dat" );
        final List list = getInputObjects( is );
        for ( final Iterator it = list.iterator(); it.hasNext(); ) {
            final Object object = it.next();
            workingMemory.insert( object );
        }

        workingMemory.insert( new Count( 1 ) );

        final long start = System.currentTimeMillis();
        workingMemory.fireAllRules();
//        System.err.println( System.currentTimeMillis() - start );

        //System.out.println( listener );

        //        while  (1==1){
        //            Thread.yield();
        //            Thread.sleep( 2000 );
        //        }

        //        final MemoryVisitor visitor = new MemoryVisitor( (InternalWorkingMemory) workingMemory );
        //        visitor.visit( ruleBase );

        //        final ReteooJungViewer viewer = new ReteooJungViewer(ruleBase);
        //        
        //        javax.swing.SwingUtilities.invokeLater(new Runnable() {
        //                public void run() {
        //                    viewer.showGUI();
        //                }
        //        });
        //        
        //        Thread.sleep( 10000 );
    }
}
