package org.drools.rule;

import junit.framework.TestCase;

import org.drools.FactHandle;
import org.drools.reteoo.InstrumentedReteTuple;
import org.drools.reteoo.InstrumentedWorkingMemoryImpl;
import org.drools.spi.ClassObjectType;
import org.drools.spi.ConstraintComparator;
import org.drools.spi.Extractor;
import org.drools.spi.LiteralExpressionConstraint;
import org.drools.spi.ObjectType;
import org.drools.spi.PredicateExpressionConstraint;
import org.drools.spi.ReturnValueExpressionConstraint;
import org.drools.spi.Tuple;

public class ConstraintTest extends TestCase {
    public ConstraintTest(){
        super();
    }

    /**
     * <pre>
     *  
     *   
     *          ( Cheese (type &quot;cheddar&quot;) )
     *    
     *   
     * </pre>
     * 
     * This is currently the same as using a ReturnValueConstraint just that it
     * doesn't need any requiredDeclarations
     */
    public void testLiteralConstraint(){

        LiteralExpressionConstraint isCheddar = new LiteralExpressionConstraint() {

            public boolean isAllowed(Object object,
                                     ConstraintComparator comparator){
                Cheese cheese = (Cheese) object;
                return comparator.compare( cheese.getType(),
                                           "cheddar" );
            }

        };

        /*
         * Creates a constraint with the given expression
         */
        LiteralConstraint constraint0 = new LiteralConstraint( isCheddar,
                                                               new StringConstraintComparator( ConstraintComparator.EQUAL ) );
        Cheese cheddar = new Cheese( "cheddar",
                                     5 );

        /* check constraint */
        assertTrue( constraint0.isAllowed( cheddar ) );

        Cheese stilton = new Cheese( "stilton",
                                     5 );

        /* check constraint */
        assertFalse( constraint0.isAllowed( stilton ) );
    }

    /**
     * <pre>
     *  
     *       
     *          ( Cheese ( type ?typeOfCheese ) )  
     *    
     *   
     * </pre>
     */
    public void testBoundConstraint(){
        InstrumentedWorkingMemoryImpl workingMemory = new InstrumentedWorkingMemoryImpl();

        ObjectType stringObjectType = new ClassObjectType( String.class );

        /* Determines how the bound value is extracted from the column */
        Extractor typeOfCheeseExtractor = new Extractor() {
            public Object getValue(Object object){
                return ((Cheese) object).getType();
            }
        };

        /* Bind the extractor to a decleration */
        /* Declarations know the column they derive their value form */
        Declaration typeOfCheeseDeclaration = new Declaration( 0,
                                                               "typeOfCheese",
                                                               stringObjectType,
                                                               typeOfCheeseExtractor,
                                                               0 );

        /* Create some facts */
        Cheese cheddar = new Cheese( "cheddar",
                                     5 );

        FactHandle f0 = workingMemory.createFactHandle( 0 );
        workingMemory.putObject( f0,
                                 cheddar );
        InstrumentedReteTuple tuple = new InstrumentedReteTuple( 0,
                                                                 f0,
                                                                 workingMemory );

        /* check constraint on the column */
        assertEquals( "cheddar",
                      tuple.get( typeOfCheeseDeclaration ) );
    }

    /**
     * <pre>
     *  
     *   
     *          (Cheese (type ?typeOfCheese ) 
     *          (Cheese (type ?typeOfCheese )
     *    
     *   
     * </pre>
     * 
     * In this case its really up to the compiler to realise the second binding
     * really is actually a constraint, ie making sure it has the same value as
     * derived from the first column's type field.
     */
    public void testDoubleBoundConstraint(){
        InstrumentedWorkingMemoryImpl workingMemory = new InstrumentedWorkingMemoryImpl();

        ObjectType stringObjectType = new ClassObjectType( String.class );

        /* Determines how the bound value is extracted from the column */
        Extractor typeOfCheeseExtractor = new Extractor() {
            public Object getValue(Object object){
                return ((Cheese) object).getType();
            }
        };

        /* Bind the extractor to a decleration */
        /* Declarations know the column they derive their value form */
        Declaration typeOfCheeseDeclaration = new Declaration( 0,
                                                               "typeOfCheese",
                                                               stringObjectType,
                                                               typeOfCheeseExtractor,
                                                               0 );

        ReturnValueExpressionConstraint isCheddar = new ReturnValueExpressionConstraint() {
            public boolean isAllowed(Object object,
                                     FactHandle handle,
                                     Declaration[] declarations,
                                     Tuple tuple,
                                     ConstraintComparator comparator){

                return comparator.compare( ((Cheese) object).getType(),
                                           tuple.get( declarations[0] ) );
            }

        };

        /*
         * Creates a constraint with an expression and an array of required
         * declarations
         */
        ReturnValueConstraint constraint1 = new ReturnValueConstraint( isCheddar,
                                                                       new Declaration[]{typeOfCheeseDeclaration},
                                                                       new StringConstraintComparator( ConstraintComparator.EQUAL ) );

        Cheese cheddar0 = new Cheese( "cheddar",
                                      5 );
        FactHandle f0 = workingMemory.createFactHandle( 0 );
        workingMemory.putObject( f0,
                                 cheddar0 );
        InstrumentedReteTuple tuple = new InstrumentedReteTuple( 0,
                                                                 f0,
                                                                 workingMemory );

        Cheese cheddar1 = new Cheese( "cheddar",
                                      5 );
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

        cheddar1 = new Cheese( "stilton",
                               5 );
        workingMemory.putObject( f1,
                                 cheddar1 );

        /*
         * simulate a modify, so we can check for a false assertion.
         */
        assertFalse( constraint1.isAllowed( cheddar1,
                                            f1,
                                            tuple ) );
    }

