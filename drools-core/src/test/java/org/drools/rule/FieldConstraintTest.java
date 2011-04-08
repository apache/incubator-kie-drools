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

package org.drools.rule;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.Cheese;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldAccessorStore;
import org.drools.base.ClassFieldReader;
import org.drools.base.ClassObjectType;
import org.drools.base.FieldFactory;
import org.drools.base.ValueType;
import org.drools.base.evaluators.ComparableEvaluatorsDefinition;
import org.drools.base.evaluators.EqualityEvaluatorsDefinition;
import org.drools.base.evaluators.Operator;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.RightTuple;
import org.drools.rule.PredicateConstraint.PredicateContextEntry;
import org.drools.rule.ReturnValueRestriction.ReturnValueContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.PredicateExpression;
import org.drools.spi.ReturnValueExpression;
import org.drools.spi.Tuple;

public class FieldConstraintTest {

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();
    EqualityEvaluatorsDefinition   equals      = new EqualityEvaluatorsDefinition();
    ComparableEvaluatorsDefinition comparables = new ComparableEvaluatorsDefinition();

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

        final ClassFieldReader extractor = store.getReader( Cheese.class,
                                                            "type",
                                                            getClass().getClassLoader() );

        final FieldValue field = FieldFactory.getFieldValue( "cheddar" );

        final Evaluator evaluator = equals.getEvaluator( ValueType.STRING_TYPE,
                                                         Operator.EQUAL );

