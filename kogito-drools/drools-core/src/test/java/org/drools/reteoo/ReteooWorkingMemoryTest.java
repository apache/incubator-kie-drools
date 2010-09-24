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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.FactHandle;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.base.MapGlobalResolver;
import org.drools.common.EqualityKey;
import org.drools.common.InternalKnowledgeRuntime;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.TruthMaintenanceSystem;
import org.drools.common.WorkingMemoryAction;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.spi.GlobalResolver;

public class ReteooWorkingMemoryTest extends TestCase {
    /*
     * @see JBRULES-356
     */
    public void testBasicWorkingMemoryActions() {
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) RuleBaseFactory.newRuleBase().newStatefulSession();
        final TruthMaintenanceSystem tms = workingMemory.getTruthMaintenanceSystem();
        final String string = "test";
        
        workingMemory.insert( string );
        
        FactHandle fd = workingMemory.insertLogical( string );

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
    
    public void testExecuteQueueActions() {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        final ReteooWorkingMemory wm = (ReteooWorkingMemory) ruleBase.newStatefulSession();
        final ReentrantAction action = new ReentrantAction();
        wm.queueWorkingMemoryAction( action );
        wm.executeQueuedActions();
        assertEquals( 2, action.counter.get() );
    }

    private static class ReentrantAction implements WorkingMemoryAction {
        // I am using AtomicInteger just as an int wrapper... nothing to do with concurrency here
        public AtomicInteger counter = new AtomicInteger(0);
        public void writeExternal(ObjectOutput out) throws IOException {}
        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {}
        public void write(MarshallerWriteContext context) throws IOException {}
        public void execute(InternalWorkingMemory workingMemory) {
            // the reentrant action must be executed completely
            // before any of the final actions is executed
            assertEquals( 0, counter.get() );
            workingMemory.queueWorkingMemoryAction( new FinalAction( counter ) );
            assertEquals( 0, counter.get() );
            workingMemory.queueWorkingMemoryAction( new FinalAction( counter ) );
            assertEquals( 0, counter.get() );
            workingMemory.executeQueuedActions();
            assertEquals( 0, counter.get() );
            workingMemory.executeQueuedActions();
            assertEquals( 0, counter.get() );
        }
        public void execute(InternalKnowledgeRuntime kruntime) {
        	execute(((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory());
        }
    }
    
    private static class FinalAction extends ReentrantAction {
        public AtomicInteger counter;
        public FinalAction( AtomicInteger counter ) {
            this.counter = counter;
        }
        public void execute(InternalWorkingMemory workingMemory) {
            counter.incrementAndGet();
            workingMemory.executeQueuedActions();
            workingMemory.executeQueuedActions();
        }
    }

}
