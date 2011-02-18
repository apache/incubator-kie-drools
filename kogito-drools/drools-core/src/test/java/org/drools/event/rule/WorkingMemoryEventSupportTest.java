/*
 * Copyright 2005 JBoss Inc
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

package org.drools.event.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.drools.Cheese;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.WorkingMemory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris</a>
 */
public class WorkingMemoryEventSupportTest {

    @Test
    public void testAddRuleRuntimeEventListener() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List wmList = new ArrayList();
        final WorkingMemoryEventListener eventListener = new WorkingMemoryEventListener() {

            public void objectInserted(ObjectInsertedEvent event) {
                wmList.add( event );
            }

            public void objectUpdated(ObjectUpdatedEvent event) {
                wmList.add( event );
            }

            public void objectRetracted(ObjectRetractedEvent event) {
                wmList.add( event );
            }

        };

        ksession.addEventListener( eventListener );

        final Cheese stilton = new Cheese( "stilton",
                                     15 );
        final Cheese cheddar = new Cheese( "cheddar",
                                     17 );

        final FactHandle stiltonHandle = ksession.insert( stilton );

        final ObjectInsertedEvent oae = (ObjectInsertedEvent) wmList.get( 0 );
        assertSame( stiltonHandle,
                    oae.getFactHandle() );

        ksession.update( stiltonHandle,
                         stilton );
        final ObjectUpdatedEvent ome = (ObjectUpdatedEvent) wmList.get( 1 );
        assertSame( stiltonHandle,
                    ome.getFactHandle() );

        ksession.retract( stiltonHandle );
        final ObjectRetractedEvent ore = (ObjectRetractedEvent) wmList.get( 2 );
        assertSame( stiltonHandle,
                    ore.getFactHandle() );

        ksession.insert( cheddar );
    }
    
    @Test
    public void testRemoveRuleRuntimeEventListener() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List wmList = new ArrayList();
        final WorkingMemoryEventListener eventListener = new WorkingMemoryEventListener() {

            public void objectInserted(ObjectInsertedEvent event) {
                wmList.add( event );
            }

            public void objectUpdated(ObjectUpdatedEvent event) {
                wmList.add( event );
            }

            public void objectRetracted(ObjectRetractedEvent event) {
                wmList.add( event );
            }

        };

        ksession.addEventListener( eventListener );
        ksession.removeEventListener( eventListener );

        final Cheese stilton = new Cheese( "stilton",
                                     15 );
        final Cheese cheddar = new Cheese( "cheddar",
                                     17 );

        final FactHandle stiltonHandle = ksession.insert( stilton );
        assertTrue( wmList.isEmpty() );

        ksession.update( stiltonHandle,
                         stilton );
        assertTrue( wmList.isEmpty() );

        ksession.retract( stiltonHandle );
        assertTrue( wmList.isEmpty() );

        ksession.insert( cheddar );
        assertTrue( wmList.isEmpty() );
    }
}
