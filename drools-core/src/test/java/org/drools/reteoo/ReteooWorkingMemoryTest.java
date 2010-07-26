/**
 * Copyright 2010 JBoss Inc
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

package org.drools.reteoo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.base.MapGlobalResolver;
import org.drools.common.EqualityKey;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.TruthMaintenanceSystem;
import org.drools.FactHandle;
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
        final GlobalResolver resolver = new MapGlobalResolver(map);
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();
        workingMemory.setGlobalResolver( resolver );
        assertEquals( "value1",
                      workingMemory.getGlobal( "global1" ) );
        assertEquals( "value2",
                      workingMemory.getGlobal( "global2" ) );
    }
    
    public void testObjectIterator() {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        final StatefulSession session = ruleBase.newStatefulSession();
        
        session.insert( new Person( "bob", 35) );
        session.insert( new Cheese( "stilton", 35) );
        session.insert( new Cheese( "brie", 35) );
        session.insert( new Person( "steve", 55) );
        session.insert( new Person( "tom", 100) );
        
        int i = 0;
        for ( Iterator it = session.iterateFactHandles(); it.hasNext(); ) {
            Object object = it.next();
            if ( i++ > 5 ) {
                fail( "should not iterate for than 3 times" );
            }
        }
        
        i = 0;
        for ( Iterator it = session.iterateObjects(); it.hasNext(); ) {
            Object object = it.next();
            if ( i++ > 5 ) {
                fail( "should not iterate for than 3 times" );
            }
        }               
    }
}
