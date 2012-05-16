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

package org.drools.reteoo;

import org.drools.Cheese;
import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldAccessorStore;
import org.drools.base.ClassFieldReader;
import org.drools.base.FieldFactory;
import org.drools.common.DefaultFactHandle;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.AlphaNode.AlphaMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.MvelConstraintTestUtil;
import org.drools.rule.Rule;
import org.drools.rule.constraint.MvelConstraint;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.PropagationContext;
import org.junit.Before;
import org.junit.Test;

import java.beans.IntrospectionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class AlphaNodeTest extends DroolsTestCase {
    
    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
    }

    @Test
    public void testLiteralConstraintAssertObjectWithoutMemory() throws Exception {
        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        BuildContext buildContext = new BuildContext( ruleBase,
                                                      ((ReteooRuleBase) ruleBase).getReteooBuilder().getIdGenerator() );
        ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final Rule rule = new Rule( "test-rule" );
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null,
                                                                       null );

        final MockObjectSource source = new MockObjectSource( buildContext.getNextId() );

        final ClassFieldReader extractor = store.getReader(Cheese.class,
                "type",
                getClass().getClassLoader());

        final FieldValue field = FieldFactory.getFieldValue("cheddar");

        final MvelConstraint constraint = new MvelConstraintTestUtil("type == \"cheddar\"", field, extractor);

        // With Memory
        final AlphaNode alphaNode = new AlphaNode( buildContext.getNextId(),
                                                   constraint,
                                                   source,
                                                   buildContext ); // no memory

        final MockObjectSink sink = new MockObjectSink();
        alphaNode.addObjectSink( sink );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );
        final DefaultFactHandle f0 = (DefaultFactHandle) workingMemory.insert( cheddar );

        // check sink is empty
        assertLength( 0,
                      sink.getAsserted() );

        // check alpha memory is empty 
        final AlphaMemory memory = (AlphaMemory) workingMemory.getNodeMemory( alphaNode );

        // object should assert as it passes text
        alphaNode.assertObject( f0,
                                context,
                                workingMemory );

        assertEquals( 1,
                      sink.getAsserted().size() );

        Object[] list = (Object[]) sink.getAsserted().get( 0 );
        assertSame( cheddar,
                    workingMemory.getObject( (DefaultFactHandle) list[0] ) );

        final Cheese stilton = new Cheese( "stilton",
                                           6 );
        final DefaultFactHandle f1 = new DefaultFactHandle( 1,
                                                            stilton );

        // object should NOT assert as it does not pass test
        alphaNode.assertObject( f1,
                                context,
                                workingMemory );

        assertLength( 1,
                      sink.getAsserted() );

        list = (Object[]) sink.getAsserted().get( 0 );
        assertSame( cheddar,
                    workingMemory.getObject( (DefaultFactHandle) list[0] ) );
    }

    /*
     *  This just test AlphaNode With a different Constraint type.
     */
    @Test
    public void testReturnValueConstraintAssertObject() throws Exception {
        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        BuildContext buildContext = new BuildContext( ruleBase,
                                                      ((ReteooRuleBase) ruleBase).getReteooBuilder().getIdGenerator() );
        ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final Rule rule = new Rule( "test-rule" );
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null,
                                                                       null );

        final MockObjectSource source = new MockObjectSource( buildContext.getNextId() );

        final InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                                "type",
                                                                getClass().getClassLoader() );

        final FieldValue field = FieldFactory.getFieldValue( "cheddar" );

        final MvelConstraint constraint = new MvelConstraintTestUtil("type == \"cheddar\"", field, extractor);

        final AlphaNode alphaNode = new AlphaNode( buildContext.getNextId(),
                                                   constraint,
                                                   source,
                                                   buildContext );
        final MockObjectSink sink = new MockObjectSink();
        alphaNode.addObjectSink( sink );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );

        final DefaultFactHandle f0 = (DefaultFactHandle) workingMemory.insert( cheddar );

        assertLength( 0,
                      sink.getAsserted() );

        // object should assert as it passes text
        alphaNode.assertObject( f0,
                                context,
                                workingMemory );

        assertLength( 1,
                      sink.getAsserted() );
        final Object[] list = (Object[]) sink.getAsserted().get( 0 );
        assertSame( cheddar,
                    workingMemory.getObject( (DefaultFactHandle) list[0] ) );

        final Cheese stilton = new Cheese( "stilton",
                                           6 );
        f0.setObject( stilton );

        sink.getAsserted().clear();

        // object should not assert as it does not pass text
        alphaNode.assertObject( f0,
                                context,
                                workingMemory );

        assertLength( 0,
                      sink.getAsserted() );
    }

    @Test
    public void testUpdateSinkWithoutMemory() throws FactException,
                                             IntrospectionException {
        // An AlphaNode should try and repropagate from its source
        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        BuildContext buildContext = new BuildContext( ruleBase,
                                                      ((ReteooRuleBase) ruleBase).getReteooBuilder().getIdGenerator() );
        ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final Rule rule = new Rule( "test-rule" );
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null,
                                                                       null );

        final MockObjectSource source = new MockObjectSource( buildContext.getNextId() );

        final InternalReadAccessor extractor = store.getReader( Cheese.class,
                                                                "type",
                                                                getClass().getClassLoader() );

        final FieldValue field = FieldFactory.getFieldValue( "cheddar" );

        final MvelConstraint constraint = new MvelConstraintTestUtil("type == \"cheddar\"", field, extractor);

        final AlphaNode alphaNode = new AlphaNode( buildContext.getNextId(),
                                                   constraint,
                                                   source,
                                                   buildContext ); // no memory

        alphaNode.attach();

        final MockObjectSink sink1 = new MockObjectSink();
        alphaNode.addObjectSink( sink1 );

        // Assert a single fact which should be in the AlphaNode memory and also
        // propagated to the
        // the tuple sink
        final Cheese cheese = new Cheese( "cheddar",
                                          0 );
        final DefaultFactHandle handle1 = new DefaultFactHandle( 1,
                                                                 cheese );
        // adding handle to the mock source
        source.addFact( handle1 );

        alphaNode.assertObject( handle1,
                                context,
                                workingMemory );

        // Create a fact that should not be propagated, since the alpha node restriction will filter it out
        final Cheese stilton = new Cheese( "stilton",
                                           10 );
        final DefaultFactHandle handle2 = new DefaultFactHandle( 2,
                                                                 stilton );
        // adding handle to the mock source
        source.addFact( handle2 );

        alphaNode.assertObject( handle2,
                                context,
                                workingMemory );

        assertLength( 1,
                      sink1.getAsserted() );

        // Attach a new tuple sink
        final MockObjectSink sink2 = new MockObjectSink();

        // Tell the alphanode to update the new node. Make sure the first sink1
        // is not updated
        // likewise the source should not do anything
        alphaNode.updateSink( sink2,
                              context,
                              workingMemory );

        assertLength( 1,
                      sink1.getAsserted() );
        assertLength( 1,
                      sink2.getAsserted() );
        assertEquals( 1,
                      source.getUdated() );
    }

}
