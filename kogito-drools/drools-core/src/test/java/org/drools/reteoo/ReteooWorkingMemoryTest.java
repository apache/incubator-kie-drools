package org.drools.reteoo;

import junit.framework.TestCase;

import org.drools.FactHandle;
import org.drools.common.EqualityKey;
import org.drools.common.TruthMaintenanceSystem;

public class ReteooWorkingMemoryTest extends TestCase {
    /*
     * @see JBRULES-356
     */
    public void testBasicWorkingMemoryActions() {
        ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) new ReteooRuleBase().newWorkingMemory();
        TruthMaintenanceSystem tms = workingMemory.getTruthMaintenanceSystem();
        String string = "test";
        FactHandle fd = workingMemory.assertObject(string);
        
        assertEquals(1, 
                     tms.getAssertMap().size() );        
        EqualityKey key = tms.get( string );
        assertSame( fd, key.getFactHandle() );
        assertNull( key.getOtherFactHandle() );
        
        workingMemory.modifyObject(fd, string);
        
        assertEquals(1, 
                     tms.getAssertMap().size() );        
        key = tms.get( string );
        assertSame( fd, key.getFactHandle() );
        assertNull( key.getOtherFactHandle() );
        
        workingMemory.retractObject(fd);
        
        assertEquals(0, 
                     tms.getAssertMap().size() );        
        key = tms.get( string );
        assertNull( key );
        
        fd = workingMemory.assertObject(string);

        assertEquals(1, 
                     tms.getAssertMap().size() );        
        
        assertEquals(1, 
                     tms.getAssertMap().size() );        
        key = tms.get( string );
        assertSame( fd, key.getFactHandle() );
        assertNull( key.getOtherFactHandle() );
    }
}
