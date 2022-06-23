/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.base.MapGlobalResolver;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.builder.NodeFactory;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.GlobalResolver;
import org.drools.core.test.model.Cheese;
import org.drools.core.test.model.MockActivation;
import org.drools.core.test.model.Person;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ReteooWorkingMemoryTest {
    /*
     * @see JBRULES-356
     */
    @Test
    @Ignore
    public void testBasicWorkingMemoryActions() {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        final TruthMaintenanceSystem tms = ((NamedEntryPoint)ksession.getWorkingMemoryEntryPoint(EntryPointId.DEFAULT.getEntryPointId()) ).getTruthMaintenanceSystem();
        final String string = "test";

        FactHandle fd = ksession.insert( string );

        FactHandle fz = ksession.getTruthMaintenanceSystem().insert( string, null, null, new MockActivation() );

        assertThat(tms.getEqualityKeyMap().size()).isEqualTo(1);

        EqualityKey key = tms.get( string );
        assertThat(key.getFactHandle()).isSameAs(fz);
        assertThat(key.size()).isEqualTo(2);

        ksession.update( fd, string );

        assertThat(tms.getEqualityKeyMap().size()).isEqualTo(1);
        key = tms.get( string );
        assertThat(key.getFactHandle()).isSameAs(fz);
        assertThat(key.size()).isEqualTo(2);

        ksession.retract( fd );

        assertThat(tms.getEqualityKeyMap().size()).isEqualTo(1);
        key = tms.get( string );

        fd = ksession.insert( string );

        assertThat(tms.getEqualityKeyMap().size()).isEqualTo(1);

        assertThat(tms.getEqualityKeyMap().size()).isEqualTo(1);
        key = tms.get( string );
        assertThat(key.getFactHandle()).isSameAs(fd);
        assertThat(key.size()).isEqualTo(1);
    }

    @Test
    public void testId() {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        assertThat(ksession.getIdentifier()).isEqualTo(0);
        ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();
        assertThat(ksession.getIdentifier()).isEqualTo(1);
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
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        ksession.setGlobalResolver( resolver );
        assertThat(ksession.getGlobal("global1")).isEqualTo("value1");
        assertThat(ksession.getGlobal("global2")).isEqualTo("value2");
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

    @Test @Ignore
    public void testExecuteQueueActions() {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();
        final ReentrantAction action = new ReentrantAction();
        ksession.queueWorkingMemoryAction( action );
        ksession.flushPropagations();
        assertThat(action.counter.get()).isEqualTo(2);
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
        KieSession ksession = kBase.newKieSession();
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
        assertThat(ksession.getObject(f1)).isNotNull();
        ksession.retract( f1 );
        
        ksession.retract( f1 );
        assertThat(ksession.getObject(f1)).isNull();
    }

    private static class ReentrantAction
            extends PropagationEntry.AbstractPropagationEntry
            implements WorkingMemoryAction {
        // I am using AtomicInteger just as an int wrapper... nothing to do with concurrency here
        public AtomicInteger counter = new AtomicInteger(0);
        public void execute(InternalWorkingMemory workingMemory) {
            // the reentrant action must be executed completely
            // before any of the final actions is executed
            assertThat(counter.get()).isEqualTo(0);
            workingMemory.queueWorkingMemoryAction( new FinalAction( counter ) );
            assertThat(counter.get()).isEqualTo(0);
            workingMemory.queueWorkingMemoryAction( new FinalAction( counter ) );
            assertThat(counter.get()).isEqualTo(0);
            workingMemory.flushPropagations();
            assertThat(counter.get()).isEqualTo(0);
            workingMemory.flushPropagations();
            assertThat(counter.get()).isEqualTo(0);
        }
    }
    
    private static class FinalAction extends ReentrantAction {
        public AtomicInteger counter;
        public FinalAction( AtomicInteger counter ) {
            this.counter = counter;
        }
        public void execute(InternalWorkingMemory workingMemory) {
            counter.incrementAndGet();
            workingMemory.flushPropagations();
            workingMemory.flushPropagations();
        }
    }

}
