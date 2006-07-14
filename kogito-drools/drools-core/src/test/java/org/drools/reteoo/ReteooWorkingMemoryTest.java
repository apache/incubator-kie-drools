package org.drools.reteoo;

import junit.framework.TestCase;

import org.drools.FactHandle;
import org.drools.RuleBaseFactory;
import org.drools.common.EqualityKey;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.TruthMaintenanceSystem;

public class ReteooWorkingMemoryTest extends TestCase {
    /*
     * @see JBRULES-356
     */
    public void testBasicWorkingMemoryActions() {
        ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) RuleBaseFactory.newRuleBase().newWorkingMemory();
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
    
    public void testId() {
        ReteooRuleBase ruleBase =  (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newWorkingMemory();
        assertEquals( 0,
                      workingMemory.getId() );
        workingMemory = (InternalWorkingMemory) ruleBase.newWorkingMemory();
        assertEquals( 1,
                      workingMemory.getId() );        
    }
}
