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

import org.drools.core.test.model.MockActivation;
import org.junit.Ignore;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.base.MapGlobalResolver;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.reteoo.builder.NodeFactory;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.GlobalResolver;
import org.drools.core.test.model.Cheese;
import org.drools.core.test.model.Person;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ReteooWorkingMemoryTest {
    /*
     * @see JBRULES-356
     */
    @Test
    @Ignore
    public void testBasicWorkingMemoryActions() {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final TruthMaintenanceSystem tms = ((NamedEntryPoint)ksession.getWorkingMemoryEntryPoint(EntryPointId.DEFAULT.getEntryPointId()) ).getTruthMaintenanceSystem();
        final String string = "test";

        FactHandle fd = ksession.insert( string );

        FactHandle fz = ksession.getTruthMaintenanceSystem().insert( string, null, null, new MockActivation() );

        assertEquals( 1,
                      tms.getEqualityKeyMap().size() );

        EqualityKey key = tms.get( string );
        assertSame( fz,
                    key.getFactHandle() );
        assertEquals( 2, key.size() );

        ksession.update( fd, string );

        assertEquals( 1,
                      tms.getEqualityKeyMap().size() );
        key = tms.get( string );
        assertSame( fz,
                    key.getFactHandle() );
        assertEquals( 2, key.size() );

        ksession.retract( fd );

        assertEquals( 1,
                      tms.getEqualityKeyMap().size() );
        key = tms.get( string );

        fd = ksession.insert( string );

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
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        assertEquals( 0,
                      ksession.getIdentifier() );
        ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();
        assertEquals( 1,
                      ksession.getIdentifier() );
    }

    @Test
    public void testGlobalResolver() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put( "global1",
                 "value1" );
        map.put( "global2",
                 "value2" );
        final GlobalResolver resolver = new MapGlobalResolver(map);

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        ksession.setGlobalResolver( resolver );
        assertEquals( "value1",
                      ksession.getGlobal( "global1" ) );
        assertEquals( "value2",
                      ksession.getGlobal( "global2" ) );
    }

    @Test
    public void testObjectIterator() {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        KieSession ksession = kBase.newKieSession();

        ksession.insert( new Person( "bob", 35) );
        ksession.insert( new Cheese( "stilton", 35) );
        ksession.insert( new Cheese( "brie", 35) );
        ksession.insert( new Person( "steve", 55) );
        ksession.insert( new Person( "tom", 100) );

        int i = 0;
        for ( FactHandle fh : ksession.getFactHandles()) {
            if ( i++ > 5 ) {
                fail( "should not iterate for than 3 times" );
            }
        }

        i = 0;
        for ( FactHandle fh : ksession.getFactHandles()) {
            if ( i++ > 5 ) {
                fail( "should not iterate for than 3 times" );
            }
        }
    }

    @Test
    public void testExecuteQueueActions() {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();
        final ReentrantAction action = new ReentrantAction();
        ksession.queueWorkingMemoryAction( action );
        ksession.executeQueuedActions();
        assertEquals( 2, action.counter.get() );
    }
    
    @Test
    public void testDifferentEntryPointsOnSameFact() {
        //JBRULES-2971

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        Rete rete = kBase.getRete();

        NodeFactory nFacotry = kBase.getConfiguration().getComponentFactory().getNodeFactoryService();
        EntryPointNode epn = nFacotry.buildEntryPointNode( kBase.getReteooBuilder().getIdGenerator().getNextId(),
                                                            RuleBasePartitionId.MAIN_PARTITION,
                                                            kBase.getConfiguration().isMultithreadEvaluation(),
                                                            rete,
                                                            new EntryPointId( "xxx" ) );


        kBase.getRete().addObjectSink( epn );
        StatefulKnowledgeSession ksession = kBase.newStatefulKnowledgeSession();
        FactHandle f1 = ksession.insert( "f1" );
        
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
