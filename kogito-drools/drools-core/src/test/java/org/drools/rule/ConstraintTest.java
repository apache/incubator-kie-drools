package org.drools.rule;

import java.beans.IntrospectionException;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.FactHandle;
import org.drools.spi.MockField;
import org.drools.reteoo.InstrumentedReteTuple;
import org.drools.reteoo.InstrumentedWorkingMemoryImpl;
import org.drools.spi.ClassFieldExtractor;
import org.drools.spi.Evaluator;
import org.drools.spi.Field;
import org.drools.spi.FieldExtractor;
import org.drools.spi.PredicateEvaluator;
import org.drools.spi.ReturnValueEvaluator;
import org.drools.spi.Tuple;

public class ConstraintTest extends TestCase {
    public ConstraintTest() {
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
        int index = Cheese.getIndex( Cheese.class,
                                     "type" );

        Field field = new MockField( "type",
                                     "cheddar",
                                     index );

        FieldExtractor extractor = new ClassFieldExtractor( Cheese.class,
                                                            field.getIndex() );

        Evaluator evaluator = EvaluatorFactory.getInstance().getEvaluator( Evaluator.OBJECT_TYPE,
                                                                           Evaluator.EQUAL );
        LiteralConstraint constraint = new LiteralConstraint( field,
                                                              extractor,
                                                              evaluator );

        Cheese cheddar = new Cheese( "cheddar",
                                     5 );

        // check constraint
        assertTrue( constraint.isAllowed( cheddar ) );

        Cheese stilton = new Cheese( "stilton",
                                     5 );

        // check constraint
        assertFalse( constraint.isAllowed( stilton ) );
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
        InstrumentedWorkingMemoryImpl workingMemory = new InstrumentedWorkingMemoryImpl();

        FieldExtractor priceExtractor = new ClassFieldExtractor( Cheese.class,
                                                                 Cheese.getIndex( Cheese.class,
                                                                                  "price" ) );

        // Bind the extractor to a decleration
        // Declarations know the column they derive their value form
        Declaration price1Declaration = new Declaration( 0,
                                                         "price1",
                                                         priceExtractor,
                                                         0 );

        // Bind the extractor to a decleration
        // Declarations know the column they derive their value form
        Declaration price2Declaration = new Declaration( 1,
                                                         "price2",
                                                         priceExtractor,
                                                         1 );

        PredicateEvaluator evaluator = new PredicateEvaluator() {

            public boolean evaluate(Tuple tuple,
                                    Object object,
                                    FactHandle handle,
                                    Declaration declaration,
                                    Declaration[] declarations) {
                int price1 = ((Integer) tuple.get( declarations[0] )).intValue();
                int price2 = ((Integer) declaration.getValue( object )).intValue();
                return (price2 == (price1 * 2));

            }
        };

        PredicateConstraint constraint1 = new PredicateConstraint( evaluator,
                                                                   price2Declaration,
                                                                   new Declaration[]{price1Declaration} );

        Cheese cheddar0 = new Cheese( "cheddar",
                                      5 );
        FactHandle f0 = workingMemory.createFactHandle( 0 );
        workingMemory.putObject( f0,
                                 cheddar0 );
        InstrumentedReteTuple tuple = new InstrumentedReteTuple( 0,
                                                                 f0,
                                                                 workingMemory );

        Cheese cheddar1 = new Cheese( "cheddar",
                                      10 );
        FactHandle f1 = workingMemory.createFactHandle( 1 );
        workingMemory.putObject( f1,
                                 cheddar1 );
        tuple = new InstrumentedReteTuple( tuple,
                                           new InstrumentedReteTuple( 1,
                                                                      f1,
                                                                      workingMemory ) );

        assertTrue( constraint1.isAllowed( cheddar1,
                                           f1,
                                           tuple ) );
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
        InstrumentedWorkingMemoryImpl workingMemory = new InstrumentedWorkingMemoryImpl();

        FieldExtractor priceExtractor = new ClassFieldExtractor( Cheese.class,
                                                                 Cheese.getIndex( Cheese.class,
                                                                                  "price" ) );

        // Bind the extractor to a decleration
        // Declarations know the column they derive their value form
        Declaration priceDeclaration = new Declaration( 0,
                                                        "price1",
                                                        priceExtractor,
                                                        0 );

        ReturnValueEvaluator isDoubleThePrice = new ReturnValueEvaluator() {
            public Object evaluate(Tuple tuple, // ?price
                                   Declaration[] declarations) {
                return new Integer( 2 * ((Integer) tuple.get( declarations[0] )).intValue() );

            }
        };

        ReturnValueConstraint constraint1 = new ReturnValueConstraint( priceExtractor,
                                                                       isDoubleThePrice,
                                                                       new Declaration[]{priceDeclaration},
                                                                       EvaluatorFactory.getInstance().getEvaluator( Evaluator.INTEGER_TYPE,
                                                                                                                    Evaluator.EQUAL ) );

        ReturnValueConstraint constraint2 = new ReturnValueConstraint( priceExtractor,
                                                                       isDoubleThePrice,
                                                                       new Declaration[]{priceDeclaration},
                                                                       EvaluatorFactory.getInstance().getEvaluator( Evaluator.INTEGER_TYPE,
                                                                                                                    Evaluator.GREATER ) );

        Cheese cheddar0 = new Cheese( "cheddar",
                                      5 );
        FactHandle f0 = workingMemory.createFactHandle( 0 );
        workingMemory.putObject( f0,
                                 cheddar0 );
        InstrumentedReteTuple tuple = new InstrumentedReteTuple( 0,
                                                                 f0,
                                                                 workingMemory );

        Cheese cheddar1 = new Cheese( "cheddar",
                                      10 );
        FactHandle f1 = workingMemory.createFactHandle( 1 );
        workingMemory.putObject( f1,
                                 cheddar1 );
        tuple = new InstrumentedReteTuple( tuple,
                                           new InstrumentedReteTuple( 0,
                                                                      f1,
                                                                      workingMemory ) );

        assertTrue( constraint1.isAllowed( cheddar1,
                                           f1,
                                           tuple ) );

        assertFalse( constraint2.isAllowed( cheddar1,
                                            f1,
                                            tuple ) );

        cheddar1 = new Cheese( "cheddar",
                               11 );

        workingMemory.putObject( f1,
                                 cheddar1 );

        assertTrue( constraint2.isAllowed( cheddar1,
                                           f1,
                                           tuple ) );
    }

}
