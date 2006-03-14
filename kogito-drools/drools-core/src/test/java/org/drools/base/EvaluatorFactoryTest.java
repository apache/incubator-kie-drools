package org.drools.base;

import java.util.ArrayList;
import java.util.List;

import org.drools.spi.Evaluator;

import junit.framework.TestCase;

/**
 * Some test coverage goodness for the evaluators.
 * Evaluator concrete instances are inside the factory at this time.
 * @author Michael Neale
 */
public class EvaluatorFactoryTest extends TestCase {

    public void testFactoryMethod() {
        assertNotNull(EvaluatorFactory.getInstance());
    }
    
    public void testObject() {

        List list = new ArrayList();
        list.add( "foo" ); 

        Object[][] data = { {"foo", "==", "bar", Boolean.FALSE},
                            {"foo", "==", "foo", Boolean.TRUE},
                            {"foo", "!=", "bar", Boolean.TRUE},
                            {"foo", "contains", list, Boolean.TRUE},
                            {"bar", "contains", list, Boolean.FALSE}
                          };

        runEvaluatorTest( data, Evaluator.OBJECT_TYPE );
        
        
    }
    
    public void testString() {

        Object[][] data = { {"foo", "==", "bar", Boolean.FALSE},
                            {"foo", "==", "foo", Boolean.TRUE},
                            {"foo", "!=", "bar", Boolean.TRUE},
                          };

        runEvaluatorTest( data, Evaluator.STRING_TYPE );
        
        
    }    
    
    public void testInteger() {
        
        
        Object[][] data = { {new Integer(42), "==", new Integer(42), Boolean.TRUE},
                            {new Integer(42), "<", new Integer(43), Boolean.TRUE},
                            {new Integer(42), ">=", new Integer(41), Boolean.TRUE},
                            {new Integer(42), "!=", new Integer(41), Boolean.TRUE}
                          };

        
        runEvaluatorTest( data,
                          Evaluator.INTEGER_TYPE );
        
        
    }
    
    public void testShort() {
        
        //Test data: Obj1, Operand, Obj2
        Object[][] data = { {new Short( (short) 42), "==", new Short((short)42), Boolean.TRUE},
                            {new Short( (short) 42), "<", new Short((short)43), Boolean.TRUE},
                            {new Short( (short) 42), ">=", new Short((short)41), Boolean.TRUE},
                            {new Short( (short) 42), "!=", new Short((short)41), Boolean.TRUE}
                          };

        runEvaluatorTest( data,
                          Evaluator.SHORT_TYPE );
    }    
    
    public void testBoolean() {
        
        //Test data: Obj1, Operand, Obj2
        Object[][] data = { {new Boolean(true), "==", new Boolean(true), Boolean.TRUE},
                            {new Boolean(false), "!=", new Boolean(true), Boolean.TRUE}
                          };

        runEvaluatorTest( data,
                          Evaluator.BOOLEAN_TYPE );
    }       

    private void runEvaluatorTest(Object[][] data,
                                  int evalType) {
        for ( int i = 0; i < data.length; i++ ) {
            Object[] row = data[i];
            boolean result = EvaluatorFactory.getEvaluator(evalType, (String) row[1]).evaluate( row[0], row[2] );
            String message = "The evaluator type: [" + evalType + "] incorrectly returned " + result + " for [" 
                + row[0] + " " + row[1] +  " " + row[2] + "]. It was asserted to return " + row[3];
            
            if (row[3] == Boolean.TRUE) {
                assertTrue( message, result );               
            } else {
                assertFalse( message, result );
            }
        }
    }
    
}
