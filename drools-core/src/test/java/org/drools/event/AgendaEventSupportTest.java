package org.drools.event;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.ClassObjectType;
import org.drools.base.FieldFactory;
import org.drools.base.ShadowProxy;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.spi.Consequence;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.KnowledgeHelper;

/**
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris</a>
 */
public class AgendaEventSupportTest extends TestCase {
    public void testIsSerializable() {
        assertTrue( Serializable.class.isAssignableFrom( AgendaEventSupport.class ) );
    }

    public void testAgendaEventListener() throws Exception {
        final RuleBase rb = RuleBaseFactory.newRuleBase();

        // create a simpe package with one rule to test the events
        final Package pkg = new Package( "org.drools.test" );
        final Rule rule = new Rule( "test1" );
        rule.setAgendaGroup( "test group" );
        final ClassObjectType cheeseObjectType = new ClassObjectType( Cheese.class );
        final Pattern pattern = new Pattern( 0,
                                             cheeseObjectType );

        final ClassFieldExtractor extractor = ClassFieldExtractorCache.getInstance().getExtractor( Cheese.class,
                                                                                                   "type",
                                                                                                   getClass().getClassLoader() );

        final FieldValue field = FieldFactory.getFieldValue( "cheddar" );

        final Evaluator evaluator = ValueType.STRING_TYPE.getEvaluator( Operator.EQUAL );

        final LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                                    evaluator,
                                                                    field );
        pattern.addConstraint( constraint );
        rule.addPattern( pattern );

        rule.setConsequence( new Consequence() {
            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) throws Exception {
            }
        } );
        pkg.addRule( rule );
        rb.addPackage( pkg );

        // create a new working memory and add an AgendaEventListener
        final WorkingMemory wm = rb.newStatefulSession();
        final List agendaList = new ArrayList();
        final AgendaEventListener agendaEventListener = new AgendaEventListener() {

            public void activationCancelled(ActivationCancelledEvent event,
                                            WorkingMemory workingMemory) {
                agendaList.add( event );

            }

            public void activationCreated(ActivationCreatedEvent event,
                                          WorkingMemory workingMemory) {
                agendaList.add( event );
            }

            public void afterActivationFired(AfterActivationFiredEvent event,
                                             WorkingMemory workingMemory) {
                agendaList.add( event );
            }

            public void agendaGroupPopped(AgendaGroupPoppedEvent event,
                                          WorkingMemory workingMemory) {
                agendaList.add( event );
            }

            public void agendaGroupPushed(AgendaGroupPushedEvent event,
                                          WorkingMemory workingMemory) {
                agendaList.add( event );
            }

            public void beforeActivationFired(BeforeActivationFiredEvent event,
                                              WorkingMemory workingMemory) {
                agendaList.add( event );
            }
        };
        wm.addEventListener( agendaEventListener );

        // assert the cheese fact
        final Cheese cheddar = new Cheese( "cheddar",
                                           15 );
        FactHandle cheddarHandle = wm.insert( cheddar );

        // should be one ActivationCreatedEvent
        assertEquals( 1,
                      agendaList.size() );
        ActivationCreatedEvent createdEvent = (ActivationCreatedEvent) agendaList.get( 0 );
        assertSame( cheddar,
                    unwrapShadow( createdEvent.getActivation().getTuple().get( 0 ).getObject() ) );
        agendaList.clear();

        // update results in a ActivationCancelledEvent and an ActivationCreatedEvent, note the object is always resolvable        
        cheddar.setPrice( 14 );
        wm.update( cheddarHandle,
                   cheddar );
        assertEquals( 2,
                      agendaList.size() );
        ActivationCancelledEvent cancelledEvent = (ActivationCancelledEvent) agendaList.get( 0 );
        assertSame( cheddar,
                    unwrapShadow( cancelledEvent.getActivation().getTuple().get( 0 ).getObject() ) );
        createdEvent = (ActivationCreatedEvent) agendaList.get( 1 );
        assertSame( cheddar,
                    unwrapShadow( createdEvent.getActivation().getTuple().get( 0 ).getObject() ) );
        agendaList.clear();

        // retract results in a ActivationCancelledEvent, noe the object is not resolveable now as it no longer exists
        wm.retract( cheddarHandle );
        assertEquals( 1,
                      agendaList.size() );
        cancelledEvent = (ActivationCancelledEvent) agendaList.get( 0 );
        assertNull( cancelledEvent.getActivation().getTuple().get( 0 ).getObject() );

        // re-assert the fact so we can test the agenda group events
        cheddarHandle = wm.insert( cheddar );
        agendaList.clear();

        // setFocus results in an AgendaGroupPushedEvent
        wm.setFocus( "test group" );
        assertEquals( 1,
                      agendaList.size() );
        final AgendaGroupPushedEvent pushedEvent = (AgendaGroupPushedEvent) agendaList.get( 0 );
        assertEquals( "test group",
                      pushedEvent.getAgendaGroup().getName() );
        agendaList.clear();

        // fireAllRules results in a BeforeActivationFiredEvent and an AfterActivationFiredEvent
        // the AgendaGroup becomes empty, which results in a popped event.
        wm.fireAllRules();
        assertEquals( 3,
                      agendaList.size() );
        final BeforeActivationFiredEvent beforeEvent = (BeforeActivationFiredEvent) agendaList.get( 0 );
        assertSame( cheddar,
                    unwrapShadow( beforeEvent.getActivation().getTuple().get( 0 ).getObject() ) );
        final AfterActivationFiredEvent afterEvent = (AfterActivationFiredEvent) agendaList.get( 1 );
        assertSame( cheddar,
                    unwrapShadow( afterEvent.getActivation().getTuple().get( 0 ).getObject() ) );
        final AgendaGroupPoppedEvent poppedEvent = (AgendaGroupPoppedEvent) agendaList.get( 2 );
        assertEquals( "test group",
                      poppedEvent.getAgendaGroup().getName() );
    }

    private Object unwrapShadow(Object object) {
        if ( object instanceof ShadowProxy ) {
            return ((ShadowProxy) object).getShadowedObject();
        } else {
            return object;
        }
    }
}