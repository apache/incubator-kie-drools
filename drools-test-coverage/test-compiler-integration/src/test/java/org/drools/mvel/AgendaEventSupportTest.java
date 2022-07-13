/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.core.WorkingMemory;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.evaluators.EvaluatorRegistry;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.test.model.Cheese;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledCause;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class AgendaEventSupportTest {

    public static EvaluatorRegistry registry = new EvaluatorRegistry();

    private final boolean useLambdaConstraint;

    public AgendaEventSupportTest(boolean useLambdaConstraint) {
        this.useLambdaConstraint = useLambdaConstraint;
    }

    @Parameterized.Parameters(name = "useLambdaConstraint={0}")
    public static Collection<Object[]> getParameters() {
        Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{false});
        parameters.add(new Object[]{true});
        return parameters;
    }

    //    public void testIsSerializable() {
    //        assertTrue( Serializable.class.isAssignableFrom( AgendaEventSupport.class ) );
    //    }

    @Ignore("This test already failed before changing to LambdaConstraint")
    @Test
    public void testAgendaEventListener() throws Exception {
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        // create a simple package with one rule to test the events
        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "org.drools.test" );
        final RuleImpl rule = new RuleImpl( "test1" );
        rule.setEager(true);
        rule.setAgendaGroup( "test group" );
        final ClassObjectType cheeseObjectType = new ClassObjectType( Cheese.class );
        final Pattern pattern = new Pattern( 0,
                                             cheeseObjectType );

        pkg.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        pkg.getClassFieldAccessorStore().setEagerWire( true );

        AlphaNodeFieldConstraint constraint = ConstraintTestUtil.createCheeseTypeEqualsConstraint(pkg.getClassFieldAccessorStore(), "cheddar", useLambdaConstraint);

        pattern.addConstraint( constraint );
        rule.addPattern( pattern );

        rule.setConsequence( new Consequence() {
            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) throws Exception {
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

        kbase.addPackages( Collections.singleton(pkg) );

        // create a new working memory and add an AgendaEventListener
        KieSession ksession = kbase.newKieSession();
        final List agendaList = new ArrayList();
        final AgendaEventListener agendaEventListener = new AgendaEventListener() {

            public void matchCancelled(MatchCancelledEvent event) {
                assertThat(event.getKieRuntime()).isNotNull();
                agendaList.add( event );
            }

            public void matchCreated(MatchCreatedEvent event) {
                assertThat(event.getKieRuntime()).isNotNull();
                agendaList.add( event );
            }

            public void afterMatchFired(AfterMatchFiredEvent event) {
                assertThat(event.getKieRuntime()).isNotNull();
                agendaList.add( event );
            }

            public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
                assertThat(event.getKieRuntime()).isNotNull();
                agendaList.add( event );
            }

            public void agendaGroupPushed(AgendaGroupPushedEvent event) {
                assertThat(event.getKieRuntime()).isNotNull();
                agendaList.add( event );
            }

            public void beforeMatchFired(BeforeMatchFiredEvent event) {
                assertThat(event.getKieRuntime()).isNotNull();
                agendaList.add( event );
            }

            public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
                assertThat(event.getKieRuntime()).isNotNull();
                agendaList.add( event );
            }

            public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
                assertThat(event.getKieRuntime()).isNotNull();
                agendaList.add( event );
            }

            public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
                assertThat(event.getKieRuntime()).isNotNull();
                agendaList.add( event );
            }

            public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
                assertThat(event.getKieRuntime()).isNotNull();
                agendaList.add( event ); 
            }
        };
        ksession.addEventListener( agendaEventListener );

        assertThat(ksession.getAgendaEventListeners().size()).isEqualTo(1);

        // assert the cheese fact
        final Cheese cheddar = new Cheese( "cheddar",
                                           15 );
        FactHandle cheddarHandle = ksession.insert( cheddar );

        InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();
        agenda.evaluateEagerList();

        // should be one MatchCreatedEvent
        assertThat(agendaList.size()).isEqualTo(1);
        MatchCreatedEvent createdEvent = (MatchCreatedEvent) agendaList.get( 0 );
        assertThat(createdEvent.getMatch().getFactHandles().toArray()[0]).isSameAs(cheddarHandle);

        // clear the agenda to check CLEAR events occur
        ksession.getAgenda().clear();
        MatchCancelledEvent cancelledEvent = (MatchCancelledEvent) agendaList.get( 1 );
        assertThat(cancelledEvent.getCause()).isEqualTo(MatchCancelledCause.CLEAR);

        agendaList.clear();

        // update results in an MatchCreatedEvent
        cheddar.setPrice( 14 );
        ksession.update(cheddarHandle,
                        cheddar);

        agenda.evaluateEagerList();

        assertThat(agendaList.size()).isEqualTo(1);
        createdEvent = (MatchCreatedEvent) agendaList.get( 0 );
        assertThat(createdEvent.getMatch().getFactHandles().toArray()[0]).isSameAs(cheddarHandle);
        agendaList.clear();

        // update should not result in cancelation+activation events
        cheddar.setPrice( 14 );
        ksession.update( cheddarHandle,
                         cheddar );
        assertThat(agendaList.size()).isEqualTo(0);
        //cancelledEvent = (ActivationCancelledEvent) agendaList.get( 0 );
        //assertEquals( ActivationCancelledCause.WME_MODIFY, cancelledEvent.getCause() );
