package org.drools.examples.manners;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.drools.WorkingMemory;

public class LeapsMannersTest extends BaseMannersTest {

    public void xxxtestManners() throws Exception {

        final org.drools.leaps.RuleBaseImpl ruleBase = new org.drools.leaps.RuleBaseImpl();
        ruleBase.addPackage( this.pkg );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

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
