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

import org.drools.Cheese;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldReader;
import org.drools.base.ClassObjectType;
import org.drools.base.FieldFactory;
import org.drools.base.evaluators.EvaluatorRegistry;
import org.drools.common.InternalFactHandle;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.rule.MvelConstraintTestUtil;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.rule.constraint.MvelConstraint;
import org.drools.spi.Consequence;
import org.drools.spi.FieldValue;
import org.drools.spi.KnowledgeHelper;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.definition.KnowledgePackage;
import org.kie.event.rule.MatchCancelledCause;
import org.kie.event.rule.MatchCancelledEvent;
import org.kie.event.rule.MatchCreatedEvent;
import org.kie.event.rule.AfterMatchFiredEvent;
import org.kie.event.rule.AgendaEventListener;
import org.kie.event.rule.AgendaGroupPoppedEvent;
import org.kie.event.rule.AgendaGroupPushedEvent;
import org.kie.event.rule.BeforeMatchFiredEvent;
import org.kie.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.rule.FactHandle;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class AgendaEventSupportTest {

    public static EvaluatorRegistry registry = new EvaluatorRegistry();

    //    public void testIsSerializable() {
    //        assertTrue( Serializable.class.isAssignableFrom( AgendaEventSupport.class ) );
    //    }

    @Test
    public void testAgendaEventListener() throws Exception {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        // create a simple package with one rule to test the events
        final Package pkg = new Package( "org.drools.test" );
        final Rule rule = new Rule( "test1" );
        rule.setAgendaGroup( "test group" );
        final ClassObjectType cheeseObjectType = new ClassObjectType( Cheese.class );
        final Pattern pattern = new Pattern( 0,
                                             cheeseObjectType );

        pkg.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        pkg.getClassFieldAccessorStore().setEagerWire( true );
        final ClassFieldReader extractor = pkg.getClassFieldAccessorStore().getReader(Cheese.class,
                "type",
                getClass().getClassLoader());

        final FieldValue field = FieldFactory.getInstance().getFieldValue( "cheddar" );

        final MvelConstraint constraint = new MvelConstraintTestUtil("type == \"cheddar\"", field, extractor);

        pattern.addConstraint( constraint );
        rule.addPattern( pattern );

        rule.setConsequence( new Consequence() {
            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final org.drools.WorkingMemory workingMemory) throws Exception {
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                // TODO Auto-generated method stub
                return null;
            }
        } );
        pkg.addRule( rule );

        List<KnowledgePackage> pkgs = new ArrayList<KnowledgePackage>();
        pkgs.add( new KnowledgePackageImp( pkg ) );
        kbase.addKnowledgePackages( pkgs );

        // create a new working memory and add an AgendaEventListener
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        final List agendaList = new ArrayList();
        final AgendaEventListener agendaEventListener = new AgendaEventListener() {

            public void activationCancelled(MatchCancelledEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );

            }

            public void activationCreated(MatchCreatedEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );
                
            }

            public void afterActivationFired(AfterMatchFiredEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );
            }

            public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );
            }

            public void agendaGroupPushed(AgendaGroupPushedEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );
            }

            public void beforeActivationFired(BeforeMatchFiredEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );
            }

            public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );
            }

            public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );
            }

            public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event );
            }

            public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
                assertNotNull( event.getKieRuntime() );
                agendaList.add( event ); 
            }
        };
        ksession.addEventListener( agendaEventListener );
        
        assertEquals( 1, ksession.getAgendaEventListeners().size() );

        // assert the cheese fact
        final Cheese cheddar = new Cheese( "cheddar",
                                           15 );
        FactHandle cheddarHandle = ksession.insert( cheddar );

        // should be one ActivationCreatedEvent
        assertEquals( 1,
                      agendaList.size() );
        MatchCreatedEvent createdEvent = (MatchCreatedEvent) agendaList.get( 0 );
        assertSame( cheddarHandle,
                    createdEvent.getMatch().getFactHandles().toArray()[0] );

        // clear the agenda to check CLEAR events occur
        ksession.getAgenda().clear();
        MatchCancelledEvent cancelledEvent = (MatchCancelledEvent) agendaList.get( 1 );
        assertEquals( MatchCancelledCause.CLEAR,
                      cancelledEvent.getCause() );

        agendaList.clear();

        // update results in an ActivationCreatedEvent
        cheddar.setPrice( 14 );
        ksession.update( cheddarHandle,
                         cheddar );
        assertEquals( 1,
                      agendaList.size() );
        createdEvent = (MatchCreatedEvent) agendaList.get( 0 );
        assertSame( cheddarHandle,
                    createdEvent.getMatch().getFactHandles().toArray()[0] );
        agendaList.clear();

        // update should not result in cancelation+activation events
        cheddar.setPrice( 14 );
        ksession.update( cheddarHandle,
                         cheddar );
        assertEquals( 0,
                      agendaList.size() );
        //cancelledEvent = (ActivationCancelledEvent) agendaList.get( 0 );
        //assertEquals( ActivationCancelledCause.WME_MODIFY, cancelledEvent.getCause() );
//        assertSame( cheddarHandle,
//                    cancelledEvent.getActivation().getFactHandles().toArray()[0] );
//        createdEvent = (ActivationCreatedEvent) agendaList.get( 1 );
//        assertSame( cheddarHandle,
//                    createdEvent.getActivation().getFactHandles().toArray()[0] );
//        agendaList.clear();

        // retract results in a ActivationCancelledEvent, note the object is not resolveable now as it no longer exists
        ksession.retract( cheddarHandle );
        assertEquals( 1,
                      agendaList.size() );
        cancelledEvent = (MatchCancelledEvent) agendaList.get( 0 );
        assertNull( ((InternalFactHandle) cancelledEvent.getMatch().getFactHandles().toArray()[0]).getObject() );

        // re-assert the fact so we can test the agenda group events
        cheddarHandle = ksession.insert( cheddar );
        agendaList.clear();

        // setFocus results in an AgendaGroupPushedEvent
        ksession.getAgenda().getAgendaGroup( "test group" ).setFocus();
        assertEquals( 1,
                      agendaList.size() );
        final AgendaGroupPushedEvent pushedEvent = (AgendaGroupPushedEvent) agendaList.get( 0 );
        assertEquals( "test group",
                      pushedEvent.getAgendaGroup().getName() );
        agendaList.clear();

        // fireAllRules results in a BeforeActivationFiredEvent and an AfterActivationFiredEvent
        // the AgendaGroup becomes empty, which results in a popped event.
        ksession.fireAllRules();
        assertEquals( 3,
                      agendaList.size() );
        final BeforeMatchFiredEvent beforeEvent = (BeforeMatchFiredEvent) agendaList.get( 0 );
        assertSame( cheddarHandle,
                    beforeEvent.getMatch().getFactHandles().toArray()[0] );
        final AfterMatchFiredEvent afterEvent = (AfterMatchFiredEvent) agendaList.get( 1 );
        assertSame( cheddarHandle,
                    afterEvent.getMatch().getFactHandles().toArray()[0] );
        final AgendaGroupPoppedEvent poppedEvent = (AgendaGroupPoppedEvent) agendaList.get( 2 );
        assertEquals( "test group",
                      poppedEvent.getAgendaGroup().getName() );
    }

}