//        assertSame( cheddarHandle,
//                    cancelledEvent.getActivation().toFactHandles().toArray()[0] );
//        createdEvent = (ActivationCreatedEvent) agendaList.get( 1 );
//        assertSame( cheddarHandle,
//                    createdEvent.getActivation().toFactHandles().toArray()[0] );
//        agendaList.clear();

        // retract results in a ActivationCancelledEvent, note the object is not resolveable now as it no longer exists
        ksession.retract( cheddarHandle );
        assertThat(agendaList.size()).isEqualTo(1);
        cancelledEvent = (MatchCancelledEvent) agendaList.get( 0 );
        // invalidated handles no longer set the object to null
        assertThat(((InternalFactHandle) cancelledEvent.getMatch().getFactHandles().toArray()[0]).getObject()).isNotNull();

        // re-assert the fact so we can test the agenda group events
        cheddarHandle = ksession.insert( cheddar );
        agendaList.clear();

        // setFocus results in an AgendaGroupPushedEvent
        ksession.getAgenda().getAgendaGroup( "test group" ).setFocus();
        assertThat(agendaList.size()).isEqualTo(1);
        final AgendaGroupPushedEvent pushedEvent = (AgendaGroupPushedEvent) agendaList.get( 0 );
        assertThat(pushedEvent.getAgendaGroup().getName()).isEqualTo("test group");
        agendaList.clear();

        // fireAllRules results in a BeforeActivationFiredEvent and an AfterActivationFiredEvent
        // the AgendaGroup becomes empty, which results in a popped event.
        ksession.fireAllRules();
        assertThat(agendaList.size()).isEqualTo(3);
        final BeforeMatchFiredEvent beforeEvent = (BeforeMatchFiredEvent) agendaList.get( 0 );
        assertThat(beforeEvent.getMatch().getFactHandles().toArray()[0]).isSameAs(cheddarHandle);
        final AfterMatchFiredEvent afterEvent = (AfterMatchFiredEvent) agendaList.get( 1 );
        assertThat(afterEvent.getMatch().getFactHandles().toArray()[0]).isSameAs(cheddarHandle);
        final AgendaGroupPoppedEvent poppedEvent = (AgendaGroupPoppedEvent) agendaList.get( 2 );
        assertThat(poppedEvent.getAgendaGroup().getName()).isEqualTo("test group");
    }

}