    /**
     * <pre>
     *  
     *   
     *          (Cheese (price ?price1 ) 
     *          (Cheese (price ?price2&amp;:(= ?price2 (* 2 ?price1) )
     *    
     *   
     * </pre>
     */
    public void testPredicateExpressionConstraint(){
        InstrumentedWorkingMemoryImpl workingMemory = new InstrumentedWorkingMemoryImpl();

        ObjectType integerObjectType = new ClassObjectType( Integer.class );

        /* Determines how the bound value is extracted from the column */
        Extractor priceOfCheeseExtractor = new Extractor() {
            public Object getValue(Object object){
                return new Integer( ((Cheese) object).getPrice() );
            }
        };

        /* Bind the extractor to a decleration */
        /* Declarations know the column they derive their value form */
        Declaration price1Declaration = new Declaration( 0,
                                                         "price1",
                                                         integerObjectType,
                                                         priceOfCheeseExtractor,
                                                         0 );

        /* Bind the extractor to a decleration */
        /* Declarations know the column they derive their value form */
        Declaration price2Declaration = new Declaration( 1,
                                                         "price2",
                                                         integerObjectType,
                                                         priceOfCheeseExtractor,
                                                         1 );

        PredicateExpressionConstraint isDoubleThePrice = new PredicateExpressionConstraint() {
            public boolean isAllowed(Object object,
                                     FactHandle handle,
                                     Declaration declaration, // ?price2
                                     Declaration[] declarations, // ?price1
                                     Tuple tuple){
                int price1 = ((Integer) tuple.get( declarations[0] )).intValue();
                int price2 = ((Integer) tuple.get( declaration )).intValue();
                return (price2 == (price1 * 2));

            }
        };

        PredicateConstraint constraint1 = new PredicateConstraint( isDoubleThePrice,
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
     *          (Cheese (price ?price ) 
     *          (Cheese (price =(* 2 ?price) )
     *          (Cheese (price &gt;(* 2 ?price) )
     *    
     *   
     * </pre>
     */
    public void testReturnValueConstraint(){
        InstrumentedWorkingMemoryImpl workingMemory = new InstrumentedWorkingMemoryImpl();

        ObjectType integerObjectType = new ClassObjectType( Integer.class );

        /* Determines how the bound value is extracted from the column */
        Extractor priceOfCheeseExtractor = new Extractor() {
            public Object getValue(Object object){
                return new Integer( ((Cheese) object).getPrice() );
            }
        };

        /* Bind the extractor to a decleration */
        /* Declarations know the column they derive their value form */
        Declaration priceDeclaration = new Declaration( 0,
                                                        "price",
                                                        integerObjectType,
                                                        priceOfCheeseExtractor,
                                                        0 );

        ReturnValueExpressionConstraint isDoubleThePrice = new ReturnValueExpressionConstraint() {
            public boolean isAllowed(Object object,
                                     FactHandle handle,
                                     Declaration[] declarations, // ?price
                                     Tuple tuple,
                                     ConstraintComparator comparator){
                int price = ((Integer) tuple.get( declarations[0] )).intValue();

                return comparator.compare( new Integer( ((Cheese) object).getPrice() ),
                                           new Integer( 2 * price ) );
            }
        };

        ReturnValueConstraint constraint1 = new ReturnValueConstraint( isDoubleThePrice,
                                                                       new Declaration[]{priceDeclaration},
                                                                       new NumericConstraintComparator( ConstraintComparator.EQUAL ) );

        ReturnValueConstraint constraint2 = new ReturnValueConstraint( isDoubleThePrice,
                                                                       new Declaration[]{priceDeclaration},
                                                                       new NumericConstraintComparator( ConstraintComparator.GREATER ) );

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
    }

    static public class Cheese {
        private String type;

        private int    price;

        public Cheese(String type,
                      int price){
            this.type = type;
            this.price = price;
        }

        public String getType(){
            return this.type;
        }

        public int getPrice(){
            return this.price;
        }

        public boolean equals(Object object){
            return this.type.equals( ((Cheese) object).getType() );
        }
    }

    static public class Person {
        private String name;

        public Person(String name){
            this.name = name;
        }

        public String getName(){
            return this.name;
        }

    }
}
