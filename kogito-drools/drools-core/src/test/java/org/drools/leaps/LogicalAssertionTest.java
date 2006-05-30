package org.drools.leaps;

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

import java.util.ArrayList;

import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;

/**
 * @author Alexander Bagerman
 */
public class LogicalAssertionTest extends DroolsTestCase {

    Consequence        consequence;

    LeapsRuleBase      ruleBase;

    LeapsWorkingMemory workingMemory;

    public void setUp() throws Exception {
        super.setUp();
        this.ruleBase = new LeapsRuleBase();
        this.workingMemory = (LeapsWorkingMemory) this.ruleBase.newWorkingMemory();
        this.consequence = new Consequence() {
            /**
             * 
             */
            private static final long serialVersionUID = -3536436180370109113L;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                // do nothing
            }
        };
    }

    public void testEqualsMap() throws Exception {
        // create a RuleBase with a single ObjectTypeNode we attach a
        // MockObjectSink so we can detect assertions and retractions

        final Rule rule1 = new Rule( "test-rule1" );

        rule1.setConsequence( this.consequence );

        final LeapsRule leapsRule1 = new LeapsRule( rule1,
                                                    new ArrayList(),
                                                    new ArrayList(),
                                                    new ArrayList(),
                                                    new ArrayList() );

        final FactHandleImpl[] factHandles = new FactHandleImpl[1];
        PropagationContext context1;
        LeapsTuple tuple1;

        final String logicalString1 = new String( "logical" );
        final FactHandle handle1 = ((LeapsFactHandleFactory) this.ruleBase.getFactHandleFactory()).newFactHandle( logicalString1 );
        context1 = new PropagationContextImpl( 1,
                                               PropagationContext.ASSERTION,
                                               rule1,
                                               null );
        factHandles[0] = (FactHandleImpl) handle1;
        tuple1 = new LeapsTuple( factHandles,
                                 leapsRule1,
                                 context1 );
        this.workingMemory.assertTuple( tuple1 );
        FactHandle logicalHandle1 = this.workingMemory.assertObject( logicalString1,
                                                                     false,
                                                                     true,
                                                                     null,
                                                                     this.workingMemory.getAgenda().getActivations()[0] );

        final String logicalString2 = new String( "logical" );
        FactHandle logicalHandle2 = this.workingMemory.assertObject( logicalString2,
                                                                     false,
                                                                     true,
                                                                     rule1,
                                                                     this.workingMemory.getAgenda().getActivations()[0] );
        factHandles[0] = (FactHandleImpl) logicalHandle2;
        tuple1 = new LeapsTuple( factHandles,
                                 leapsRule1,
                                 context1 );
        this.workingMemory.assertTuple( tuple1 );

        assertSame( logicalHandle1,
                    logicalHandle2 );

        // little sanity check using normal assert
        logicalHandle1 = this.workingMemory.assertObject( logicalString1 );
        logicalHandle2 = this.workingMemory.assertObject( logicalString2 );
        assertNotSame( logicalHandle1,
                       logicalHandle2 );

    }

    /**
     * This tests that Stated asserts always take precedent
     * 
     * @throws Exception
     */
    public void testStatedOverride() throws Exception {
        final Rule rule1 = new Rule( "test-rule1" );

        rule1.setConsequence( this.consequence );

        final LeapsRule leapsRule1 = new LeapsRule( rule1,
                                                    new ArrayList(),
                                                    new ArrayList(),
                                                    new ArrayList(),
                                                    new ArrayList() );

        final FactHandleImpl[] factHandles = new FactHandleImpl[1];
        PropagationContext context1;
        LeapsTuple tuple1;

        String logicalString1 = new String( "logical" );
        final FactHandle handle1 = ((LeapsFactHandleFactory) this.ruleBase.getFactHandleFactory()).newFactHandle( logicalString1 );
        context1 = new PropagationContextImpl( 1,
                                               PropagationContext.ASSERTION,
                                               rule1,
                                               null );
        factHandles[0] = (FactHandleImpl) handle1;
        tuple1 = new LeapsTuple( factHandles,
                                 leapsRule1,
                                 context1 );
        this.workingMemory.assertTuple( tuple1 );
        FactHandle logicalHandle1 = this.workingMemory.assertObject( logicalString1,
                                                                     false,
                                                                     true,
                                                                     null,
                                                                     this.workingMemory.getAgenda().getActivations()[0] );

        String logicalString2 = new String( "logical" );
        FactHandle logicalHandle2 = this.workingMemory.assertObject( logicalString2 );

        // Should keep the same handle when overriding
        assertSame( logicalHandle1,
                    logicalHandle2 );

        // so while new STATED assertion is equal
        assertEquals( logicalString1,
                      this.workingMemory.getObject( logicalHandle2 ) );
        // they are not - not identity same - leaps can not store two objects if
        // handles are the same
        assertSame( logicalString1,
                    this.workingMemory.getObject( logicalHandle2 ) );

        // Test that a logical assertion cannot override a STATED assertion
        factHandles[0] = (FactHandleImpl) logicalHandle2;
        tuple1 = new LeapsTuple( factHandles,
                                 leapsRule1,
                                 context1 );
        this.workingMemory.assertTuple( tuple1 );

        logicalString2 = new String( "logical" );
        logicalHandle2 = this.workingMemory.assertObject( logicalString2 );

        // This logical assertion will be ignored as there is already
        // an equals STATED assertion.
        logicalString1 = new String( "logical" );
        logicalHandle1 = this.workingMemory.assertObject( logicalString1,
                                                          false,
                                                          true,
                                                          null,
                                                          this.workingMemory.getAgenda().getActivations()[0] );
        // Already an equals object but not identity same, so will do nothing
        // and return null
        assertNull( logicalHandle1 );

        //        // Alreyad identify same so return previously assigned handle
        //        logicalHandle1 = this.workingMemory.assertObject( logicalString2,
        //                                                          false,
        //                                                          true,
        //                                                          null,
        //                                                          this.workingMemory.getAgenda().getActivations()[0] );
        //        // return the matched handle
        //        assertSame( logicalHandle2,
        //                    logicalHandle1 );
        //
        //        this.workingMemory.retractObject( handle1 );
        //
        //        // Should keep the same handle when overriding
        //        assertSame( logicalHandle1,
        //                    logicalHandle2 );
        //
        //        // so while new STATED assertion is equal
        //        assertEquals( logicalString1,
        //                      this.workingMemory.getObject( logicalHandle2 ) );
        //
        //        // they are not identity same
        //        assertNotSame( logicalString1,
        //                       this.workingMemory.getObject( logicalHandle2 ) );
        //
    }

    public void testRetract() throws Exception {
        final Rule rule1 = new Rule( "test-rule1" );
        // create the first agendaItem which will justify the fact "logical"
        rule1.setConsequence( this.consequence );

        final LeapsRule leapsRule1 = new LeapsRule( rule1,
                                                    new ArrayList(),
                                                    new ArrayList(),
                                                    new ArrayList(),
                                                    new ArrayList() );

        final FactHandleImpl tuple1FactHandle = (FactHandleImpl) this.workingMemory.assertObject( "tuple1 object" );
        final FactHandleImpl tuple2FactHandle = (FactHandleImpl) this.workingMemory.assertObject( "tuple2 object" );
        final FactHandleImpl[] factHandlesTuple1 = new FactHandleImpl[1];
        final FactHandleImpl[] factHandlesTuple2 = new FactHandleImpl[1];
        factHandlesTuple1[0] = tuple1FactHandle;
        factHandlesTuple2[0] = tuple2FactHandle;

        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       rule1,
                                                                       null );
        LeapsTuple tuple1 = new LeapsTuple( factHandlesTuple1,
                                            leapsRule1,
                                            context );
        final LeapsTuple tuple2 = new LeapsTuple( factHandlesTuple2,
                                                  leapsRule1,
                                                  context );
        this.workingMemory.assertTuple( tuple1 );
        final Activation activation1 = this.workingMemory.getAgenda().getActivations()[0];

        // Assert the logical "logical" fact
        final String logicalString1 = new String( "logical" );
        final FactHandle logicalHandle1 = this.workingMemory.assertObject( logicalString1,
                                                                           false,
                                                                           true,
                                                                           rule1,
                                                                           activation1 );
        assertEquals( 3,
                      this.workingMemory.getObjects().size() );

        // create the second agendaItem to justify the "logical" fact
        final Rule rule2 = new Rule( "test-rule2" );
        rule2.setConsequence( this.consequence );
        final PropagationContext context2 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule2,
                                                                        null );
        tuple1 = new LeapsTuple( factHandlesTuple2,
                                 leapsRule1,
                                 context2 );
        this.workingMemory.assertTuple( tuple1 );
        final Activation activation2 = this.workingMemory.getAgenda().getActivations()[1];
        //
        final String logicalString2 = new String( "logical" );
        final FactHandle logicalHandle2 = this.workingMemory.assertObject( logicalString2,
                                                                           false,
                                                                           true,
                                                                           rule1,
                                                                           activation2 );
        // "logical" should only appear once
        assertEquals( 3,
                      this.workingMemory.getObjects().size() );

        // retract the logical object
        this.workingMemory.retractObject( logicalHandle2 );

        // The logical object should never appear
        assertEquals( 2,
                      this.workingMemory.getObjects().size() );

    }

    public void testMultipleLogicalRelationships() throws FactException {
        final Rule rule1 = new Rule( "test-rule1" );
        // create the first agendaItem which will justify the fact "logical"
        rule1.setConsequence( this.consequence );

        final LeapsRule leapsRule1 = new LeapsRule( rule1,
                                                    new ArrayList(),
                                                    new ArrayList(),
                                                    new ArrayList(),
                                                    new ArrayList() );

        final FactHandleImpl tuple1Fact = (FactHandleImpl) this.workingMemory.assertObject( "tuple1 object" );
        final FactHandleImpl tuple2Fact = (FactHandleImpl) this.workingMemory.assertObject( "tuple2 object" );
        final FactHandleImpl[] tuple1Handles = new FactHandleImpl[1];
        final FactHandleImpl[] tuple2Handles = new FactHandleImpl[1];
        tuple1Handles[0] = tuple1Fact;
        tuple2Handles[0] = tuple2Fact;

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule1,
                                                                        null );
        final LeapsTuple tuple1 = new LeapsTuple( tuple1Handles,
                                                  leapsRule1,
                                                  context1 );
        this.workingMemory.assertTuple( tuple1 );
        final Activation activation1 = this.workingMemory.getAgenda().getActivations()[0];

        // Assert the logical "logical" fact
        final String logicalString1 = new String( "logical" );
        final FactHandle logicalHandle1 = this.workingMemory.assertObject( logicalString1,
                                                                           false,
                                                                           true,
                                                                           rule1,
                                                                           activation1 );

        // create the second agendaItem to justify the "logical" fact
        final Rule rule2 = new Rule( "test-rule2" );
        rule2.setConsequence( this.consequence );
        final PropagationContext context2 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule2,
                                                                        null );
        final LeapsTuple tuple2 = new LeapsTuple( tuple2Handles,
                                                  leapsRule1,
                                                  context2 );
        this.workingMemory.assertTuple( tuple2 );
        // "logical" should only appear once
        final Activation activation2 = this.workingMemory.getAgenda().getActivations()[1];
        //
        final String logicalString2 = new String( "logical" );
        final FactHandle logicalHandle2 = this.workingMemory.assertObject( logicalString2,
                                                                           false,
                                                                           true,
                                                                           rule2,
                                                                           activation2 );

        assertEquals( 3,
                      this.workingMemory.getObjects().size() );
        //
        this.workingMemory.retractObject( tuple1Fact );
        // check "logical" is still in the system
        assertEquals( 2,
                      this.workingMemory.getObjects().size() );

        // now remove that final justification
        this.workingMemory.retractObject( tuple2Fact );
        // "logical" fact should no longer be in the system
        assertEquals( 0,
                      this.workingMemory.getObjects().size() );
    }
}