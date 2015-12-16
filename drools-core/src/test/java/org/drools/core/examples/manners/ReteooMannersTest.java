/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.examples.manners;

import org.drools.core.event.AfterActivationFiredEvent;
import org.drools.core.event.DefaultAgendaEventListener;
import org.drools.core.impl.InternalKnowledgeBase;
import org.junit.Test;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class ReteooMannersTest extends BaseMannersTest {

    @Test
    public void testManners() throws Exception {

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addPackage( this.pkg );
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();

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
            kSession.insert( object );
        }

        kSession.insert( new Count( 1 ) );

        final long start = System.currentTimeMillis();
        kSession.fireAllRules();
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
