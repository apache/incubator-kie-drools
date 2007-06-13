package org.drools.reteoo;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.FactHandle;
import org.drools.RuleBaseFactory;
import org.drools.common.EqualityKey;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.TruthMaintenanceSystem;
import org.drools.spi.GlobalResolver;

public class ReteooWorkingMemoryTest extends TestCase {
    /*
     * @see JBRULES-356
     */
    public void testBasicWorkingMemoryActions() {
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) RuleBaseFactory.newRuleBase().newStatefulSession();
        final TruthMaintenanceSystem tms = workingMemory.getTruthMaintenanceSystem();
        final String string = "test";
        FactHandle fd = workingMemory.insert( string );

        assertEquals( 1,
                      tms.getAssertMap().size() );
        EqualityKey key = tms.get( string );
        assertSame( fd,
                    key.getFactHandle() );
        assertNull( key.getOtherFactHandle() );

        workingMemory.update( fd,
                                    string );

        assertEquals( 1,
                      tms.getAssertMap().size() );
        key = tms.get( string );
        assertSame( fd,
                    key.getFactHandle() );
        assertNull( key.getOtherFactHandle() );

        workingMemory.retract( fd );

        assertEquals( 0,
                      tms.getAssertMap().size() );
        key = tms.get( string );
        assertNull( key );

        fd = workingMemory.insert( string );

        assertEquals( 1,
                      tms.getAssertMap().size() );

        assertEquals( 1,
                      tms.getAssertMap().size() );
        key = tms.get( string );
        assertSame( fd,
                    key.getFactHandle() );
        assertNull( key.getOtherFactHandle() );
    }

    public void testId() {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();
        assertEquals( 0,
                      workingMemory.getId() );
        workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();
        assertEquals( 1,
                      workingMemory.getId() );
    }

    public void testGlobalResolver() {
        final Map map = new HashMap();
        map.put( "global1",
                 "value1" );
        map.put( "global2",
                 "value2" );
        final GlobalResolver resolver = new GlobalResolver() {

            public Object resolve(String name) {
                return map.get( name );
            }

        };
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();
        workingMemory.setGlobalResolver( resolver );
        assertEquals( "value1",
                      workingMemory.getGlobal( "global1" ) );
        assertEquals( "value2",
                      workingMemory.getGlobal( "global2" ) );
    }
}
