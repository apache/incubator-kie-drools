package org.drools.examples.manners;

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
        
        workingMemory.addEventListener( new DefaultAgendaEventListener() {
           public void activationCreated(ActivationCreatedEvent event) {
                super.activationCreated( event );
                System.out.println( event );
            }
           
           public void activationCancelled(ActivationCancelledEvent event) {
               super.activationCancelled( event );
               System.out.println( event );
           }
           
           public void beforeActivationFired(BeforeActivationFiredEvent event) {
               super.beforeActivationFired( event );
               System.out.println( event );
           }           
           
           public void afterActivationFired(AfterActivationFiredEvent event) {
               super.afterActivationFired( event );
               System.out.println( event );
           }
           
        });

        InputStream is = getClass().getResourceAsStream( "/manners5.dat" );
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
