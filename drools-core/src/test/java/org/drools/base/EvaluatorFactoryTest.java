package org.drools.base;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.drools.spi.Evaluator;

import junit.framework.TestCase;

/**
 * Some test coverage goodness for the evaluators.
 * Evaluator concrete instances are inside the factory at this time.
 * @author Michael Neale
 */
public class EvaluatorFactoryTest extends TestCase {

    public void testObject() {

        List list = new ArrayList();
        list.add( "foo" ); 

        Object[][] data = { {"foo", "==", "bar", Boolean.FALSE},
                            {"foo", "==", "foo", Boolean.TRUE},
                            {"foo", "!=", "bar", Boolean.TRUE},
                            {list, "contains", "foo", Boolean.TRUE},
                            {list, "contains", "bar", Boolean.FALSE}
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
                            {new Integer(42), "!=", new Integer(41), Boolean.TRUE},
                            {new Integer(42), ">", new Integer(41), Boolean.TRUE},
                            {new Integer(42), "<=", new Integer(42), Boolean.TRUE},
                            {new Integer(42), ">", new Integer(100), Boolean.FALSE}
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
                            {new Boolean(false), "!=", new Boolean(true), Boolean.TRUE},
                            {new Boolean(true), "==", new Boolean(false), Boolean.FALSE},
                            {new Boolean(true), "!=", new Boolean(false), Boolean.TRUE}
                          };

        runEvaluatorTest( data,
                          Evaluator.BOOLEAN_TYPE );
    }  
    
    public void testDouble() {
        Object[][] data = { {new Double(42), "==", new Double(42), Boolean.TRUE},
                            {new Double(42), "<", new Double(43), Boolean.TRUE},
                            {new Double(42), ">=", new Double(41), Boolean.TRUE},
                            {new Double(42), "!=", new Double(41), Boolean.TRUE},
                            {new Double(42), ">", new Double(41), Boolean.TRUE},
                            {new Double(42), ">=", new Double(41), Boolean.TRUE},
                            {new Double(42), ">=", new Double(42), Boolean.TRUE},
                            {new Double(42), ">=", new Double(100), Boolean.FALSE},
                            {new Double(42), "<", new Double(1), Boolean.FALSE}
                          };

        runEvaluatorTest( data,
                          Evaluator.DOUBLE_TYPE );
    }  
    
    public void testFloat() {
        Object[][] data = { {new Float(42), "==", new Float(42), Boolean.TRUE},
                            {new Float(42), "<", new Float(43), Boolean.TRUE},
                            {new Float(42), ">=", new Float(41), Boolean.TRUE},
                            {new Float(42), "!=", new Float(41), Boolean.TRUE},
                            {new Float(42), ">", new Float(41), Boolean.TRUE},
                            {new Float(42), ">=", new Float(41), Boolean.TRUE},
                            {new Float(42), ">=", new Float(42), Boolean.TRUE},
                            {new Float(42), ">=", new Float(100), Boolean.FALSE},
                            {new Float(42), "<", new Float(1), Boolean.FALSE}
                          };

        runEvaluatorTest( data,
                          Evaluator.FLOAT_TYPE );
    }     
    
    public void testLong() {
        Object[][] data = { {new Long(42), "==", new Long(42), Boolean.TRUE},
                            {new Long(42), "<", new Long(43), Boolean.TRUE},
                            {new Long(42), ">=", new Long(41), Boolean.TRUE},
                            {new Long(42), "!=", new Long(41), Boolean.TRUE},
                            {new Long(42), ">", new Long(41), Boolean.TRUE},
                            {new Long(42), ">=", new Long(41), Boolean.TRUE},
                            {new Long(42), ">=", new Long(42), Boolean.TRUE},
                            {new Long(42), ">=", new Long(100), Boolean.FALSE},
                            {new Long(42), "<", new Long(1), Boolean.FALSE}
                          };

        runEvaluatorTest( data,
                          Evaluator.LONG_TYPE );
    }      
    
