package org.drools.base;

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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import junit.framework.TestCase;

import org.drools.base.evaluators.Operator;
import org.drools.spi.Evaluator;

/**
 * Some test coverage goodness for the evaluators.
 * Evaluator concrete instances are inside the factory at this time.
 * @author Michael Neale
 */
public class EvaluatorFactoryTest extends TestCase {

    public void testObject() {

        final List list = new ArrayList();
        list.add( "foo" );

        final Object[][] data = {
                {"foo", "==", "bar", Boolean.FALSE}, 
                {"foo", "==", "foo", Boolean.TRUE}, 
                {"foo", "!=", "bar", Boolean.TRUE}, 
                {list, "contains", "foo", Boolean.TRUE}, 
                {list, "contains", "bar", Boolean.FALSE},
                {list, "==", null, Boolean.FALSE}, 
                {list, "!=", null, Boolean.TRUE}, 
                {null, "==", null, Boolean.TRUE}, 
                {null, "==", list, Boolean.FALSE}, 
                {null, "!=", list, Boolean.TRUE}, 
                {new BigDecimal( "42.42" ), "<", new BigDecimal( "43" ), Boolean.TRUE},
                {new BigDecimal( "42.42" ), ">", new BigDecimal( "43" ), Boolean.FALSE}, 
                {new BigDecimal( "42.42" ), "<=", new BigDecimal( "42.42" ), Boolean.TRUE}, 
                {new BigInteger( "42" ), ">=", new BigInteger( "43" ), Boolean.FALSE},
                {new BigInteger( "42" ), ">=", new BigInteger( "43" ), Boolean.FALSE}, 
                {list, "excludes", "baz", Boolean.TRUE}, 
                {list, "excludes", "foo", Boolean.FALSE}

        };

        runEvaluatorTest( data,
                          ValueType.OBJECT_TYPE );

    }

