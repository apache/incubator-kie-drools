package org.drools.leaps;

import junit.framework.TestCase;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.common.EqualityKey;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.TruthMaintenanceSystem;

public class LeapsWorkingMemoryTest extends TestCase {
    /*
     * @see JBRULES-356
     */
    public void testBasicWorkingMemoryActions() {
        LeapsWorkingMemory workingMemory = (LeapsWorkingMemory) RuleBaseFactory.newRuleBase( RuleBase.LEAPS ).newWorkingMemory();
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
        assertNotSame( fd, key.getFactHandle() );
        assertNull( key.getOtherFactHandle() );
        
        workingMemory.retractObject( key.getFactHandle() );
        
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
        LeapsRuleBase ruleBase = (LeapsRuleBase) RuleBaseFactory.newRuleBase( RuleBase.LEAPS );
        InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newWorkingMemory();
        assertEquals( 0,
                      workingMemory.getId() );
        workingMemory = (InternalWorkingMemory) ruleBase.newWorkingMemory();
        assertEquals( 1,
                      workingMemory.getId() );        
    }
}
