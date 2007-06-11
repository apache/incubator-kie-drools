package org.drools.rule;

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

import java.beans.IntrospectionException;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.FactHandle;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.FieldFactory;
import org.drools.base.ShadowProxy;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.InstrumentedReteTuple;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.rule.PredicateConstraint.PredicateContextEntry;
import org.drools.rule.ReturnValueRestriction.ReturnValueContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.PredicateExpression;
import org.drools.spi.ReturnValueExpression;
import org.drools.spi.Tuple;

public class FieldConstraintTest extends TestCase {
    public FieldConstraintTest() {
        super();
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
    public void testLiteralConstraint() throws IntrospectionException {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();

        final ClassFieldExtractor extractor = new ClassFieldExtractor( Cheese.class,
                                                                       "type" );

        final FieldValue field = FieldFactory.getFieldValue( "cheddar" );

        final Evaluator evaluator = ValueType.STRING_TYPE.getEvaluator( Operator.EQUAL );

        final LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                                    evaluator,
                                                                    field );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );

        final InternalFactHandle cheddarHandle = (InternalFactHandle) workingMemory.assertObject( cheddar );

        // check constraint
        assertTrue( constraint.isAllowed( cheddarHandle.getObject(),
                                          workingMemory ) );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );

        final InternalFactHandle stiltonHandle = (InternalFactHandle) workingMemory.assertObject( stilton );

        // check constraint
        assertFalse( constraint.isAllowed( stiltonHandle.getObject(),
                                           workingMemory ) );
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
    public void testPrimitiveLiteralConstraint() throws IntrospectionException {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();

        final ClassFieldExtractor extractor = new ClassFieldExtractor( Cheese.class,
                                                                       "price" );

        final FieldValue field = FieldFactory.getFieldValue( 5 );

        final Evaluator evaluator = ValueType.PINTEGER_TYPE.getEvaluator( Operator.EQUAL );

        final LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                                    evaluator,
                                                                    field );

        final Cheese cheddar = new Cheese( "cheddar",
                                           5 );

        final InternalFactHandle cheddarHandle = (InternalFactHandle) workingMemory.assertObject( cheddar );

        // check constraint
        assertTrue( constraint.isAllowed( cheddarHandle.getObject(),
                                          workingMemory ) );

        final Cheese stilton = new Cheese( "stilton",
                                           10 );

        final InternalFactHandle stiltonHandle = (InternalFactHandle) workingMemory.assertObject( stilton );

        // check constraint
        assertFalse( constraint.isAllowed( stiltonHandle.getObject(),
                                           workingMemory ) );
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
    public void testPredicateConstraint() throws IntrospectionException {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();

        final FieldExtractor priceExtractor = new ClassFieldExtractor( Cheese.class,
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

            /**
             * 
             */
            private static final long serialVersionUID = -7805842671538257493L;

            public boolean evaluate(Object object,
                                    Tuple tuple,
                                    Declaration[] previousDeclarations,
                                    Declaration[] localDeclarations,
                                    WorkingMemory workingMemory) {
                int price1 = previousDeclarations[0].getIntValue( workingMemory.getObject( tuple.get( previousDeclarations[0] ) ) );
                int price2 = localDeclarations[0].getIntValue( object );

                return (price2 == (price1 * 2));

            }
        };

        final PredicateConstraint constraint1 = new PredicateConstraint( evaluator,
                                                                         new Declaration[]{price1Declaration},
                                                                         new Declaration[]{price2Declaration},
                                                                         new String[]{} );

        final Cheese cheddar0 = new Cheese( "cheddar",
                                            5 );
        final FactHandle f0 = workingMemory.assertObject( cheddar0 );
        InstrumentedReteTuple tuple = new InstrumentedReteTuple( f0 );

        final Cheese cheddar1 = new Cheese( "cheddar",
                                            10 );
        final InternalFactHandle f1 = (InternalFactHandle) workingMemory.assertObject( cheddar1 );

        tuple = new InstrumentedReteTuple( tuple,
                                           f1 );

        final PredicateContextEntry context = (PredicateContextEntry) constraint1.getContextEntry();
        context.updateFromTuple( workingMemory,
                                 tuple );
        assertTrue( constraint1.isAllowedCachedLeft( context,
                                                     f1.getObject() ) );
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
    public void testReturnValueConstraint() throws IntrospectionException {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();

        final FieldExtractor priceExtractor = new ClassFieldExtractor( Cheese.class,
                                                                       "price" );

        final Pattern pattern = new Pattern( 0,
                                             new ClassObjectType( Cheese.class ) );

        // Bind the extractor to a decleration
        // Declarations know the pattern they derive their value form
        final Declaration priceDeclaration = new Declaration( "price1",
                                                              priceExtractor,
                                                              pattern );

        final ReturnValueExpression isDoubleThePrice = new ReturnValueExpression() {
            /**
             * 
             */
            private static final long serialVersionUID = 5673999834006100045L;

            public FieldValue evaluate(Object object,
                                       Tuple tuple, // ?price
                                       Declaration[] previousDeclarations,
                                       Declaration[] localDeclarations,
                                       WorkingMemory workingMemory) {
                int price = ((Number) previousDeclarations[0].getValue( workingMemory.getObject( tuple.get( previousDeclarations[0] ) ) )).intValue();
                return FieldFactory.getFieldValue( 2 * price );

            }
        };

        final ReturnValueRestriction restriction1 = new ReturnValueRestriction( priceExtractor,
                                                                                isDoubleThePrice,
                                                                                new Declaration[]{priceDeclaration},
                                                                                new Declaration[0],
                                                                                new String[0],
                                                                                ValueType.INTEGER_TYPE.getEvaluator( Operator.EQUAL ) );

        final ReturnValueConstraint constraint1 = new ReturnValueConstraint( priceExtractor,
                                                                             restriction1 );

        final ReturnValueRestriction restriction2 = new ReturnValueRestriction( priceExtractor,
                                                                                isDoubleThePrice,
                                                                                new Declaration[]{priceDeclaration},
                                                                                new Declaration[0],
                                                                                new String[0],
                                                                                ValueType.INTEGER_TYPE.getEvaluator( Operator.GREATER ) );

        final ReturnValueConstraint constraint2 = new ReturnValueConstraint( priceExtractor,
                                                                             restriction2 );

        final Cheese cheddar0 = new Cheese( "cheddar",
                                            5 );
        final FactHandle f0 = workingMemory.assertObject( cheddar0 );

        InstrumentedReteTuple tuple = new InstrumentedReteTuple( f0 );

        final Cheese cheddar1 = new Cheese( "cheddar",
                                            10 );
        final InternalFactHandle f1 = (InternalFactHandle) workingMemory.assertObject( cheddar1 );
        tuple = new InstrumentedReteTuple( tuple,
                                           f1 );

        final ReturnValueContextEntry context1 = (ReturnValueContextEntry) constraint1.getContextEntry();
        context1.updateFromTuple( workingMemory,
                                  tuple );
        assertTrue( constraint1.isAllowedCachedLeft( context1,
                                                     f1.getObject() ) );

        final ReturnValueContextEntry context2 = (ReturnValueContextEntry) constraint2.getContextEntry();
        context2.updateFromTuple( workingMemory,
                                  tuple );
        assertFalse( constraint2.isAllowedCachedLeft( context2,
                                                      f1.getObject() ) );

        final Cheese cheddar2 = new Cheese( "cheddar",
                                            11 );

        final InternalFactHandle f2 = (InternalFactHandle) workingMemory.assertObject( cheddar2 );

        assertTrue( constraint2.isAllowedCachedLeft( context2,
                                                     f2.getObject() ) );
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
    public void testCompositeAndConstraint() {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();

        final ClassFieldExtractor extractor = new ClassFieldExtractor( Cheese.class,
                                                                       "type" );

        final FieldValue field = FieldFactory.getFieldValue( "cheddar" );

        final Evaluator evaluator = ValueType.STRING_TYPE.getEvaluator( Operator.EQUAL );

        final LiteralConstraint constraint1 = new LiteralConstraint( extractor,
                                                                     evaluator,
                                                                     field );

        final ClassFieldExtractor priceExtractor = new ClassFieldExtractor( Cheese.class,
                                                                            "price" );

        final FieldValue priceField = FieldFactory.getFieldValue( 10 );

        final Evaluator priceEvaluator = ValueType.INTEGER_TYPE.getEvaluator( Operator.GREATER );

        final LiteralConstraint constraint2 = new LiteralConstraint( priceExtractor,
                                                                     priceEvaluator,
                                                                     priceField );

        final Cheese cheddar = new Cheese( "cheddar",
                                           15 );

        final AndConstraint constraint = new AndConstraint();
        constraint.addAlphaConstraint( constraint1 );
        constraint.addAlphaConstraint( constraint2 );

        final InternalFactHandle cheddarHandle = (InternalFactHandle) workingMemory.assertObject( cheddar );

        // check constraint
        assertTrue( constraint.isAllowed( cheddarHandle.getObject(),
                                          workingMemory ) );

        cheddar.setPrice( 5 );
        ((ShadowProxy) cheddarHandle.getObject()).updateProxy();
        assertFalse( constraint.isAllowed( cheddarHandle.getObject(),
                                           workingMemory ) );
        
        cheddar.setType( "stilton" );
        ((ShadowProxy) cheddarHandle.getObject()).updateProxy();
        assertFalse( constraint.isAllowed( cheddarHandle.getObject(),
                                           workingMemory ) );
        
        cheddar.setPrice( 15 );
        ((ShadowProxy) cheddarHandle.getObject()).updateProxy();
        assertFalse( constraint.isAllowed( cheddarHandle.getObject(),
                                           workingMemory ) );
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
    public void testCompositeOrConstraint() {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();

        final ClassFieldExtractor extractor = new ClassFieldExtractor( Cheese.class,
                                                                       "type" );

        final FieldValue field = FieldFactory.getFieldValue( "cheddar" );

        final Evaluator evaluator = ValueType.STRING_TYPE.getEvaluator( Operator.EQUAL );

        final LiteralConstraint constraint1 = new LiteralConstraint( extractor,
                                                                     evaluator,
                                                                     field );

        final ClassFieldExtractor priceExtractor = new ClassFieldExtractor( Cheese.class,
                                                                            "price" );

        final FieldValue priceField = FieldFactory.getFieldValue( 10 );

        final Evaluator priceEvaluator = ValueType.INTEGER_TYPE.getEvaluator( Operator.GREATER );

        final LiteralConstraint constraint2 = new LiteralConstraint( priceExtractor,
                                                                     priceEvaluator,
                                                                     priceField );

        final Cheese cheddar = new Cheese( "cheddar",
                                           15 );

        final OrConstraint constraint = new OrConstraint();
        constraint.addAlphaConstraint( constraint1 );
        constraint.addAlphaConstraint( constraint2 );

        final InternalFactHandle cheddarHandle = (InternalFactHandle) workingMemory.assertObject( cheddar );

        // check constraint
        assertTrue( constraint.isAllowed( cheddarHandle.getObject(),
                                          workingMemory ) );

        cheddar.setPrice( 5 );
        ((ShadowProxy) cheddarHandle.getObject()).updateProxy();
        assertTrue( constraint.isAllowed( cheddarHandle.getObject(),
                                           workingMemory ) );
        
        cheddar.setType( "stilton" );
        ((ShadowProxy) cheddarHandle.getObject()).updateProxy();
        assertFalse( constraint.isAllowed( cheddarHandle.getObject(),
                                           workingMemory ) );
        
        cheddar.setPrice( 15 );
        ((ShadowProxy) cheddarHandle.getObject()).updateProxy();
        assertTrue( constraint.isAllowed( cheddarHandle.getObject(),
                                           workingMemory ) );
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
    public void testNestedCompositeConstraints() {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();

        final ClassFieldExtractor typeExtractor = new ClassFieldExtractor( Cheese.class,
                                                                       "type" );

        final FieldValue cheddarField = FieldFactory.getFieldValue( "cheddar" );

        final Evaluator stringEqual = ValueType.STRING_TYPE.getEvaluator( Operator.EQUAL );

        // type == 'cheddar'
        final LiteralConstraint constraint1 = new LiteralConstraint( typeExtractor,
                                                                     stringEqual,
                                                                     cheddarField );

        final ClassFieldExtractor priceExtractor = new ClassFieldExtractor( Cheese.class,
                                                                            "price" );

        final FieldValue field10 = FieldFactory.getFieldValue( 10 );

        final Evaluator integerGreater = ValueType.INTEGER_TYPE.getEvaluator( Operator.GREATER );

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

        final Evaluator integerLess = ValueType.INTEGER_TYPE.getEvaluator( Operator.LESS );

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

        final Cheese cheddar = new Cheese( "cheddar",
                                           15 );
        
        final InternalFactHandle cheddarHandle = (InternalFactHandle) workingMemory.assertObject( cheddar );

        // check constraint
        assertTrue( constraint.isAllowed( cheddarHandle.getObject(),
                                          workingMemory ) );

        cheddar.setPrice( 5 );
        ((ShadowProxy) cheddarHandle.getObject()).updateProxy();
        assertFalse( constraint.isAllowed( cheddarHandle.getObject(),
                                           workingMemory ) );
        
        cheddar.setType( "stilton" );
        ((ShadowProxy) cheddarHandle.getObject()).updateProxy();
        assertTrue( constraint.isAllowed( cheddarHandle.getObject(),
                                           workingMemory ) );
        
        cheddar.setPrice( 15 );
        ((ShadowProxy) cheddarHandle.getObject()).updateProxy();
        assertFalse( constraint.isAllowed( cheddarHandle.getObject(),
                                           workingMemory ) );
    }
    
}