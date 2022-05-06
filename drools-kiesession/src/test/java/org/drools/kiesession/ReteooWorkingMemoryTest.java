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

package org.drools.kiesession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.base.MapGlobalResolver;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.TruthMaintenanceSystemFactory;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.builder.NodeFactory;
import org.drools.core.reteoo.builder.PhreakNodeFactory;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.accessor.GlobalResolver;
import org.drools.core.test.model.Cheese;
import org.drools.core.test.model.Person;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class ReteooWorkingMemoryTest {
    /*
     * @see JBRULES-356
     */
    @Test
    @Ignore
    public void testBasicWorkingMemoryActions() {
        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        final TruthMaintenanceSystem tms = TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem(ksession);
        final String string = "test";

        FactHandle fd = ksession.insert( string );

        FactHandle fz = tms.insert( string, null, new MockActivation() );

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
        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        assertEquals( 0,
                      ksession.getIdentifier() );
        ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();
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

        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        ksession.setGlobalResolver( resolver );
        assertEquals( "value1",
                      ksession.getGlobal( "global1" ) );
        assertEquals( "value2",
                      ksession.getGlobal( "global2" ) );
    }

    @Test
    public void testObjectIterator() {
        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
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
        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();
        final ReentrantAction action = new ReentrantAction();
        ksession.addPropagation( action, true );
        ksession.flushPropagations();
        assertEquals( 2, action.counter.get() );
    }
    
    @Test
    public void testDifferentEntryPointsOnSameFact() {
        //JBRULES-2971

        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        Rete rete = kBase.getRete();

        NodeFactory nFacotry = new PhreakNodeFactory();
        EntryPointNode epn = nFacotry.buildEntryPointNode( kBase.getReteooBuilder().getNodeIdsGenerator().getNextId(),
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
        assertNull( ksession.getObject( f1 ) );
    }

    private static class ReentrantAction
            extends PropagationEntry.AbstractPropagationEntry
            implements WorkingMemoryAction {
        // I am using AtomicInteger just as an int wrapper... nothing to do with concurrency here
        public AtomicInteger counter = new AtomicInteger(0);
        public void execute(ReteEvaluator reteEvaluator) {
            // the reentrant action must be executed completely
            // before any of the final actions is executed
            assertEquals( 0, counter.get() );
            reteEvaluator.addPropagation( new FinalAction( counter ) );
            assertEquals( 0, counter.get() );
            reteEvaluator.addPropagation( new FinalAction( counter ) );
            assertEquals( 0, counter.get() );
            reteEvaluator.getActivationsManager().flushPropagations();
            assertEquals( 0, counter.get() );
            reteEvaluator.getActivationsManager().flushPropagations();
            assertEquals( 0, counter.get() );
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
