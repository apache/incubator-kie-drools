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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import junit.framework.TestCase;

import org.drools.base.evaluators.Operator;
import org.drools.rule.Declaration;
import org.drools.rule.VariableRestriction.BooleanVariableContextEntry;
import org.drools.rule.VariableRestriction.DoubleVariableContextEntry;
import org.drools.rule.VariableRestriction.LongVariableContextEntry;
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;

/**
 * Some test coverage goodness for the evaluators.
 * Evaluator concrete instances are inside the factory at this time.
 * @author Michael Neale
 */
public class EvaluatorFactoryTest extends TestCase {

    public void testObject() {

        final List list = new ArrayList();
        list.add( "foo" );

        final Object[][] data = {{"foo", "==", "bar", Boolean.FALSE}, {"foo", "==", "foo", Boolean.TRUE}, {"foo", "!=", "bar", Boolean.TRUE}, {list, "contains", "foo", Boolean.TRUE}, {list, "contains", "bar", Boolean.FALSE},
                {list, "==", null, Boolean.FALSE}, {list, "!=", null, Boolean.TRUE}, {null, "==", null, Boolean.TRUE}, {null, "==", list, Boolean.FALSE}, {null, "!=", list, Boolean.TRUE},
                {new BigDecimal( "42.42" ), "<", new BigDecimal( "43" ), Boolean.TRUE}, {new BigDecimal( "42.42" ), ">", new BigDecimal( "43" ), Boolean.FALSE}, {new BigDecimal( "42.42" ), "<=", new BigDecimal( "42.42" ), Boolean.TRUE},
                {new BigInteger( "42" ), ">=", new BigInteger( "43" ), Boolean.FALSE}, {new BigInteger( "42" ), ">=", new BigInteger( "43" ), Boolean.FALSE}, {list, "excludes", "baz", Boolean.TRUE}, {list, "excludes", "foo", Boolean.FALSE}

        };

        runEvaluatorTest( data,
                          ValueType.OBJECT_TYPE );

    }

