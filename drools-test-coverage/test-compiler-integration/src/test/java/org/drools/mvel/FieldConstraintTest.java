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

import org.drools.core.WorkingMemory;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassFieldReader;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.RightTupleImpl;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.PredicateConstraint;
import org.drools.core.rule.PredicateConstraint.PredicateContextEntry;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.PredicateExpression;
import org.drools.core.spi.Tuple;
import org.drools.mvel.model.Cheese;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class FieldConstraintTest {

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    private final boolean useLambdaConstraint;

    public FieldConstraintTest(boolean useLambdaConstraint) {
        this.useLambdaConstraint = useLambdaConstraint;
    }

    @Parameterized.Parameters(name = "useLambdaConstraint={0}")
    public static Collection<Object[]> getParameters() {
        Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{false});
        parameters.add(new Object[]{true});
        return parameters;
    }

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
    }

    /**
     * <pre>
     *
     *
     *                ( Cheese (type &quot;cheddar&quot;) )
     *
     *
     * </pre>
     *
     * This is currently the same as using a ReturnValueConstraint just that it
     * doesn't need any requiredDeclarations
     */
    @Test
    public void testLiteralConstraint() {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        final ClassFieldReader extractor = store.getReader( Cheese.class,
                "type");

        AlphaNodeFieldConstraint constraint = ConstraintTestUtil.createCheeseTypeEqualsConstraint(extractor, "cheddar", useLambdaConstraint);

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );

        final InternalFactHandle cheddarHandle = (InternalFactHandle) ksession.insert( cheddar );

        // check constraint
        assertTrue( constraint.isAllowed( cheddarHandle,
                                          ksession ) );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        final InternalFactHandle stiltonHandle = (InternalFactHandle) ksession.insert( stilton );

        // check constraint
        assertFalse( constraint.isAllowed( stiltonHandle,
                                           ksession ) );
    }

    /**
     * <pre>
     *
     *
     *                Cheese( price == 5 )
     *
     *
     * </pre>
     */
    @Test
    public void testPrimitiveLiteralConstraint() {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        final ClassFieldReader extractor = store.getReader(Cheese.class,
                "price");

        AlphaNodeFieldConstraint constraint = ConstraintTestUtil.createCheesePriceEqualsConstraint(extractor, 5, useLambdaConstraint);

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );

        final InternalFactHandle cheddarHandle = (InternalFactHandle) ksession.insert( cheddar );

        // check constraint
        assertTrue( constraint.isAllowed( cheddarHandle,
                                          ksession ) );

        final Cheese stilton = new Cheese( "stilton",
                                           10 );

        final InternalFactHandle stiltonHandle = (InternalFactHandle) ksession.insert( stilton );

        // check constraint
        assertFalse(constraint.isAllowed(stiltonHandle,
                                         ksession));
    }

    /**
     * <pre>
     *
     *
     *                (Cheese (price ?price1 )
     *                (Cheese (price ?price2&amp;:(= ?price2 (* 2 ?price1) )
     *
     *
     * </pre>
     */
    @Test
    public void testPredicateConstraint() {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        final InternalReadAccessor priceExtractor = store.getReader( Cheese.class,
                                                                     "price" );

        Pattern pattern = new Pattern( 0,
                                       new ClassObjectType( Cheese.class ) );

        // Bind the extractor to a decleration
        // Declarations know the pattern they derive their value form
        final Declaration price1Declaration = new Declaration( "price1",
                                                               priceExtractor,
                                                               pattern );

        pattern = new Pattern( 1,
                               new ClassObjectType( Cheese.class ) );

        // Bind the extractor to a decleration
        // Declarations know the pattern they derive their value form
        final Declaration price2Declaration = new Declaration( "price2",
                                                               priceExtractor,
                                                               pattern );

        final PredicateExpression evaluator = new PredicateExpression() {

            private static final long serialVersionUID = 510l;

            public boolean evaluate(InternalFactHandle handle,
                                    Tuple tuple,
                                    Declaration[] previousDeclarations,
                                    Declaration[] localDeclarations,
                                    WorkingMemory workingMemory,
                                    Object context) {
                int price1 = previousDeclarations[0].getIntValue( (InternalWorkingMemory) workingMemory,
                                                                  tuple.getObject( previousDeclarations[0] ) );
                int price2 = localDeclarations[0].getIntValue( (InternalWorkingMemory) workingMemory,
                                                               handle.getObject() );

                return (price2 == (price1 * 2));

            }

            public Object createContext() {
                return null;
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
            }

            public void writeExternal(ObjectOutput out) throws IOException {
            }
        };

        final PredicateConstraint constraint1 = new PredicateConstraint( evaluator,
                                                                         new Declaration[]{price1Declaration},
                                                                         new Declaration[]{price2Declaration} );

        final Cheese cheddar0 = new Cheese( "cheddar",
                                            5 );
        final InternalFactHandle f0 = (InternalFactHandle) ksession.insert( cheddar0 );
        LeftTupleImpl tuple = new LeftTupleImpl( f0,
                                         null,
                                         true );

        final Cheese cheddar1 = new Cheese( "cheddar",
                                            10 );
        final InternalFactHandle f1 = (InternalFactHandle) ksession.insert( cheddar1 );

        tuple = new LeftTupleImpl( tuple,
                               new RightTupleImpl( f1, null ),
                               null,
                               true );

        final PredicateContextEntry context = (PredicateContextEntry) constraint1.createContextEntry();
        context.updateFromTuple(ksession,
                tuple);
        assertTrue( constraint1.isAllowedCachedLeft( context,
                                                     f1 ) );
    }
}
