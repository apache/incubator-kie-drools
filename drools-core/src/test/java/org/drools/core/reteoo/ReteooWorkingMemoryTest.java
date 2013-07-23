/*
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

package org.drools.core.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.FactHandle;
import org.drools.core.RuleBaseFactory;
import org.drools.core.StatefulSession;
import org.drools.core.common.AbstractWorkingMemory;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.rule.EntryPoint;

import static org.junit.Assert.*;

import org.drools.core.RuleBase;
import org.drools.core.base.MapGlobalResolver;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.test.model.Cheese;
import org.drools.core.test.model.Person;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.GlobalResolver;

public class ReteooWorkingMemoryTest {
    /*
     * @see JBRULES-356
     */
    @Test
    public void testBasicWorkingMemoryActions() {
        final AbstractWorkingMemory workingMemory = (AbstractWorkingMemory) RuleBaseFactory.newRuleBase().newStatefulSession();
        final TruthMaintenanceSystem tms = ((NamedEntryPoint)workingMemory.getWorkingMemoryEntryPoint( EntryPointId.DEFAULT.getEntryPointId() ) ).getTruthMaintenanceSystem();
        final String string = "test";

        workingMemory.insert( string );

        FactHandle fd = workingMemory.insertLogical( string );

        assertEquals( 1,
                      tms.getEqualityKeyMap().size() );

        EqualityKey key = tms.get( string );
        assertSame( fd,
                    key.getFactHandle() );
        assertEquals( 1, key.size() );

        workingMemory.update( fd,
                                    string );

        assertEquals( 1,
                      tms.getEqualityKeyMap().size() );
        key = tms.get( string );
        assertSame( fd,
                    key.getFactHandle() );
        assertEquals( 1, key.size() );

        workingMemory.retract( fd );

        assertEquals( 0,
                      tms.getEqualityKeyMap().size() );
        key = tms.get( string );
        assertNull( key );

        fd = workingMemory.insert( string );

        assertEquals( 1,
                      tms.getEqualityKeyMap().size() );

        assertEquals( 1,
                      tms.getEqualityKeyMap().size() );
        key = tms.get( string );
        assertSame( fd,
                    key.getFactHandle() );
        assertEquals( 1, key.size() );
    }

    @Test
    public void testId() {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();
        assertEquals( 0,
                      workingMemory.getId() );
        workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();
        assertEquals( 1,
                      workingMemory.getId() );
    }

    @Test
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

    @Test
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

    @Test
    public void testExecuteQueueActions() {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        final AbstractWorkingMemory wm = (AbstractWorkingMemory) ruleBase.newStatefulSession();
        final ReentrantAction action = new ReentrantAction();
        wm.queueWorkingMemoryAction( action );
        wm.executeQueuedActions();
        assertEquals( 2, action.counter.get() );
    }
    
    @Test
    public void testDifferentEntryPointsOnSameFact() {
        //JBRULES-2971
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ReteooRuleBase rbase = ( ReteooRuleBase ) ((KnowledgeBaseImpl)kbase).getRuleBase();
        Rete rete = rbase.getRete();
        EntryPointNode epn = new EntryPointNode( rbase.getReteooBuilder().getIdGenerator().getNextId(),
                                                 RuleBasePartitionId.MAIN_PARTITION,
                                                 rbase.getConfiguration().isMultithreadEvaluation(),
                                                 rete,
                                                 new EntryPointId( "xxx" ) );
        
        
        rbase.getRete().addObjectSink( epn );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        org.kie.api.runtime.rule.FactHandle f1 = ksession.insert( "f1" );
        
        EntryPoint ep = ksession.getEntryPoint( "xxx" );
        try {
            ep.update( f1, "s1" );
            fail( "Should throw an exception" );
        } catch ( IllegalArgumentException e ) {
            
        }
       
        try {
            ep.retract( f1 );
            fail( "Should throw an exception" );
        } catch ( IllegalArgumentException e ) {
            
        }   
        
        ksession.update( f1, "s1" );
        assertNotNull( ksession.getObject( f1 ) );
        ksession.retract( f1 );
        
        ksession.retract( f1 );
        assertNull( ksession.getObject( f1 ) );
    }

    private static class ReentrantAction implements WorkingMemoryAction {
        // I am using AtomicInteger just as an int wrapper... nothing to do with concurrency here
        public AtomicInteger counter = new AtomicInteger(0);
        public void writeExternal(ObjectOutput out) throws IOException {}
        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {}
        public void write(MarshallerWriteContext context) { throw new IllegalStateException("this method should never be called"); }
        public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext context) { throw new IllegalStateException("this method should never be called"); }
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