        final LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                                    evaluator,
                                                                    field );
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

        final ClassFieldReader extractor = store.getReader( Cheese.class,
                                                            "price",
                                                            getClass().getClassLoader() );

        final FieldValue field = FieldFactory.getFieldValue( 5 );

        final Evaluator evaluator = equals.getEvaluator( ValueType.PINTEGER_TYPE,
                                                         Operator.EQUAL );

        final LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                                    evaluator,
                                                                    field );
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
        assertFalse( constraint.isAllowed( stiltonHandle,
                                           workingMemory,
                                           context ) );
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

            public boolean evaluate(Object object,
                                    Tuple tuple,
                                    Declaration[] previousDeclarations,
                                    Declaration[] localDeclarations,
                                    WorkingMemory workingMemory,
                                    Object context) {
                int price1 = previousDeclarations[0].getIntValue( (InternalWorkingMemory) workingMemory,
                                                                  workingMemory.getObject( tuple.get( previousDeclarations[0] ) ) );
                int price2 = localDeclarations[0].getIntValue( (InternalWorkingMemory) workingMemory,
                                                               object );

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
        LeftTuple tuple = new LeftTuple( f0,
                                         null,
                                         true );

        final Cheese cheddar1 = new Cheese( "cheddar",
                                            10 );
        final InternalFactHandle f1 = (InternalFactHandle) workingMemory.insert( cheddar1 );

        tuple = new LeftTuple( tuple,
                               new RightTuple( f1,
                                               null ),
                               null,
                               true );

        final PredicateContextEntry context = (PredicateContextEntry) constraint1.createContextEntry();
        context.updateFromTuple( workingMemory,
                                 tuple );
        assertTrue( constraint1.isAllowedCachedLeft( context,
                                                     f1 ) );
    }

    /**
     * <pre>
     *
     *
     *                (Cheese (price ?price )
     *                (Cheese (price =(* 2 ?price) )
     *                (Cheese (price &gt;(* 2 ?price) )
     *
     *
     * </pre>
     *
     * @throws IntrospectionException
     */
    @Test
    public void testReturnValueConstraint() throws IntrospectionException {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();

        final InternalReadAccessor priceExtractor = store.getReader( Cheese.class,
                                                                     "price",
                                                                     getClass().getClassLoader() );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Cheese.class ) );

        // Bind the extractor to a decleration
        // Declarations know the pattern they derive their value form
        final Declaration priceDeclaration = new Declaration( "price1",
                                                              priceExtractor,
                                                              pattern );

        final ReturnValueExpression isDoubleThePrice = new ReturnValueExpression() {
            private static final long serialVersionUID = 510l;

            public FieldValue evaluate(Object object,
                                       Tuple tuple, // ?price
                                       Declaration[] previousDeclarations,
                                       Declaration[] localDeclarations,
                                       WorkingMemory workingMemory,
                                       Object context) {
                int price = ((Number) previousDeclarations[0].getValue( (InternalWorkingMemory) workingMemory,
                                                                        workingMemory.getObject( tuple.get( previousDeclarations[0] ) ) )).intValue();
                return FieldFactory.getFieldValue( 2 * price );

            }

            public Object createContext() {
                return null;
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public void replaceDeclaration(Declaration declaration,
                                           Declaration resolved) {
            }
        };

        final ReturnValueRestriction restriction1 = new ReturnValueRestriction( priceExtractor,
                                                                                isDoubleThePrice,
                                                                                new Declaration[]{priceDeclaration},
                                                                                new Declaration[0],
                                                                                new String[0],
                                                                                equals.getEvaluator( ValueType.INTEGER_TYPE,
                                                                                                     Operator.EQUAL ) );

        final ReturnValueConstraint constraint1 = new ReturnValueConstraint( priceExtractor,
                                                                             restriction1 );

        final ReturnValueRestriction restriction2 = new ReturnValueRestriction( priceExtractor,
                                                                                isDoubleThePrice,
                                                                                new Declaration[]{priceDeclaration},
                                                                                new Declaration[0],
                                                                                new String[0],
                                                                                comparables.getEvaluator( ValueType.INTEGER_TYPE,
                                                                                                          Operator.GREATER ) );

        final ReturnValueConstraint constraint2 = new ReturnValueConstraint( priceExtractor,
                                                                             restriction2 );

        final Cheese cheddar0 = new Cheese( "cheddar",
                                            5 );
        final InternalFactHandle f0 = (InternalFactHandle) workingMemory.insert( cheddar0 );

        LeftTuple tuple = new LeftTuple( f0,
                                         null,
                                         true );

        final Cheese cheddar1 = new Cheese( "cheddar",
                                            10 );
        final InternalFactHandle f1 = (InternalFactHandle) workingMemory.insert( cheddar1 );
        tuple = new LeftTuple( tuple,
                               new RightTuple( f1,
                                               null ),
                               null,
                               true );

        final ReturnValueContextEntry context1 = (ReturnValueContextEntry) constraint1.createContextEntry();
        context1.updateFromTuple( workingMemory,
                                  tuple );
        assertTrue( constraint1.isAllowedCachedLeft( context1,
                                                     f1 ) );

        final ReturnValueContextEntry context2 = (ReturnValueContextEntry) constraint2.createContextEntry();
        context2.updateFromTuple( workingMemory,
                                  tuple );
        assertFalse( constraint2.isAllowedCachedLeft( context2,
                                                      f1 ) );

        final Cheese cheddar2 = new Cheese( "cheddar",
                                            11 );

        final InternalFactHandle f2 = (InternalFactHandle) workingMemory.insert( cheddar2 );

        assertTrue( constraint2.isAllowedCachedLeft( context2,
                                                     f2 ) );
    }

    /**
     * <pre>
     *
     *
     *                type == &quot;cheddar&quot &amp;&amp; price &gt; 10
     *
     *
     * </pre>
     *
     * Test the use of the composite AND constraint. Composite AND constraints are only
     * used when nested inside other field constraints, as the top level AND is implicit
     *
     * @throws IntrospectionException
     */
    @Test
    public void testCompositeAndConstraint() {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();

        final ClassFieldReader extractor = store.getReader( Cheese.class,
                                                            "type",
                                                            getClass().getClassLoader() );

        final FieldValue field = FieldFactory.getFieldValue( "cheddar" );

        final Evaluator evaluator = equals.getEvaluator( ValueType.STRING_TYPE,
                                                         Operator.EQUAL );

        final LiteralConstraint constraint1 = new LiteralConstraint( extractor,
                                                                     evaluator,
                                                                     field );

        final ClassFieldReader priceExtractor = store.getReader( Cheese.class,
                                                                 "price",
                                                                 getClass().getClassLoader() );

        final FieldValue priceField = FieldFactory.getFieldValue( 10 );

        final Evaluator priceEvaluator = comparables.getEvaluator( ValueType.INTEGER_TYPE,
                                                                   Operator.GREATER );

        final LiteralConstraint constraint2 = new LiteralConstraint( priceExtractor,
                                                                     priceEvaluator,
                                                                     priceField );

        final Cheese cheddar = new Cheese( "cheddar",
                                           15 );

        final AndConstraint constraint = new AndConstraint();
        constraint.addAlphaConstraint( constraint1 );
        constraint.addAlphaConstraint( constraint2 );

        final ContextEntry context = constraint.createContextEntry();

        final InternalFactHandle cheddarHandle = (InternalFactHandle) workingMemory.insert( cheddar );

        // check constraint
        assertTrue( constraint.isAllowed( cheddarHandle,
                                          workingMemory,
                                          context ) );

        cheddar.setPrice( 5 );
        assertFalse( constraint.isAllowed( cheddarHandle,
                                           workingMemory,
                                           context ) );

        cheddar.setType( "stilton" );
        assertFalse( constraint.isAllowed( cheddarHandle,
                                           workingMemory,
                                           context ) );

        cheddar.setPrice( 15 );
        assertFalse( constraint.isAllowed( cheddarHandle,
                                           workingMemory,
                                           context ) );
    }

    /**
     * <pre>
     *
     *
     *                Cheese( type == &quot;cheddar&quot || price &gt; 10 )
     *
     *
     * </pre>
     *
     * Test the use of the composite OR constraint.
     *
     * @throws IntrospectionException
     */
    @Test
    public void testCompositeOrConstraint() {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();

        final ClassFieldReader extractor = store.getReader( Cheese.class,
                                                            "type",
                                                            getClass().getClassLoader() );

        final FieldValue field = FieldFactory.getFieldValue( "cheddar" );

        final Evaluator evaluator = equals.getEvaluator( ValueType.STRING_TYPE,
                                                         Operator.EQUAL );

        final LiteralConstraint constraint1 = new LiteralConstraint( extractor,
                                                                     evaluator,
                                                                     field );

        final ClassFieldReader priceExtractor = store.getReader( Cheese.class,
                                                                 "price",
                                                                 getClass().getClassLoader() );

        final FieldValue priceField = FieldFactory.getFieldValue( 10 );

        final Evaluator priceEvaluator = comparables.getEvaluator( ValueType.INTEGER_TYPE,
                                                                   Operator.GREATER );

        final LiteralConstraint constraint2 = new LiteralConstraint( priceExtractor,
                                                                     priceEvaluator,
                                                                     priceField );

        final Cheese cheddar = new Cheese( "cheddar",
                                           15 );

        final OrConstraint constraint = new OrConstraint();
        constraint.addAlphaConstraint( constraint1 );
        constraint.addAlphaConstraint( constraint2 );
        final ContextEntry context = constraint.createContextEntry();

        final InternalFactHandle cheddarHandle = (InternalFactHandle) workingMemory.insert( cheddar );

        // check constraint
        assertTrue( constraint.isAllowed( cheddarHandle,
                                          workingMemory,
                                          context ) );

        cheddar.setPrice( 5 );
        assertTrue( constraint.isAllowed( cheddarHandle,
                                          workingMemory,
                                          context ) );

        cheddar.setType( "stilton" );
        assertFalse( constraint.isAllowed( cheddarHandle,
                                           workingMemory,
                                           context ) );

        cheddar.setPrice( 15 );
        assertTrue( constraint.isAllowed( cheddarHandle,
                                          workingMemory,
                                          context ) );
    }

    /**
     * <pre>
     *
     *
     *                Cheese( ( type == &quot;cheddar&quot &amp;&amp; price &gt; 10) || ( type == &quote;stilton&quote; && price &lt; 10 ) )
     *
     *
     * </pre>
     *
     * Test the use of the composite OR constraint.
     *
     * @throws IntrospectionException
     */
    @Test
    public void testNestedCompositeConstraints() {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();

        final ClassFieldReader typeExtractor = store.getReader( Cheese.class,
                                                                "type",
                                                                getClass().getClassLoader() );

        final FieldValue cheddarField = FieldFactory.getFieldValue( "cheddar" );

        final Evaluator stringEqual = equals.getEvaluator( ValueType.STRING_TYPE,
                                                           Operator.EQUAL );

        // type == 'cheddar'
        final LiteralConstraint constraint1 = new LiteralConstraint( typeExtractor,
                                                                     stringEqual,
                                                                     cheddarField );

        final ClassFieldReader priceExtractor = store.getReader( Cheese.class,
                                                                 "price",
                                                                 getClass().getClassLoader() );

        final FieldValue field10 = FieldFactory.getFieldValue( 10 );

        final Evaluator integerGreater = comparables.getEvaluator( ValueType.INTEGER_TYPE,
                                                                   Operator.GREATER );

        // price > 10
        final LiteralConstraint constraint2 = new LiteralConstraint( priceExtractor,
                                                                     integerGreater,
                                                                     field10 );

        // type == 'cheddar' && price > 10
        final AndConstraint and1 = new AndConstraint();
        and1.addAlphaConstraint( constraint1 );
        and1.addAlphaConstraint( constraint2 );

        final FieldValue stiltonField = FieldFactory.getFieldValue( "stilton" );
        // type == 'stilton'
        final LiteralConstraint constraint3 = new LiteralConstraint( typeExtractor,
                                                                     stringEqual,
                                                                     stiltonField );

        final Evaluator integerLess = comparables.getEvaluator( ValueType.INTEGER_TYPE,
                                                                Operator.LESS );

        // price < 10
        final LiteralConstraint constraint4 = new LiteralConstraint( priceExtractor,
                                                                     integerLess,
                                                                     field10 );

        // type == 'stilton' && price < 10
        final AndConstraint and2 = new AndConstraint();
        and2.addAlphaConstraint( constraint3 );
        and2.addAlphaConstraint( constraint4 );

        // ( type == 'cheddar' && price > 10 ) || ( type == 'stilton' && price < 10 )
        final OrConstraint constraint = new OrConstraint();
        constraint.addAlphaConstraint( and1 );
        constraint.addAlphaConstraint( and2 );
        final ContextEntry context = constraint.createContextEntry();

        final Cheese cheddar = new Cheese( "cheddar",
                                           15 );

        final InternalFactHandle cheddarHandle = (InternalFactHandle) workingMemory.insert( cheddar );

        // check constraint
        assertTrue( constraint.isAllowed( cheddarHandle,
                                          workingMemory,
                                          context ) );

        cheddar.setPrice( 5 );
        assertFalse( constraint.isAllowed( cheddarHandle,
                                           workingMemory,
                                           context ) );

        cheddar.setType( "stilton" );
        assertTrue( constraint.isAllowed( cheddarHandle,
                                          workingMemory,
                                          context ) );

        cheddar.setPrice( 15 );
        assertFalse( constraint.isAllowed( cheddarHandle,
                                           workingMemory,
                                           context ) );
    }

}
