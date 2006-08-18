package org.drools.reteoo;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.FactHandle;
import org.drools.RuleBaseFactory;
import org.drools.common.EqualityKey;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.TruthMaintenanceSystem;
import org.drools.rule.Package;
import org.drools.spi.GlobalResolver;

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
    
    public void testGlobalResolver() {
        final Map map = new HashMap();
        map.put( "global1", "value1" );
        map.put( "global2", "value2" );
        GlobalResolver resolver = new GlobalResolver() {
            
            public Object resolve(String name) {
                return map.get( name );
            }
            
        };
        ReteooRuleBase ruleBase =  (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newWorkingMemory();
        workingMemory.setGlobalResolver( resolver );
        assertEquals( "value1", workingMemory.getGlobal( "global1" ) );
        assertEquals( "value2", workingMemory.getGlobal( "global2" ) );
    }
}
