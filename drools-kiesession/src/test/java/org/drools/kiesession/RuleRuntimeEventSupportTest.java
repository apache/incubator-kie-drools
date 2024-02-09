/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.kiesession;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.test.model.Cheese;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleRuntimeEventSupportTest {
    @Test
    public void testIsSerializable() {
        assertThat(Serializable.class.isAssignableFrom(RuleRuntimeEventSupport.class)).isTrue();
    }

    @Test
    public void testRuleRuntimeEventListener() {
        final KieBase rb = KnowledgeBaseFactory.newKnowledgeBase();
        final KieSession wm = rb.newKieSession();

        final List wmList = new ArrayList();
        final RuleRuntimeEventListener workingMemoryListener = new RuleRuntimeEventListener() {
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

        wm.addEventListener( workingMemoryListener );
        assertThat(wm.getRuleRuntimeEventListeners().size()).isEqualTo(1);

        final Cheese stilton = new Cheese( "stilton",
                                           15 );
        final Cheese cheddar = new Cheese( "cheddar",
                                           17 );

        final FactHandle stiltonHandle = wm.insert( stilton );

        ObjectInsertedEvent oae = (ObjectInsertedEvent) wmList.get( 0 );
        assertThat(oae.getFactHandle()).isSameAs(stiltonHandle);

        wm.update( stiltonHandle,
                   cheddar );
        final ObjectUpdatedEvent ome = (ObjectUpdatedEvent) wmList.get( 1 );
        assertThat(ome.getFactHandle()).isSameAs(stiltonHandle);
        assertThat(ome.getObject()).isEqualTo(cheddar);
        assertThat(ome.getOldObject()).isEqualTo(stilton);

        wm.retract( stiltonHandle );
        final ObjectDeletedEvent ore = (ObjectDeletedEvent) wmList.get( 2 );
        assertThat(ore.getFactHandle()).isSameAs(stiltonHandle);

        final FactHandle cheddarHandle = wm.insert( cheddar );
        oae = (ObjectInsertedEvent) wmList.get( 3 );
        assertThat(oae.getFactHandle()).isSameAs(cheddarHandle);
    }

    @Test
    public void testAddRuleRuntimeEventListener() {
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KieSession ksession = kbase.newKieSession();

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
        assertThat(oae.getFactHandle()).isSameAs(stiltonHandle);

        ksession.update( stiltonHandle,
                stilton );
        final ObjectUpdatedEvent ome = (ObjectUpdatedEvent) wmList.get( 1 );
        assertThat(ome.getFactHandle()).isSameAs(stiltonHandle);

        ksession.retract( stiltonHandle );
        final ObjectDeletedEvent ore = (ObjectDeletedEvent) wmList.get( 2 );
        assertThat(ore.getFactHandle()).isSameAs(stiltonHandle);

        ksession.insert( cheddar );
    }

    @Test
    public void testRemoveRuleRuntimeEventListener() {
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KieSession ksession = kbase.newKieSession();

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
        assertThat(wmList.isEmpty()).isTrue();

        ksession.update( stiltonHandle,
                stilton );
        assertThat(wmList.isEmpty()).isTrue();

        ksession.retract( stiltonHandle );
        assertThat(wmList.isEmpty()).isTrue();

        ksession.insert( cheddar );
        assertThat(wmList.isEmpty()).isTrue();
    }
}
