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

package org.drools.core.rule;

import org.drools.core.RuleBaseFactory;
import org.drools.core.WorkingMemory;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassFieldReader;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.FieldFactory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.test.model.Cheese;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.ReteooRuleBase;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.rule.PredicateConstraint.PredicateContextEntry;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.PredicateExpression;
import org.drools.core.spi.Tuple;
import org.junit.Before;
import org.junit.Test;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FieldConstraintTest {

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
    }

    public FieldConstraintTest() {
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
     *
     * @throws IntrospectionException
     */
    @Test
    public void testLiteralConstraint() throws IntrospectionException {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();

        final ClassFieldReader extractor = store.getReader(Cheese.class,
                "type",
                getClass().getClassLoader());

        final MvelConstraint constraint = new MvelConstraintTestUtil( "type == \"cheddar\"",
                                                                      FieldFactory.getInstance().getFieldValue( "cheddar" ),
                                                                      extractor );

        final ContextEntry context = constraint.createContextEntry();

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );

        final InternalFactHandle cheddarHandle = (InternalFactHandle) workingMemory.insert( cheddar );

        // check constraint
        assertTrue( constraint.isAllowed( cheddarHandle,
                                          workingMemory,
                                          context ) );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        final InternalFactHandle stiltonHandle = (InternalFactHandle) workingMemory.insert( stilton );

        // check constraint
        assertFalse( constraint.isAllowed( stiltonHandle,
                                           workingMemory,
                                           context ) );
    }

    /**
     * <pre>
     *
     *
     *                Cheese( price == 5 )
     *
     *
     * </pre>
     *
     * @throws IntrospectionException
     */
    @Test
    public void testPrimitiveLiteralConstraint() throws IntrospectionException {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();

        final ClassFieldReader extractor = store.getReader(Cheese.class,
                "price",
                getClass().getClassLoader());

        final MvelConstraint constraint = new MvelConstraintTestUtil( "price == 5",
                                                                      FieldFactory.getInstance().getFieldValue( 5 ),
                                                                      extractor );
        final ContextEntry context = constraint.createContextEntry();

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );

        final InternalFactHandle cheddarHandle = (InternalFactHandle) workingMemory.insert( cheddar );

        // check constraint
        assertTrue( constraint.isAllowed( cheddarHandle,
                                          workingMemory,
                                          context ) );

        final Cheese stilton = new Cheese( "stilton",
                                           10 );

        final InternalFactHandle stiltonHandle = (InternalFactHandle) workingMemory.insert( stilton );

        // check constraint
        assertFalse(constraint.isAllowed(stiltonHandle,
                workingMemory,
                context));
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
     *
     * @throws IntrospectionException
     */
    @Test
    public void testPredicateConstraint() throws IntrospectionException {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();

        final InternalReadAccessor priceExtractor = store.getReader( Cheese.class,
                                                                     "price",
                                                                     getClass().getClassLoader() );

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
                                                                  workingMemory.getObject( tuple.get( previousDeclarations[0] ) ) );
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
                                                                         new Declaration[]{price2Declaration},
                                                                         new String[]{},
                                                                         new String[]{});

        final Cheese cheddar0 = new Cheese( "cheddar",
                                            5 );
        final InternalFactHandle f0 = (InternalFactHandle) workingMemory.insert( cheddar0 );
        LeftTupleImpl tuple = new LeftTupleImpl( f0,
                                         null,
                                         true );

        final Cheese cheddar1 = new Cheese( "cheddar",
                                            10 );
        final InternalFactHandle f1 = (InternalFactHandle) workingMemory.insert( cheddar1 );

        tuple = new LeftTupleImpl( tuple,
                               new RightTuple( f1,
                                               null ),
                               null,
                               true );

        final PredicateContextEntry context = (PredicateContextEntry) constraint1.createContextEntry();
        context.updateFromTuple(workingMemory,
                tuple);
        assertTrue( constraint1.isAllowedCachedLeft( context,
                                                     f1 ) );
    }
}