    public void testCharacter() {
        Object[][] data = { {new Character('a'), "==", new Character('a'), Boolean.TRUE},
                            {new Character('a'), "<", new Character('b'), Boolean.TRUE},
                            {new Character('a'), ">=", new Character('a'), Boolean.TRUE},
                            {new Character('a'), "!=", new Character('Z'), Boolean.TRUE},
                            {new Character('z'), ">", new Character('a'), Boolean.TRUE},
                            {new Character('z'), ">=", new Character('z'), Boolean.TRUE},
                            {new Character('z'), ">=", new Character('a'), Boolean.TRUE},
                            {new Character('a'), ">=", new Character('z'), Boolean.FALSE},
                            {new Character('z'), "<", new Character('a'), Boolean.FALSE}
                          };
        runEvaluatorTest( data,
                          Evaluator.CHAR_TYPE );
    }  
    
    public void testDate() throws Exception {

        
        
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

        //note that strings are also allowed on the right
        Object[][] data = { {df.parse( "10-Jul-1974" ), "==", df.parse( "10-Jul-1974" ), Boolean.TRUE},
                            {df.parse( "10-Jul-1974" ), "<", df.parse( "11-Jul-1974" ), Boolean.TRUE},
                            {df.parse( "10-Jul-1974" ), ">=", df.parse( "10-Jul-1974" ), Boolean.TRUE},
                            {df.parse( "10-Jul-1974" ), "!=", df.parse( "11-Jul-1974" ), Boolean.TRUE},
                            {df.parse( "10-Jul-2000" ), ">", df.parse( "10-Jul-1974" ), Boolean.TRUE},
                            {df.parse( "10-Jul-1974" ), ">=", df.parse( "10-Jul-1974" ), Boolean.TRUE},
                            {df.parse( "11-Jul-1974" ), ">=", df.parse( "10-Jul-1974" ), Boolean.TRUE},
                            {df.parse( "10-Jul-1974" ), ">=", df.parse( "11-Jul-1974" ), Boolean.FALSE},
                            {df.parse( "10-Jul-2000" ), "<", df.parse( "10-Jul-1974" ), Boolean.FALSE},
                            {df.parse( "10-Jul-1974" ), "<", "11-Jul-1974" , Boolean.TRUE},
                            {df.parse( "10-Jul-1974" ), ">=", "10-Jul-1974", Boolean.TRUE},
                            {df.parse( "10-Jul-1974" ), "!=", "11-Jul-1974", Boolean.TRUE},
                            {df.parse( "10-Jul-2000" ), ">", "10-Jul-1974", Boolean.TRUE},
                            {df.parse( "10-Jul-1974" ), ">=", "10-Jul-1974", Boolean.TRUE},
                            {df.parse( "11-Jul-1974" ), ">=", "10-Jul-1974", Boolean.TRUE},
                            {df.parse( "10-Jul-1974" ), ">=", "11-Jul-1974", Boolean.FALSE},                            
                          };
        runEvaluatorTest( data,
                          Evaluator.DATE_TYPE );
    }    
    

    
    public void testByte() {
        Object[][] data = { {new Byte("1"), "==", new Byte("1"), Boolean.TRUE},
                            {new Byte("1"), "==", new Byte("2"), Boolean.FALSE},
                            {new Byte("1"), "!=", new Byte("2"), Boolean.TRUE},
                            {new Byte("1"), "!=", new Byte("1"), Boolean.FALSE},
                            {new Byte("1"), "<=", new Byte("1"), Boolean.TRUE}
                          };
        runEvaluatorTest( data,
                          Evaluator.BYTE_TYPE );
        
    }     

    /**
     * Test utility to play the data through the evaluators.
     * @param data The data to try out : Array of {arg1, operator, arg2}
     * @param evalType The Evaluator.**_TYPE to test
     */
    private void runEvaluatorTest(Object[][] data,
                                  int evalType) {
        for ( int i = 0; i < data.length; i++ ) {
            Object[] row = data[i];
            Evaluator evaluator = EvaluatorFactory.getEvaluator(evalType, (String) row[1]); 
            boolean result =  evaluator.evaluate( row[0], row[2] );
            String message = "The evaluator type: [" + evalType + "] incorrectly returned " + result + " for [" 
                + row[0] + " " + row[1] +  " " + row[2] + "]. It was asserted to return " + row[3];
            
            if (row[3] == Boolean.TRUE) {
                assertTrue( message, result );               
            } else {
                assertFalse( message, result );
            }
            
            //check the type is being set, Strings are treated as objects for now...
            if (evalType == Evaluator.STRING_TYPE) {
                assertEquals("Strings are objects", Evaluator.OBJECT_TYPE, evaluator.getType());
            } else {
                assertEquals(evalType, evaluator.getType());
            }
            
        }
    }
    
}
