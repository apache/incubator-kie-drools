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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.Cheese;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldReader;
import org.drools.base.ClassObjectType;
import org.drools.base.FieldFactory;
import org.drools.base.ValueType;
import org.drools.base.evaluators.ComparableEvaluatorsDefinition;
import org.drools.base.evaluators.EqualityEvaluatorsDefinition;
import org.drools.base.evaluators.EvaluatorRegistry;
import org.drools.base.evaluators.MatchesEvaluatorsDefinition;
import org.drools.base.evaluators.Operator;
import org.drools.base.evaluators.SetEvaluatorsDefinition;
import org.drools.base.evaluators.SoundslikeEvaluatorsDefinition;
import org.drools.common.InternalFactHandle;
import org.drools.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.spi.Consequence;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.KnowledgeHelper;

public class AgendaEventSupportTest {

    public static EvaluatorRegistry registry = new EvaluatorRegistry();
    static {
        registry.addEvaluatorDefinition( new EqualityEvaluatorsDefinition() );
        registry.addEvaluatorDefinition( new ComparableEvaluatorsDefinition() );
        registry.addEvaluatorDefinition( new SetEvaluatorsDefinition() );
        registry.addEvaluatorDefinition( new MatchesEvaluatorsDefinition() );
        registry.addEvaluatorDefinition( new SoundslikeEvaluatorsDefinition() );
    }

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
        final ClassFieldReader extractor = pkg.getClassFieldAccessorStore().getReader( Cheese.class,
                                                                                       "type",
                                                                                       getClass().getClassLoader() );

        final FieldValue field = FieldFactory.getFieldValue( "cheddar" );

        final Evaluator evaluator = registry.getEvaluator( ValueType.STRING_TYPE,
                                                           Operator.EQUAL,
                                                           null );

        final LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                                    evaluator,
                                                                    field );
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

            public void activationCancelled(ActivationCancelledEvent event) {
                assertNotNull( event.getKnowledgeRuntime() );
                agendaList.add( event );

            }

            public void activationCreated(ActivationCreatedEvent event) {
                assertNotNull( event.getKnowledgeRuntime() );
                agendaList.add( event );
                
            }

            public void afterActivationFired(AfterActivationFiredEvent event) {
                assertNotNull( event.getKnowledgeRuntime() );
                agendaList.add( event );
            }

            public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
                assertNotNull( event.getKnowledgeRuntime() );
                agendaList.add( event );
            }

            public void agendaGroupPushed(AgendaGroupPushedEvent event) {
                assertNotNull( event.getKnowledgeRuntime() );
                agendaList.add( event );
            }

            public void beforeActivationFired(BeforeActivationFiredEvent event) {
                assertNotNull( event.getKnowledgeRuntime() );
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
        ActivationCreatedEvent createdEvent = (ActivationCreatedEvent) agendaList.get( 0 );
        assertSame( cheddarHandle,
                    createdEvent.getActivation().getFactHandles().toArray()[0] );

        // clear the agenda to check CLEAR events occur
        ksession.getAgenda().clear();
        ActivationCancelledEvent cancelledEvent = (ActivationCancelledEvent) agendaList.get( 1 );
        assertEquals( ActivationCancelledCause.CLEAR,
                      cancelledEvent.getCause() );

        agendaList.clear();

        // update results in an ActivationCreatedEvent
        cheddar.setPrice( 14 );
        ksession.update( cheddarHandle,
                         cheddar );
        assertEquals( 1,
                      agendaList.size() );
        createdEvent = (ActivationCreatedEvent) agendaList.get( 0 );
        assertSame( cheddarHandle,
                    createdEvent.getActivation().getFactHandles().toArray()[0] );
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
        cancelledEvent = (ActivationCancelledEvent) agendaList.get( 0 );
        assertNull( ((InternalFactHandle) cancelledEvent.getActivation().getFactHandles().toArray()[0]).getObject() );

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
        final BeforeActivationFiredEvent beforeEvent = (BeforeActivationFiredEvent) agendaList.get( 0 );
        assertSame( cheddarHandle,
                    beforeEvent.getActivation().getFactHandles().toArray()[0] );
        final AfterActivationFiredEvent afterEvent = (AfterActivationFiredEvent) agendaList.get( 1 );
        assertSame( cheddarHandle,
                    afterEvent.getActivation().getFactHandles().toArray()[0] );
        final AgendaGroupPoppedEvent poppedEvent = (AgendaGroupPoppedEvent) agendaList.get( 2 );
        assertEquals( "test group",
                      poppedEvent.getAgendaGroup().getName() );
    }

}