    public void testArrayType() {

        final Object[] field = new Object[]{"foo", "bar"};

        final Object[][] data = {
                {field, "==", new Object[]{"foo"}, Boolean.FALSE}, 
                {field, "==", field, Boolean.TRUE}, 
                {field, "!=", new Object[]{"foo"}, Boolean.TRUE}, 
                {field, "contains", "foo", Boolean.TRUE}, 
                {field, "!=", null, Boolean.TRUE},
                {field, "==", null, Boolean.FALSE}, 
                {null, "==", field, Boolean.FALSE},
                {null, "==", null, Boolean.TRUE},
                {null, "!=", field, Boolean.TRUE}, 
                {null, "!=", null, Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.ARRAY_TYPE );

    }

    public void testString() {

        final Object[][] data = {
                {"foo", "==", "bar", Boolean.FALSE}, 
                {"foo", "==", "foo", Boolean.TRUE}, 
                {"foo", "!=", "bar", Boolean.TRUE}, 
                {"something foo", "matches", ".*foo", Boolean.TRUE}, 
                {"foo", "matches", ".*foo", Boolean.TRUE},
                {"foo", "matches", "bar", Boolean.FALSE}, 
                {null, "matches", ".*foo", Boolean.FALSE}, 
                {"foo", "==", null, Boolean.FALSE}, 
                {"foo", "!=", null, Boolean.TRUE}, 
                {null, "==", null, Boolean.TRUE}, 
                {"foo", "!=", null, Boolean.TRUE},
                {null, "!=", "foo", Boolean.TRUE}, 
                {null, "!=", null, Boolean.FALSE}
        };

        runEvaluatorTest( data,
                          ValueType.STRING_TYPE );

    }

    public void testInteger() {

        final Object[][] data = {
                {new Integer( 42 ), "==", new Integer( 42 ), Boolean.TRUE}, 
                {new Integer( 42 ), "<", new Integer( 43 ), Boolean.TRUE}, 
                {new Integer( 42 ), ">=", new Integer( 41 ), Boolean.TRUE},
                {new Integer( 42 ), "!=", new Integer( 41 ), Boolean.TRUE}, 
                {new Integer( 42 ), ">", new Integer( 41 ), Boolean.TRUE}, 
                {new Integer( 42 ), "<=", new Integer( 42 ), Boolean.TRUE},
                {new Integer( 42 ), ">", new Integer( 100 ), Boolean.FALSE}, 
                {new Integer( 42 ), "==", null, Boolean.FALSE}, 
                {new Integer( 42 ), "!=", null, Boolean.TRUE}, 
                {null, "==", null, Boolean.TRUE}, 
                {null, "==", new Integer( 42 ), Boolean.FALSE},
                {null, "!=", null, Boolean.FALSE}, 
                {null, "!=", new Integer( 42 ), Boolean.TRUE}};

        runEvaluatorTest( data,
                          ValueType.INTEGER_TYPE );

    }
    
    public void testBigDecimal() {

        ValueType type = ValueType.determineValueType(BigDecimal.class);
        assertSame(type, ValueType.BIG_DECIMAL_TYPE);
    	
        final Object[][] data = {{new BigDecimal( 42 ), "==", new BigDecimal( 42 ), Boolean.TRUE}, 
        		{new BigDecimal( 42 ), "<", new BigDecimal( 43 ), Boolean.TRUE}, {new BigDecimal( 42 ), ">=", new BigDecimal( 41 ), Boolean.TRUE},
                {new BigDecimal( 42 ), "!=", new BigDecimal( 41 ), Boolean.TRUE}, {new BigDecimal( 42 ), ">", new BigDecimal( 41 ), Boolean.TRUE}, {new BigDecimal( 42 ), "<=", new BigDecimal( 42 ), Boolean.TRUE},
                {new BigDecimal( 42 ), ">", new BigDecimal( 100 ), Boolean.FALSE}, {new BigDecimal( 42 ), "==", null, Boolean.FALSE}, {new BigDecimal( 42 ), "!=", null, Boolean.TRUE}, {null, "==", new BigDecimal( 42 ), Boolean.FALSE},
                {null, "!=", new BigDecimal( 42 ), Boolean.TRUE}};

        runEvaluatorTest( data,
                          ValueType.BIG_DECIMAL_TYPE );

        
 
    }
    
    public void testBigInteger() {

        ValueType type = ValueType.determineValueType(BigInteger.class);
        assertSame(type, ValueType.BIG_INTEGER_TYPE);
    	
        final Object[][] data = {{new BigInteger( "42" ), "==", new BigInteger( "42" ), Boolean.TRUE}, 
        		{new BigInteger( "42" ), "<", new BigInteger( "43" ), Boolean.TRUE}, {new BigInteger( "42" ), ">=", new BigInteger( "41" ), Boolean.TRUE},
                {new BigInteger( "42" ), "!=", new BigInteger( "41" ), Boolean.TRUE}, {new BigInteger( "42" ), ">", new BigInteger( "41" ), Boolean.TRUE}, {new BigInteger( "42" ), "<=", new BigInteger( "42" ), Boolean.TRUE},
                {new BigInteger( "42" ), ">", new BigInteger( "100" ), Boolean.FALSE}, {new BigInteger( "42" ), "==", null, Boolean.FALSE}, {new BigInteger( "42" ), "!=", null, Boolean.TRUE}, {null, "==", new BigInteger( "42" ), Boolean.FALSE},
                {null, "!=", new BigInteger( "42" ), Boolean.TRUE}};

        runEvaluatorTest( data,
                          ValueType.BIG_INTEGER_TYPE );

        

    }    
    

    public void testShort() {

        //Test data: Obj1, Operand, Obj2
        final Object[][] data = {
                {new Short( (short) 42 ), "==", new Short( (short) 42 ), Boolean.TRUE}, 
                {new Short( (short) 42 ), "<", new Short( (short) 43 ), Boolean.TRUE}, 
                {new Short( (short) 42 ), ">=", new Short( (short) 41 ), Boolean.TRUE},
                {new Short( (short) 42 ), "!=", new Short( (short) 41 ), Boolean.TRUE}, 
                {new Short( (short) 42 ), "!=", null, Boolean.TRUE},
                {null, "==", null, Boolean.TRUE}, 
                {null, "==", new Short( (short) 42 ), Boolean.FALSE},
                {null, "!=", null, Boolean.FALSE}, 
                {null, "!=", new Short( (short) 42 ), Boolean.TRUE}};

        runEvaluatorTest( data,
                          ValueType.SHORT_TYPE );
    }

    public void testBoolean() {

        //Test data: Obj1, Operand, Obj2
        final Object[][] data = {
                {new Boolean( true ), "==", new Boolean( true ), Boolean.TRUE}, 
                {new Boolean( false ), "!=", new Boolean( true ), Boolean.TRUE}, 
                {new Boolean( true ), "==", new Boolean( false ), Boolean.FALSE},
                {new Boolean( true ), "!=", new Boolean( false ), Boolean.TRUE}, 
                {new Boolean( true ), "==", null, Boolean.FALSE}, 
                {new Boolean( true ), "!=", null, Boolean.TRUE}, 
                {null, "==", new Boolean( true ), Boolean.FALSE}, 
                {null, "!=", new Boolean( true ), Boolean.TRUE}, 
                {null, "==", null, Boolean.TRUE}

        };

        runEvaluatorTest( data,
                          ValueType.BOOLEAN_TYPE );
    }

    public void testDouble() {
        final Object[][] data = {
                {new Double( 42 ), "==", new Double( 42 ), Boolean.TRUE}, 
                {new Double( 42 ), "<", new Double( 43 ), Boolean.TRUE}, 
                {new Double( 42 ), ">=", new Double( 41 ), Boolean.TRUE},
                {new Double( 42 ), "!=", new Double( 41 ), Boolean.TRUE}, 
                {new Double( 42 ), ">", new Double( 41 ), Boolean.TRUE}, 
                {new Double( 42 ), ">=", new Double( 41 ), Boolean.TRUE}, 
                {new Double( 42 ), ">=", new Double( 42 ), Boolean.TRUE},
                {new Double( 42 ), ">=", new Double( 100 ), Boolean.FALSE}, 
                {new Double( 42 ), "<", new Double( 1 ), Boolean.FALSE}, 
                {new Double( 42 ), "==", null, Boolean.FALSE}, 
                {new Double( 42 ), "!=", null, Boolean.TRUE}, 
                {null, "==", null, Boolean.TRUE}, 
                {null, "==", new Double( 42 ), Boolean.FALSE},
                {null, "!=", null, Boolean.FALSE}, 
                {null, "!=", new Double( 42 ), Boolean.TRUE}};

        runEvaluatorTest( data,
                          ValueType.DOUBLE_TYPE );
    }

    public void testFloat() {
        final Object[][] data = {
                {new Float( 42 ), "==", new Float( 42 ), Boolean.TRUE}, 
                {new Float( 42 ), "<", new Float( 43 ), Boolean.TRUE}, 
                {new Float( 42 ), ">=", new Float( 41 ), Boolean.TRUE},
                {new Float( 42 ), "!=", new Float( 41 ), Boolean.TRUE}, 
                {new Float( 42 ), ">", new Float( 41 ), Boolean.TRUE}, 
                {new Float( 42 ), ">=", new Float( 41 ), Boolean.TRUE}, 
                {new Float( 42 ), ">=", new Float( 42 ), Boolean.TRUE},
                {new Float( 42 ), ">=", new Float( 100 ), Boolean.FALSE}, 
                {new Float( 42 ), "<", new Float( 1 ), Boolean.FALSE}, 
                {new Float( 42 ), "==", null, Boolean.FALSE}, 
                {new Float( 42 ), "!=", null, Boolean.TRUE}, 
                {null, "==", null, Boolean.TRUE}, 
                {null, "==", new Float( 42 ), Boolean.FALSE},
                {null, "!=", null, Boolean.FALSE}, 
                {null, "!=", new Float( 42 ), Boolean.TRUE}};

        runEvaluatorTest( data,
                          ValueType.FLOAT_TYPE );
    }

    public void testLong() {
        final Object[][] data = {
                {new Long( 42 ), "==", new Long( 42 ), Boolean.TRUE}, 
                {new Long( 42 ), "<", new Long( 43 ), Boolean.TRUE}, 
                {new Long( 42 ), ">=", new Long( 41 ), Boolean.TRUE}, 
                {new Long( 42 ), "!=", new Long( 41 ), Boolean.TRUE},
                {new Long( 42 ), ">", new Long( 41 ), Boolean.TRUE}, 
                {new Long( 42 ), ">=", new Long( 41 ), Boolean.TRUE}, 
                {new Long( 42 ), ">=", new Long( 42 ), Boolean.TRUE}, 
                {new Long( 42 ), ">=", new Long( 100 ), Boolean.FALSE},
                {new Long( 42 ), "<", new Long( 1 ), Boolean.FALSE},
                {new Long( 42 ), "==", null, Boolean.FALSE}, 
                {new Long( 42 ), "!=", null, Boolean.TRUE}, 
                {null, "==", null, Boolean.TRUE}, 
                {null, "==", new Long( 42 ), Boolean.FALSE},
                {null, "!=", null, Boolean.FALSE}, 
                {null, "!=", new Long( 42 ), Boolean.TRUE}};

        runEvaluatorTest( data,
                          ValueType.LONG_TYPE );
    }

    public void testCharacter() {
        final Object[][] data = {
                {new Character( 'a' ), "==", new Character( 'a' ), Boolean.TRUE}, 
                {new Character( 'a' ), "<", new Character( 'b' ), Boolean.TRUE}, 
                {new Character( 'a' ), ">=", new Character( 'a' ), Boolean.TRUE},
                {new Character( 'a' ), "!=", new Character( 'Z' ), Boolean.TRUE}, 
                {new Character( 'z' ), ">", new Character( 'a' ), Boolean.TRUE}, 
                {new Character( 'z' ), ">=", new Character( 'z' ), Boolean.TRUE},
                {new Character( 'z' ), ">=", new Character( 'a' ), Boolean.TRUE}, 
                {new Character( 'a' ), ">=", new Character( 'z' ), Boolean.FALSE}, 
                {new Character( 'z' ), "<", new Character( 'a' ), Boolean.FALSE},
                {new Character( 'z' ), "==", null, Boolean.FALSE}, 
                {new Character( 'z' ), "!=", null, Boolean.TRUE}, 
                {null, "==", null, Boolean.TRUE}, 
                {null, "==", new Character( 'z' ), Boolean.FALSE},
                {null, "!=", null, Boolean.FALSE}, 
                {null, "!=", new Character( 'z' ), Boolean.TRUE}};
        runEvaluatorTest( data,
                          ValueType.CHAR_TYPE );
    }

    public void testDate() throws Exception {

        final SimpleDateFormat df = new SimpleDateFormat( "dd-MMM-yyyy",
                                                          Locale.ENGLISH );

        //note that strings are also allowed on the right
        final Object[][] data = {
                {df.parse( "10-Jul-1974" ), "==", df.parse( "10-Jul-1974" ), Boolean.TRUE}, 
                {df.parse( "10-Jul-1974" ), "<", df.parse( "11-Jul-1974" ), Boolean.TRUE},
                {df.parse( "10-Jul-1974" ), ">=", df.parse( "10-Jul-1974" ), Boolean.TRUE}, 
                {df.parse( "10-Jul-1974" ), "!=", df.parse( "11-Jul-1974" ), Boolean.TRUE}, 
                {df.parse( "10-Jul-2000" ), ">", df.parse( "10-Jul-1974" ), Boolean.TRUE},
                {df.parse( "10-Jul-1974" ), ">=", df.parse( "10-Jul-1974" ), Boolean.TRUE}, 
                {df.parse( "11-Jul-1974" ), ">=", df.parse( "10-Jul-1974" ), Boolean.TRUE}, 
                {df.parse( "10-Jul-1974" ), ">=", df.parse( "11-Jul-1974" ), Boolean.FALSE},
                {df.parse( "10-Jul-2000" ), "<", df.parse( "10-Jul-1974" ), Boolean.FALSE}, 
                {df.parse( "10-Jul-1974" ), "<", "11-Jul-1974", Boolean.TRUE}, 
                {df.parse( "10-Jul-1974" ), ">=", "10-Jul-1974", Boolean.TRUE},
                {df.parse( "10-Jul-1974" ), "!=", "11-Jul-1974", Boolean.TRUE}, 
                {df.parse( "10-Jul-2000" ), ">", "10-Jul-1974", Boolean.TRUE}, 
                {df.parse( "10-Jul-1974" ), ">=", "10-Jul-1974", Boolean.TRUE},
                {df.parse( "11-Jul-1974" ), ">=", "10-Jul-1974", Boolean.TRUE}, 
                {df.parse( "10-Jul-1974" ), ">=", "11-Jul-1974", Boolean.FALSE}, 
                {df.parse( "10-Jul-1974" ), "==", null, Boolean.FALSE}, 
                {df.parse( "10-Jul-1974" ), "!=", null, Boolean.TRUE}, 
                {null, "==", null, Boolean.TRUE}, 
                {null, "==", df.parse( "10-Jul-1974" ), Boolean.FALSE},
                {null, "!=", null, Boolean.FALSE}, 
                {null, "!=", df.parse( "10-Jul-1974" ), Boolean.TRUE}};
        runEvaluatorTest( data,
                          ValueType.DATE_TYPE );
    }

    public void testByte() {
        final Object[][] data = {
                {new Byte( "1" ), "==", new Byte( "1" ), Boolean.TRUE}, 
                {new Byte( "1" ), "==", new Byte( "2" ), Boolean.FALSE}, 
                {new Byte( "1" ), "!=", new Byte( "2" ), Boolean.TRUE},
                {new Byte( "1" ), "!=", new Byte( "1" ), Boolean.FALSE}, 
                {new Byte( "1" ), "<=", new Byte( "1" ), Boolean.TRUE}, 
                {new Byte( "1" ), "==", null, Boolean.FALSE}, 
                {new Byte( "1" ), "!=", null, Boolean.TRUE}, 
                {null, "==", null, Boolean.TRUE}, 
                {null, "==", new Byte( "1" ), Boolean.FALSE},
                {null, "!=", null, Boolean.FALSE}, 
                {null, "!=", new Byte( "1" ), Boolean.TRUE}};
        runEvaluatorTest( data,
                          ValueType.BYTE_TYPE );

    }

    /**
     * Test utility to play the data through the evaluators.
     * @param data The data to try out : Array of {arg1, operator, arg2}
     * @param valueType The Evaluator.**_TYPE to test
     */
    private void runEvaluatorTest(final Object[][] data,
                                  final ValueType valueType) {
        for ( int i = 0; i < data.length; i++ ) {
            final Object[] row = data[i];
            final Evaluator evaluator = valueType.getEvaluator( Operator.determineOperator( (String) row[1] ) );
            final boolean result = evaluator.evaluate( row[0],
                                                       row[2] );
            final String message = "The evaluator type: [" + valueType + "] incorrectly returned " + result + " for [" + row[0] + " " + row[1] + " " + row[2] + "]. It was asserted to return " + row[3];

            if ( row[3] == Boolean.TRUE ) {
                assertTrue( message,
                            result );
            } else {
                assertFalse( message,
                             result );
            }

            assertEquals( valueType,
                          evaluator.getValueType() );

        }
    }

    //    public void testRegexFoo() {
    //        Pattern p = Pattern.compile( ".*foo" );
    //        boolean b;
    //        long start = System.currentTimeMillis();
    //        for (int i = 0; i < 1000000; i++) {
    //            b = ("something foo".matches( ".*foo" ));
    //        }
    //        System.out.println("time: " + (System.currentTimeMillis() - start));
    //        
    //        start = System.currentTimeMillis();
    //        for (int i = 0; i < 1000000; i++) {        
    //            Matcher m = p.matcher( "something foo" );
    //            b = m.matches();
    //        }
    //        System.out.println("time: " + (System.currentTimeMillis() - start));
    //    }

}