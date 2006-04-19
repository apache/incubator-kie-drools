package org.drools.examples.manners;
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



import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.drools.FactException;
import org.drools.PackageIntegrationException;
import org.drools.RuleIntegrationException;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.event.DefaultAgendaEventListener;
import org.drools.rule.DuplicateRuleNameException;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.InvalidRuleException;

public class ReteooMannersTest extends BaseMannersTest {

    public void testManners() throws DuplicateRuleNameException,
                                InvalidRuleException,
                                IntrospectionException,
                                RuleIntegrationException,
                                PackageIntegrationException,
                                InvalidPatternException,
                                FactException,
                                IOException,
                                InterruptedException {

        final org.drools.reteoo.RuleBaseImpl ruleBase = new org.drools.reteoo.RuleBaseImpl();
        ruleBase.addPackage( this.pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        
//        workingMemory.addEventListener( new DefaultAgendaEventListener() {
//           public void activationCreated(ActivationCreatedEvent event) {
//                super.activationCreated( event );
//                System.out.println( event );
//            }
//           
//           public void activationCancelled(ActivationCancelledEvent event) {
//               super.activationCancelled( event );
//               System.out.println( event );
//           }
//           
//           public void beforeActivationFired(BeforeActivationFiredEvent event) {
//               super.beforeActivationFired( event );
//               System.out.println( event );
//           }           
//           
//           public void afterActivationFired(AfterActivationFiredEvent event) {
//               super.afterActivationFired( event );
//               System.out.println( event );
//           }
//           
//        });

        InputStream is = getClass().getResourceAsStream( "/manners64.dat" );
        List list = getInputObjects( is );
        for ( Iterator it = list.iterator(); it.hasNext(); ) {
            Object object = it.next();
            workingMemory.assertObject( object );
        }

        workingMemory.assertObject( new Count( 1 ) );

        long start = System.currentTimeMillis();
        workingMemory.fireAllRules();
        System.err.println( System.currentTimeMillis() - start );

        //        final ReteooJungViewer viewer = new ReteooJungViewer(ruleBase); 
        //        
        //        javax.swing.SwingUtilities.invokeLater(new Runnable() { 
        //        		public void run() {
        //        			viewer.showGUI();
        //        		}
        //        });
        //        
        //        Thread.sleep( 10000 );
    }
}