    public void testArrayType() {

        final Object[] field = new Object[]{"foo", "bar"};

        final Object[][] data = {{field, "==", new Object[]{"foo"}, Boolean.FALSE}, {field, "==", field, Boolean.TRUE}, {field, "!=", new Object[]{"foo"}, Boolean.TRUE}, {field, "contains", "foo", Boolean.TRUE}, {field, "!=", null, Boolean.TRUE},
                {field, "==", null, Boolean.FALSE}, {null, "==", field, Boolean.FALSE}, {null, "==", null, Boolean.TRUE}, {null, "!=", field, Boolean.TRUE}, {null, "!=", null, Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.ARRAY_TYPE );

    }

    public void testString() {

        final Object[][] data = {{"foo", "==", "bar", Boolean.FALSE}, {"foo", "==", "foo", Boolean.TRUE}, {"foo", "!=", "bar", Boolean.TRUE}, {"something foo", "matches", ".*foo", Boolean.TRUE}, {"foo", "matches", ".*foo", Boolean.TRUE},
                {"foo", "matches", "bar", Boolean.FALSE}, {null, "matches", ".*foo", Boolean.FALSE}, {"foo", "==", null, Boolean.FALSE}, {"foo", "!=", null, Boolean.TRUE}, {null, "==", null, Boolean.TRUE}, {"foo", "!=", null, Boolean.TRUE},
                {null, "!=", "foo", Boolean.TRUE}, {null, "!=", null, Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.STRING_TYPE );

    }

    public void testInteger() {

        final Object[][] data = {{new Integer( 42 ), "==", new Integer( 42 ), Boolean.TRUE}, {new Integer( 42 ), "<", new Integer( 43 ), Boolean.TRUE}, {new Integer( 42 ), ">=", new Integer( 41 ), Boolean.TRUE},
                {new Integer( 42 ), "!=", new Integer( 41 ), Boolean.TRUE}, {new Integer( 42 ), ">", new Integer( 41 ), Boolean.TRUE}, {new Integer( 42 ), "<=", new Integer( 42 ), Boolean.TRUE},
                {new Integer( 42 ), ">", new Integer( 100 ), Boolean.FALSE}, {new Integer( 42 ), "!=", null, Boolean.TRUE}, {new Integer( 42 ), "==", null, Boolean.FALSE}, {null, "==", null, Boolean.TRUE}, 
                {null, "!=", null, Boolean.FALSE}, {null, "!=", new Integer( 42 ), Boolean.TRUE}, {null, "==", new Integer( 42 ), Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.PINTEGER_TYPE );

    }

    public void testBigDecimal() {

        final ValueType type = ValueType.determineValueType( BigDecimal.class );
        assertSame( type,
                    ValueType.BIG_DECIMAL_TYPE );

        final Object[][] data = {{new BigDecimal( 42 ), "==", new BigDecimal( 42 ), Boolean.TRUE}, {new BigDecimal( 42 ), "<", new BigDecimal( 43 ), Boolean.TRUE}, {new BigDecimal( 42 ), ">=", new BigDecimal( 41 ), Boolean.TRUE},
                {new BigDecimal( 42 ), "!=", new BigDecimal( 41 ), Boolean.TRUE}, {new BigDecimal( 42 ), ">", new BigDecimal( 41 ), Boolean.TRUE}, {new BigDecimal( 42 ), "<=", new BigDecimal( 42 ), Boolean.TRUE},
                {new BigDecimal( 42 ), ">", new BigDecimal( 100 ), Boolean.FALSE}, {new BigDecimal( 42 ), "==", null, Boolean.FALSE}, {new BigDecimal( 42 ), "!=", null, Boolean.TRUE}, {null, "==", new BigDecimal( 42 ), Boolean.FALSE},
                {null, "!=", new BigDecimal( 42 ), Boolean.TRUE}};

        runEvaluatorTest( data,
                          ValueType.BIG_DECIMAL_TYPE );

    }

    public void testBigInteger() {

        final ValueType type = ValueType.determineValueType( BigInteger.class );
        assertSame( type,
                    ValueType.BIG_INTEGER_TYPE );

        final Object[][] data = {{new BigInteger( "42" ), "==", new BigInteger( "42" ), Boolean.TRUE}, {new BigInteger( "42" ), "<", new BigInteger( "43" ), Boolean.TRUE}, {new BigInteger( "42" ), ">=", new BigInteger( "41" ), Boolean.TRUE},
                {new BigInteger( "42" ), "!=", new BigInteger( "41" ), Boolean.TRUE}, {new BigInteger( "42" ), ">", new BigInteger( "41" ), Boolean.TRUE}, {new BigInteger( "42" ), "<=", new BigInteger( "42" ), Boolean.TRUE},
                {new BigInteger( "42" ), ">", new BigInteger( "100" ), Boolean.FALSE}, {new BigInteger( "42" ), "==", null, Boolean.FALSE}, {new BigInteger( "42" ), "!=", null, Boolean.TRUE}, {null, "==", new BigInteger( "42" ), Boolean.FALSE},
                {null, "!=", new BigInteger( "42" ), Boolean.TRUE}};

        runEvaluatorTest( data,
                          ValueType.BIG_INTEGER_TYPE );

    }

    public void testShort() {

        //Test data: Obj1, Operand, Obj2
        final Object[][] data = {{new Short( (short) 42 ), "==", new Short( (short) 42 ), Boolean.TRUE}, {new Short( (short) 42 ), "<", new Short( (short) 43 ), Boolean.TRUE}, {new Short( (short) 42 ), ">=", new Short( (short) 41 ), Boolean.TRUE},
                {new Short( (short) 42 ), "!=", new Short( (short) 41 ), Boolean.TRUE}, {new Short( (short) 42 ), "==", null, Boolean.FALSE}, {null, "==", null, Boolean.TRUE}, {null, "!=", null, Boolean.FALSE}, 
                {null, "!=", new Short( (short) 42 ), Boolean.TRUE}, {null, "==", new Short( (short) 42 ), Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.PSHORT_TYPE );
    }

    public void testBoolean() {

        //Test data: Obj1, Operand, Obj2
        final Object[][] data = {{new Boolean( true ), "==", new Boolean( true ), Boolean.TRUE}, {new Boolean( false ), "!=", new Boolean( true ), Boolean.TRUE}, {new Boolean( true ), "==", new Boolean( false ), Boolean.FALSE},
                {new Boolean( true ), "!=", new Boolean( false ), Boolean.TRUE}, {new Boolean( true ), "==", null, Boolean.FALSE}, {null, "==", null, Boolean.TRUE}, {null, "!=", null, Boolean.FALSE}, 
                {null, "!=", new Boolean( true ), Boolean.TRUE}, {null, "==", new Boolean( true ), Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.PBOOLEAN_TYPE );
    }

    public void testDouble() {
        final Object[][] data = {{new Double( 42 ), "==", new Double( 42 ), Boolean.TRUE}, {new Double( 42 ), "<", new Double( 43 ), Boolean.TRUE}, {new Double( 42 ), ">=", new Double( 41 ), Boolean.TRUE},
                {new Double( 42 ), "!=", new Double( 41 ), Boolean.TRUE}, {new Double( 42 ), ">", new Double( 41 ), Boolean.TRUE}, {new Double( 42 ), ">=", new Double( 41 ), Boolean.TRUE}, {new Double( 42 ), ">=", new Double( 42 ), Boolean.TRUE},
                {new Double( 42 ), ">=", new Double( 100 ), Boolean.FALSE}, {new Double( 42 ), "<", new Double( 1 ), Boolean.FALSE}, {new Double( 42 ), "==", null, Boolean.FALSE}, {null, "==", null, Boolean.TRUE}, {null, "!=", null, Boolean.FALSE}, 
                {null, "!=", new Double( 42 ), Boolean.TRUE}, {null, "==", new Double( 42 ), Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.PDOUBLE_TYPE );
    }

    public void testFloat() {
        final Object[][] data = {{new Float( 42 ), "==", new Float( 42 ), Boolean.TRUE}, {new Float( 42 ), "<", new Float( 43 ), Boolean.TRUE}, {new Float( 42 ), ">=", new Float( 41 ), Boolean.TRUE},
                {new Float( 42 ), "!=", new Float( 41 ), Boolean.TRUE}, {new Float( 42 ), ">", new Float( 41 ), Boolean.TRUE}, {new Float( 42 ), ">=", new Float( 41 ), Boolean.TRUE}, {new Float( 42 ), ">=", new Float( 42 ), Boolean.TRUE},
                {new Float( 42 ), ">=", new Float( 100 ), Boolean.FALSE}, {new Float( 42 ), "<", new Float( 1 ), Boolean.FALSE}, {new Float( 42 ), "==", null, Boolean.FALSE}, {null, "==", null, Boolean.TRUE}, {null, "!=", null, Boolean.FALSE}, 
                {null, "!=", new Float( 42 ), Boolean.TRUE}, {null, "==", new Float( 42 ), Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.PFLOAT_TYPE );
    }

    public void testLong() {
        final Object[][] data = {{new Long( 42 ), "==", new Long( 42 ), Boolean.TRUE}, {new Long( 42 ), "<", new Long( 43 ), Boolean.TRUE}, {new Long( 42 ), ">=", new Long( 41 ), Boolean.TRUE}, {new Long( 42 ), "!=", new Long( 41 ), Boolean.TRUE},
                {new Long( 42 ), ">", new Long( 41 ), Boolean.TRUE}, {new Long( 42 ), ">=", new Long( 41 ), Boolean.TRUE}, {new Long( 42 ), ">=", new Long( 42 ), Boolean.TRUE}, {new Long( 42 ), ">=", new Long( 100 ), Boolean.FALSE},
                {new Long( 42 ), "<", new Long( 1 ), Boolean.FALSE}, {new Long( 42 ), "==", null, Boolean.FALSE}, {null, "==", null, Boolean.TRUE}, {null, "!=", null, Boolean.FALSE}, {null, "!=", new Long( 42 ), Boolean.TRUE}, 
                {null, "==", new Long( 42 ), Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.PLONG_TYPE );
    }

    public void testCharacter() {
        final Object[][] data = {{new Character( 'a' ), "==", new Character( 'a' ), Boolean.TRUE}, {new Character( 'a' ), "<", new Character( 'b' ), Boolean.TRUE}, {new Character( 'a' ), ">=", new Character( 'a' ), Boolean.TRUE},
                {new Character( 'a' ), "!=", new Character( 'Z' ), Boolean.TRUE}, {new Character( 'z' ), ">", new Character( 'a' ), Boolean.TRUE}, {new Character( 'z' ), ">=", new Character( 'z' ), Boolean.TRUE},
                {new Character( 'z' ), ">=", new Character( 'a' ), Boolean.TRUE}, {new Character( 'a' ), ">=", new Character( 'z' ), Boolean.FALSE}, {new Character( 'z' ), "<", new Character( 'a' ), Boolean.FALSE},
                {new Character( 'z' ), "==", null, Boolean.FALSE}, {null, "==", null, Boolean.TRUE}, {null, "!=", null, Boolean.FALSE}, {null, "!=", new Character( 'z' ), Boolean.TRUE}, {null, "==", new Character( 'z' ), Boolean.FALSE}};
        runEvaluatorTest( data,
                          ValueType.PCHAR_TYPE );
    }

    public void testDate() throws Exception {

        final SimpleDateFormat df = new SimpleDateFormat( "dd-MMM-yyyy",
                                                          Locale.ENGLISH );

        //note that strings are also allowed on the right
        final Object[][] data = {{df.parse( "10-Jul-1974" ), "==", df.parse( "10-Jul-1974" ), Boolean.TRUE}, {df.parse( "10-Jul-1974" ), "<", df.parse( "11-Jul-1974" ), Boolean.TRUE},
                {df.parse( "10-Jul-1974" ), ">=", df.parse( "10-Jul-1974" ), Boolean.TRUE}, {df.parse( "10-Jul-1974" ), "!=", df.parse( "11-Jul-1974" ), Boolean.TRUE}, {df.parse( "10-Jul-2000" ), ">", df.parse( "10-Jul-1974" ), Boolean.TRUE},
                {df.parse( "10-Jul-1974" ), ">=", df.parse( "10-Jul-1974" ), Boolean.TRUE}, {df.parse( "11-Jul-1974" ), ">=", df.parse( "10-Jul-1974" ), Boolean.TRUE}, {df.parse( "10-Jul-1974" ), ">=", df.parse( "11-Jul-1974" ), Boolean.FALSE},
                {df.parse( "10-Jul-2000" ), "<", df.parse( "10-Jul-1974" ), Boolean.FALSE}, {df.parse( "10-Jul-1974" ), "<", df.parse( "11-Jul-1974" ), Boolean.TRUE}, {df.parse( "10-Jul-1974" ), "==", null, Boolean.FALSE},
                {df.parse( "10-Jul-1974" ), "!=", null, Boolean.TRUE}, {null, "==", null, Boolean.TRUE}, {null, "==", df.parse( "10-Jul-1974" ), Boolean.FALSE}, {null, "!=", null, Boolean.FALSE}, {null, "!=", df.parse( "10-Jul-1974" ), Boolean.TRUE}};
        runEvaluatorTest( data,
                          ValueType.DATE_TYPE );
    }

    public void testByte() {
        final Object[][] data = {{new Byte( "1" ), "==", new Byte( "1" ), Boolean.TRUE}, {new Byte( "1" ), "==", new Byte( "2" ), Boolean.FALSE}, {new Byte( "1" ), "!=", new Byte( "2" ), Boolean.TRUE},
                {new Byte( "1" ), "!=", new Byte( "1" ), Boolean.FALSE}, {new Byte( "1" ), "<=", new Byte( "1" ), Boolean.TRUE}, {new Byte( "1" ), "==", null, Boolean.FALSE}, {null, "==", null, Boolean.TRUE}, 
                {null, "!=", null, Boolean.FALSE}, {null, "!=", new Byte( "1" ), Boolean.TRUE}, {null, "==", new Byte( "1" ), Boolean.FALSE}};
        runEvaluatorTest( data,
                          ValueType.PBYTE_TYPE );

    }

    /**
     * Test utility to play the data through the evaluators.
     * @param data The data to try out : Array of {arg1, operator, arg2}
     * @param valueType The Evaluator.**_TYPE to test
     */
    private void runEvaluatorTest(final Object[][] data,
                                  final ValueType valueType) {
        final Extractor extractor = new MockExtractor();
        for ( int i = 0; i < data.length; i++ ) {
            final Object[] row = data[i];
            final Evaluator evaluator = valueType.getEvaluator( Operator.determineOperator( (String) row[1] ) );
            checkEvaluatorMethodWithFieldValue( valueType,
                                                extractor,
                                                row,
                                                evaluator );
            checkEvaluatorMethodCachedRight( valueType,
                                             extractor,
                                             row,
                                             evaluator );
            checkEvaluatorMethodCachedLeft( valueType,
                                            extractor,
                                            row,
                                            evaluator );
            checkEvaluatorMethodWith2Extractors( valueType,
                                                 extractor,
                                                 row,
                                                 evaluator );

            assertEquals( valueType,
                          evaluator.getValueType() );

        }
    }

    /**
     * @param valueType
     * @param extractor
     * @param row
     * @param evaluator
     */
    private void checkEvaluatorMethodWithFieldValue(final ValueType valueType,
                                                    final Extractor extractor,
                                                    final Object[] row,
                                                    final Evaluator evaluator) {
        final FieldValue value = FieldFactory.getFieldValue( row[2],
                                                       valueType );
        final boolean result = evaluator.evaluate( extractor,
                                                   row[0],
                                                   value );
        final String message = "The evaluator type: [" + valueType + "] with FieldValue incorrectly returned " + result + " for [" + row[0] + " " + row[1] + " " + row[2] + "]. It was asserted to return " + row[3];

        if ( row[3] == Boolean.TRUE ) {
            assertTrue( message,
                        result );
        } else {
            assertFalse( message,
                         result );
        }
    }

    /**
     * @param valueType
     * @param extractor
     * @param row
     * @param evaluator
     */
    private void checkEvaluatorMethodCachedRight(final ValueType valueType,
                                                 final Extractor extractor,
                                                 final Object[] row,
                                                 final Evaluator evaluator) {
        final VariableContextEntry context = this.getContextEntry( (FieldExtractor) extractor,
                                                             valueType,
                                                             row );
        final boolean result = evaluator.evaluateCachedRight( context,
                                                              row[2] );
        final String message = "The evaluator type: [" + valueType + "] with CachedRight incorrectly returned " + result + " for [" + row[0] + " " + row[1] + " " + row[2] + "]. It was asserted to return " + row[3];

        if ( row[3] == Boolean.TRUE ) {
            assertTrue( message,
                        result );
        } else {
            assertFalse( message,
                         result );
        }
    }

    /**
     * @param valueType
     * @param extractor
     * @param row
     * @param evaluator
     */
    private void checkEvaluatorMethodCachedLeft(final ValueType valueType,
                                                final Extractor extractor,
                                                final Object[] row,
                                                final Evaluator evaluator) {
        final VariableContextEntry context = this.getContextEntry( (FieldExtractor) extractor,
                                                             valueType,
                                                             row );
        final boolean result = evaluator.evaluateCachedLeft( context,
                                                             row[0] );
        final String message = "The evaluator type: [" + valueType + "] with CachedLeft incorrectly returned " + result + " for [" + row[0] + " " + row[1] + " " + row[2] + "]. It was asserted to return " + row[3];

        if ( row[3] == Boolean.TRUE ) {
            assertTrue( message,
                        result );
        } else {
            assertFalse( message,
                         result );
        }
    }

    /**
     * @param valueType
     * @param extractor
     * @param row
     * @param evaluator
     */
    private void checkEvaluatorMethodWith2Extractors(final ValueType valueType,
                                                     final Extractor extractor,
                                                     final Object[] row,
                                                     final Evaluator evaluator) {
        final boolean result = evaluator.evaluate( extractor,
                                                   row[0],
                                                   extractor,
                                                   row[2] );
        final String message = "The evaluator type: [" + valueType + "] with 2 extractors incorrectly returned " + result + " for [" + row[0] + " " + row[1] + " " + row[2] + "]. It was asserted to return " + row[3];

        if ( row[3] == Boolean.TRUE ) {
            assertTrue( message,
                        result );
        } else {
            assertFalse( message,
                         result );
        }
    }

    private VariableContextEntry getContextEntry(final FieldExtractor extractor,
                                                 final ValueType valueType,
                                                 final Object[] row) {
        final Declaration declaration = new Declaration( "test",
                                                   extractor,
                                                   null );
        if ( valueType.isIntegerNumber() || valueType.isChar() ) {
            final LongVariableContextEntry context = new LongVariableContextEntry( extractor,
                                                                             declaration );
            
            if ( valueType.isChar() ) {
                if (row[2] == null) {
                    context.leftNull = true;
                } else {
                    context.left = ((Character) row[2]).charValue();
                }
                
                if (row[0] == null) {
                    context.rightNull = true;
                } else {
                    context.right = ((Character) row[0]).charValue();
                }
            } else {
                if (row[2] == null) {
                    context.leftNull = true;
                } else {
                    context.left = ((Number) row[2]).longValue();
                }
                
                if (row[0] == null) {
                    context.rightNull = true;
                } else {
                    context.right = ((Number) row[0]).longValue();
                }
            }
            return context;
        } else if ( valueType.isBoolean() ) {
            final BooleanVariableContextEntry context = new BooleanVariableContextEntry( extractor,
                                                                                   declaration );
            
            if (row[2] == null) {
                context.leftNull = true;
            } else {
                context.left = ((Boolean) row[2]).booleanValue();
            }
            
            if (row[0] == null) {
                context.rightNull = true;
            } else {
                context.right = ((Boolean) row[0]).booleanValue();
            }
            return context;
        } else if ( valueType.isFloatNumber() ) {
            final DoubleVariableContextEntry context = new DoubleVariableContextEntry( extractor,
                                                                                 declaration );
            if (row[2] == null) {
                context.leftNull = true;
            } else {
                context.left = ((Number) row[2]).doubleValue();
            }
            
            if (row[0] == null) {
                context.rightNull = true;
            } else {
                context.right = ((Number) row[0]).doubleValue();
            }
            return context;
        } else {
            final ObjectVariableContextEntry context = new ObjectVariableContextEntry( extractor,
                                                                                 declaration );
            if (row[2] == null) {
                context.leftNull = true;
            } else {
                context.left = row[2];
            }
            
            if (row[0] == null) {
                context.rightNull = true;
            } else {
                context.right = row[0];
            }
            return context;
        }
    }

    private static class MockExtractor
        implements
        FieldExtractor {

        private static final long serialVersionUID = 2759666130893301563L;

        public boolean getBooleanValue(final Object object) {
            return ((Boolean) object).booleanValue();
        }

        public byte getByteValue(final Object object) {
            return ((Number) object).byteValue();
        }

        public char getCharValue(final Object object) {
            return ((Character) object).charValue();
        }

        public double getDoubleValue(final Object object) {
            return ((Number) object).doubleValue();
        }

        public Class getExtractToClass() {
            return null;
        }

        public float getFloatValue(final Object object) {
            return ((Number) object).floatValue();
        }

        public int getHashCode(final Object object) {
            return 0;
        }

        public int getIntValue(final Object object) {
            return ((Number) object).intValue();
        }

        public long getLongValue(final Object object) {
            return ((Number) object).longValue();
        }

        public Method getNativeReadMethod() {
            return null;
        }

        public short getShortValue(final Object object) {
            return ((Number) object).shortValue();
        }

        public Object getValue(final Object object) {
            return object;
        }
        
        public boolean isNullValue(final Object object) {
            return object == null;
        }

        public ValueType getValueType() {
            // TODO Auto-generated method stub
            return null;
        }

        public int getIndex() {
            return 0;
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