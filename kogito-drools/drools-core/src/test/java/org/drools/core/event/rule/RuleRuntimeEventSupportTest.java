/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.event.rule;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.test.model.Cheese;

import org.junit.Test;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.*;

public class RuleRuntimeEventSupportTest {

    @Test
    public void testAddRuleRuntimeEventListener() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List wmList = new ArrayList();
        final RuleRuntimeEventListener eventListener = new RuleRuntimeEventListener() {

            public void objectInserted(ObjectInsertedEvent event) {
                wmList.add( event );
            }

            public void objectUpdated(ObjectUpdatedEvent event) {
                wmList.add( event );
            }

            public void objectDeleted(ObjectDeletedEvent event) {
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
        final ObjectDeletedEvent ore = (ObjectDeletedEvent) wmList.get( 2 );
        assertSame( stiltonHandle,
                    ore.getFactHandle() );

        ksession.insert( cheddar );
    }
    
    @Test
    public void testRemoveRuleRuntimeEventListener() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final List wmList = new ArrayList();
        final RuleRuntimeEventListener eventListener = new RuleRuntimeEventListener() {

            public void objectInserted(ObjectInsertedEvent event) {
                wmList.add( event );
            }

            public void objectUpdated(ObjectUpdatedEvent event) {
                wmList.add( event );
            }

            public void objectDeleted(ObjectDeletedEvent event) {
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
