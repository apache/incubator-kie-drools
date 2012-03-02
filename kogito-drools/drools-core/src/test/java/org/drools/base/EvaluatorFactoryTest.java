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

package org.drools.base;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.base.evaluators.EvaluatorRegistry;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.MathUtils;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.rule.Declaration;
import org.drools.rule.VariableRestriction.BooleanVariableContextEntry;
import org.drools.rule.VariableRestriction.CharVariableContextEntry;
import org.drools.rule.VariableRestriction.DoubleVariableContextEntry;
import org.drools.rule.VariableRestriction.LongVariableContextEntry;
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;

/**
 * Some test coverage goodness for the evaluators.
 * Evaluator concrete instances are inside the factory at this time.
 */
public class EvaluatorFactoryTest {

    private EvaluatorRegistry registry = new EvaluatorRegistry();
    
    @Test
    public void testObject() {

        final List list = new ArrayList();
        list.add( "foo" );
        list.add( null );

        Collection col = Arrays.asList( new String[]{"foo", "bar", null} );

        final Object[][] data = {{"foo", "==", "bar", Boolean.FALSE}, {"foo", "==", "foo", Boolean.TRUE}, {"foo", "!=", "bar", Boolean.TRUE}, {list, "contains", "foo", Boolean.TRUE}, {list, "contains", "bar", Boolean.FALSE},
                {list, "contains", null, Boolean.TRUE}, {null, "contains", "bar", Boolean.FALSE}, {null, "contains", null, Boolean.FALSE}, {list, "==", null, Boolean.FALSE}, {list, "!=", null, Boolean.TRUE}, {null, "==", null, Boolean.TRUE},
                {null, "==", list, Boolean.FALSE}, {null, "!=", list, Boolean.TRUE}, {null, "<", new Integer( 43 ), Boolean.FALSE}, {null, ">=", new Integer( -10 ), Boolean.FALSE}, {null, ">", new Integer( -10 ), Boolean.FALSE},
                {null, "<=", new Integer( 42 ), Boolean.FALSE}, {new BigDecimal( "42.42" ), "<", new BigDecimal( "43" ), Boolean.TRUE}, {new BigDecimal( "42.42" ), ">", new BigDecimal( "43" ), Boolean.FALSE},
                {new BigDecimal( "42.42" ), "<=", new BigDecimal( "42.42" ), Boolean.TRUE}, {new BigInteger( "42" ), ">=", new BigInteger( "43" ), Boolean.FALSE}, {new BigInteger( "42" ), ">=", new BigInteger( "43" ), Boolean.FALSE},
                {list, "excludes", "baz", Boolean.TRUE}, {list, "excludes", "foo", Boolean.FALSE}, {"foo", "memberOf", col, Boolean.TRUE}, {"xyz", "memberOf", col, Boolean.FALSE}, {null, "memberOf", col, Boolean.TRUE},
                {"foo", "memberOf", null, Boolean.FALSE}, {"foo", "not memberOf", col, Boolean.FALSE}, {"xyz", "not memberOf", col, Boolean.TRUE}, {null, "not memberOf", col, Boolean.FALSE}, {"foo", "not memberOf", null, Boolean.FALSE},
                {Boolean.TRUE, "==", "xyz", Boolean.FALSE}, {Boolean.TRUE, "==", "true", Boolean.TRUE}, {Boolean.FALSE, "==", "xyz", Boolean.TRUE}, {Boolean.FALSE, "==", "false", Boolean.TRUE}, {Boolean.FALSE, "==", "true", Boolean.FALSE},
                {Boolean.TRUE, "!=", "xyz", Boolean.TRUE}, {Boolean.TRUE, "!=", "true", Boolean.FALSE}, {Boolean.FALSE, "!=", "xyz", Boolean.FALSE}, {Boolean.FALSE, "!=", "true", Boolean.TRUE}, {Boolean.FALSE, "!=", "false", Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.OBJECT_TYPE );

    }

    @Test
    public void testArrayType() {

        final Object[] field = new Object[]{"foo", "bar"};

        final Object[][] data = {{field, "==", new Object[]{"foo"}, Boolean.FALSE}, {field, "==", field, Boolean.TRUE}, {field, "!=", new Object[]{"foo"}, Boolean.TRUE}, /*{field, "contains", "foo", Boolean.TRUE},   */

                {field, "!=", null, Boolean.TRUE}, {field, "==", null, Boolean.FALSE}, {null, "==", field, Boolean.FALSE}, {null, "==", null, Boolean.TRUE},
                {null, "!=", field, Boolean.TRUE}, {null, "!=", null, Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.ARRAY_TYPE );

    }

    @Test
    public void testString() {

        Collection<String> col = Arrays.asList( new String[]{"foo", "bar", null} );

        final Object[][] data = {{"foo", "==", "bar", Boolean.FALSE}, {"foo", "==", "foo", Boolean.TRUE}, {"foo", "!=", "bar", Boolean.TRUE}, {"something foo", "matches", ".*foo", Boolean.TRUE}, {"foo", "matches", ".*foo", Boolean.TRUE},
                {"foo", "matches", "bar", Boolean.FALSE}, {null, "matches", ".*foo", Boolean.FALSE}, {"something", "matches", "something", Boolean.TRUE}, {"something", "matches", "hello ;=", Boolean.FALSE},
                {"something", "not matches", "something", Boolean.FALSE}, {"something", "not matches", "hello ;=", Boolean.TRUE}, {"foo", "==", null, Boolean.FALSE}, {"foo", "!=", null, Boolean.TRUE}, {null, "==", null, Boolean.TRUE},
                {"foo", "!=", null, Boolean.TRUE}, {null, "!=", "foo", Boolean.TRUE}, {null, "!=", null, Boolean.FALSE}, {"foo", "memberOf", col, Boolean.TRUE}, {"xyz", "memberOf", col, Boolean.FALSE}, {null, "memberOf", col, Boolean.TRUE},
                {"foo", "memberOf", null, Boolean.FALSE}, {"foo", "not memberOf", col, Boolean.FALSE}, {"xyz", "not memberOf", col, Boolean.TRUE}, {null, "not memberOf", col, Boolean.FALSE}, {"foo", "not memberOf", null, Boolean.FALSE},
                {"bar", "<", "foo", Boolean.TRUE}, {"foo", "<", "bar", Boolean.FALSE}, {"foo", "<=", "foo", Boolean.TRUE}, {"foo", "<=", "bar", Boolean.FALSE}, {"bar", ">", "foo", Boolean.FALSE}, {"foo", ">", "bar", Boolean.TRUE}, {"bar", ">=", "bar", Boolean.TRUE}, {"foo", ">=", "bar", Boolean.TRUE},
                {"foobar", "soundslike", "fubar", Boolean.TRUE}, {"fubar", "soundslike", "foobar", Boolean.TRUE}, {"foobar", "soundslike", "wanklerotaryengine", Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.STRING_TYPE );

    }

    @Test
    public void testInteger() {
        
        Collection col = new ArrayList();
        col.add( new Integer( 42 ) );
        col.add( new Integer( 45 ) );

        final Object[][] data = 
               {{new Integer( 42 ), "==", new Integer( 42 ), Boolean.TRUE}, 
                {new Integer( 42 ), "<", new Integer( 43 ), Boolean.TRUE}, 
                {new Integer( 42 ), ">=", new Integer( 41 ), Boolean.TRUE},
                {new Integer( 42 ), "!=", new Integer( 41 ), Boolean.TRUE}, 
                {new Integer( 42 ), ">", new Integer( 41 ), Boolean.TRUE}, 
                {new Integer( 42 ), "<=", new Integer( 42 ), Boolean.TRUE},
                {new Integer( 42 ), ">", new Integer( 100 ), Boolean.FALSE}, 
                {new Integer( 42 ), "!=", null, Boolean.TRUE}, 
                {new Integer( 42 ), "==", null, Boolean.FALSE}, 
                {new Integer( 42 ), ">", null, Boolean.FALSE}, 
                {new Integer( 42 ), ">=", null, Boolean.FALSE}, 
                {new Integer( -42 ), "<", null, Boolean.FALSE}, 
                {new Integer( -42 ), "<=", null, Boolean.FALSE}, 
                {null, ">", null, Boolean.FALSE}, 
                {null, ">=", null, Boolean.FALSE}, 
                {null, "<", null, Boolean.FALSE}, 
                {null, "<=", null, Boolean.FALSE}, 
                {null, "==", null, Boolean.TRUE}, 
                {null, "!=", null, Boolean.FALSE},
                {null, "!=", new Integer( 42 ), Boolean.TRUE}, 
                {null, "==", new Integer( 42 ), Boolean.FALSE}, 
                {null, "<", new Integer( 43 ), Boolean.FALSE}, 
                {null, ">=", new Integer( -10 ), Boolean.FALSE},
                {null, ">", new Integer( -10 ), Boolean.FALSE}, 
                {null, "<=", new Integer( 42 ), Boolean.FALSE}, 
                {new Integer( 42 ), "memberOf", col, Boolean.TRUE}, 
                {new Integer( 43 ), "memberOf", col, Boolean.FALSE},
                {null, "memberOf", col, Boolean.FALSE}, 
                {new Integer( 42 ), "memberOf", null, Boolean.FALSE}, 
                {new Integer( 42 ), "not memberOf", col, Boolean.FALSE}, 
                {new Integer( 43 ), "not memberOf", col, Boolean.TRUE},
                {null, "not memberOf", col, Boolean.TRUE}, 
                {new Integer( 42 ), "not memberOf", null, Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.PINTEGER_TYPE );

    }

    @Test
    public void testBigDecimal() {

        final ValueType type = ValueType.determineValueType( BigDecimal.class );
        assertSame( type,
                    ValueType.BIG_DECIMAL_TYPE );

        Collection col = new ArrayList();
        col.add( new BigDecimal( 42.0 ) );
        col.add( new BigDecimal( 45.0 ) );

        final Object[][] data = {
                {new BigDecimal( 42 ), "==", new BigDecimal( 42 ), Boolean.TRUE}, 
                {new BigDecimal( 42 ), "==", new BigDecimal( "42.0" ), Boolean.TRUE}, 
                {new BigDecimal( 42 ), "!=", new BigDecimal( "42.0" ), Boolean.FALSE}, 
                {new BigDecimal( 45 ), "==", new Double( 45.0 ), Boolean.TRUE}, 
                {new BigDecimal( 45 ), "!=", new Double( 45.0 ), Boolean.FALSE}, 
                {new BigDecimal( 42 ), "==", new BigInteger( "42" ), Boolean.TRUE}, 
                {new BigDecimal( 42 ), "!=", new BigInteger( "42" ), Boolean.FALSE}, 
                {new BigDecimal( 42 ), "<", new BigDecimal( 43 ), Boolean.TRUE}, 
                {new BigDecimal( 42 ), ">=", new BigDecimal( 41 ), Boolean.TRUE},
                {new BigDecimal( 42 ), ">=", new BigDecimal( "41.0" ), Boolean.TRUE},
                {new BigDecimal( 42 ), "!=", new BigDecimal( 41 ), Boolean.TRUE}, 
                {new BigDecimal( 42 ), ">", new BigDecimal( 41 ), Boolean.TRUE}, 
                {new BigDecimal( 42 ), "<=", new BigDecimal( 42 ), Boolean.TRUE},
                {new BigDecimal( 42 ), ">", new BigDecimal( 100 ), Boolean.FALSE}, 
                {new BigDecimal( 42 ), "<", new Double( 43 ), Boolean.TRUE}, 
                {new BigDecimal( 42 ), ">=", new Double( 41 ), Boolean.TRUE},
                {new BigDecimal( 42 ), ">", new Double( 41 ), Boolean.TRUE}, 
                {new BigDecimal( 42 ), "<=", new Double( 42 ), Boolean.TRUE},
                {new BigDecimal( 42 ), ">", new Double( 100 ), Boolean.FALSE}, 
                {new BigDecimal( 42 ), "<", new BigInteger( "43" ), Boolean.TRUE}, 
                {new BigDecimal( 42 ), ">=", new BigInteger( "41" ), Boolean.TRUE},
                {new BigDecimal( 42 ), ">", new BigInteger( "41" ), Boolean.TRUE}, 
                {new BigDecimal( 42 ), "<=", new BigInteger( "42" ), Boolean.TRUE},
                {new BigDecimal( 42 ), ">", new BigInteger( "100" ), Boolean.FALSE}, 
                {new BigDecimal( 42 ), "==", null, Boolean.FALSE}, 
                {new BigDecimal( 42 ), "!=", null, Boolean.TRUE}, 
                {null, "==", new BigDecimal( 42 ), Boolean.FALSE},
                {null, "!=", new BigDecimal( 42 ), Boolean.TRUE}, 
                {null, "<", new BigDecimal( 43 ), Boolean.FALSE}, 
                {null, ">=", new BigDecimal( -10 ), Boolean.FALSE}, 
                {null, ">", new BigDecimal( -10 ), Boolean.FALSE},
                {null, "<=", new BigDecimal( 42 ), Boolean.FALSE}, 
                {new BigDecimal( 42 ), "memberOf", col, Boolean.TRUE}, 
                {new BigDecimal( 43 ), "memberOf", col, Boolean.FALSE}, 
                {null, "memberOf", col, Boolean.FALSE},
                {new BigDecimal( 42 ), "memberOf", null, Boolean.FALSE}, 
                {new BigDecimal( 42 ), "not memberOf", col, Boolean.FALSE}, 
                {new BigDecimal( 43 ), "not memberOf", col, Boolean.TRUE}, 
                {null, "not memberOf", col, Boolean.TRUE},
                {new BigDecimal( 42 ), "not memberOf", null, Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.BIG_DECIMAL_TYPE );

    }

    @Test
    public void testBigInteger() {

        final ValueType type = ValueType.determineValueType( BigInteger.class );
        assertSame( type,
                    ValueType.BIG_INTEGER_TYPE );

        Collection col = new ArrayList();
        col.add( new BigInteger( "42" ) );
        col.add( new BigInteger( "45" ) );

        final Object[][] data = {
                {new BigInteger( "42" ), "==", new BigInteger( "42" ), Boolean.TRUE}, 
                {new BigInteger( "42" ), "==", new BigDecimal( "42.0" ), Boolean.TRUE}, 
                {new BigInteger( "42" ), "==", new Double( 42 ), Boolean.TRUE}, 
                {new BigInteger( "42" ), "!=", new BigDecimal( "43.0" ), Boolean.TRUE}, 
                {new BigInteger( "42" ), "!=", new Double( 43 ), Boolean.TRUE}, 
                {new BigInteger( "42" ), "<", new BigInteger( "43" ), Boolean.TRUE}, 
                {new BigInteger( "42" ), ">=", new BigInteger( "41" ), Boolean.TRUE},
                {new BigInteger( "42" ), "!=", new BigInteger( "41" ), Boolean.TRUE}, 
                {new BigInteger( "42" ), ">", new BigInteger( "41" ), Boolean.TRUE}, 
                {new BigInteger( "42" ), "<=", new BigInteger( "42" ), Boolean.TRUE},
                {new BigInteger( "42" ), ">", new BigInteger( "100" ), Boolean.FALSE},
                {new BigInteger( "42" ), "<", new Long( "43" ), Boolean.TRUE}, 
                {new BigInteger( "42" ), ">=", new Long( "41" ), Boolean.TRUE},
                {new BigInteger( "42" ), "!=", new Long( "41" ), Boolean.TRUE}, 
                {new BigInteger( "42" ), ">", new Long( "41" ), Boolean.TRUE}, 
                {new BigInteger( "42" ), "<=", new Long( "42" ), Boolean.TRUE},
                {new BigInteger( "42" ), ">", new Long( "100" ), Boolean.FALSE}, 
                {new BigInteger( "42" ), "==", null, Boolean.FALSE}, 
                {new BigInteger( "42" ), "!=", null, Boolean.TRUE}, 
                {null, "==", new BigInteger( "42" ), Boolean.FALSE},
                {null, "!=", new BigInteger( "42" ), Boolean.TRUE}, 
                {null, "<", new BigInteger( "43" ), Boolean.FALSE}, 
                {null, ">=", new BigInteger( "-10" ), Boolean.FALSE}, 
                {null, ">", new BigInteger( "-10" ), Boolean.FALSE},
                {null, "<=", new BigInteger( "42" ), Boolean.FALSE}, 
                {new BigInteger( "42" ), "memberOf", col, Boolean.TRUE}, 
                {new BigInteger( "43" ), "memberOf", col, Boolean.FALSE}, 
                {null, "memberOf", col, Boolean.FALSE},
                {new BigInteger( "42" ), "memberOf", null, Boolean.FALSE}, 
                {new BigInteger( "42" ), "not memberOf", col, Boolean.FALSE}, 
                {new BigInteger( "43" ), "not memberOf", col, Boolean.TRUE}, 
                {null, "not memberOf", col, Boolean.TRUE},
                {new BigInteger( "42" ), "not memberOf", null, Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.BIG_INTEGER_TYPE );

    }

    @Test
    public void testShort() {

        Collection col = new ArrayList();
        col.add( new Short( (short) 42 ) );
        col.add( new Short( (short) 45 ) );

        //Test data: Obj1, Operand, Obj2
        final Object[][] data = {{new Short( (short) 42 ), "==", new Short( (short) 42 ), Boolean.TRUE}, {new Short( (short) 42 ), "<", new Short( (short) 43 ), Boolean.TRUE}, {new Short( (short) 42 ), ">=", new Short( (short) 41 ), Boolean.TRUE},
                {new Short( (short) 42 ), "!=", new Short( (short) 41 ), Boolean.TRUE}, {new Short( (short) 42 ), "==", null, Boolean.FALSE}, {null, "==", null, Boolean.TRUE}, {null, "!=", null, Boolean.FALSE},
                {null, "!=", new Short( (short) 42 ), Boolean.TRUE}, {null, "==", new Short( (short) 42 ), Boolean.FALSE}, {null, "<", new Short( (short) 43 ), Boolean.FALSE}, {null, ">=", new Short( (short) -10 ), Boolean.FALSE},
                {null, ">", new Short( (short) -10 ), Boolean.FALSE}, {null, "<=", new Short( (short) 42 ), Boolean.FALSE}, {new Short( (short) 42 ), "memberOf", col, Boolean.TRUE}, {new Short( (short) 43 ), "memberOf", col, Boolean.FALSE},
                {null, "memberOf", col, Boolean.FALSE}, {new Short( (short) 42 ), "memberOf", null, Boolean.FALSE}, {new Short( (short) 42 ), "not memberOf", col, Boolean.FALSE}, {new Short( (short) 43 ), "not memberOf", col, Boolean.TRUE},
                {null, "not memberOf", col, Boolean.TRUE}, {new Short( (short) 42 ), "not memberOf", null, Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.PSHORT_TYPE );
    }

    @Test
    public void testBoolean() {

        Collection col = new ArrayList();
        col.add( new Boolean( true ) );
        col.add( new Boolean( true ) );

        //Test data: Obj1, Operand, Obj2
        final Object[][] data = {
                                 {new Boolean( true ), "==", new Boolean( true ), Boolean.TRUE}, 
                                 {new Boolean( false ), "!=", new Boolean( true ), Boolean.TRUE}, 
                                 {new Boolean( true ), "==", new Boolean( false ), Boolean.FALSE},
                                 {new Boolean( true ), "!=", new Boolean( false ), Boolean.TRUE}, 
                                 {new Boolean( true ), "==", null, Boolean.FALSE}, 
                                 {null, "==", null, Boolean.TRUE}, 
                                 {null, "!=", null, Boolean.FALSE}, 
                                 {null, "!=", new Boolean( true ), Boolean.TRUE},
                                 {null, "==", new Boolean( true ), Boolean.FALSE}, 
                                 {new Boolean( true ), "memberOf", col, Boolean.TRUE}, 
                                 {new Boolean( false ), "memberOf", col, Boolean.FALSE}, 
                                 {null, "memberOf", col, Boolean.FALSE},
                                 {new Boolean( true ), "memberOf", null, Boolean.FALSE}, 
                                 {new Boolean( true ), "not memberOf", col, Boolean.FALSE}, 
                                 {new Boolean( false ), "not memberOf", col, Boolean.TRUE}, 
                                 {null, "not memberOf", col, Boolean.TRUE},
                                 {new Boolean( true ), "not memberOf", null, Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.PBOOLEAN_TYPE );
    }

    @Test
    public void testDouble() {
        Collection col = new ArrayList();
        col.add( new Double( 42 ) );
        col.add( new Double( 45 ) );

        final Object[][] data = {{new Double( 42 ), "==", new Double( 42 ), Boolean.TRUE}, {new Double( 42 ), "<", new Double( 43 ), Boolean.TRUE}, {new Double( 42 ), ">=", new Double( 41 ), Boolean.TRUE},
                {new Double( 42 ), "!=", new Double( 41 ), Boolean.TRUE}, {new Double( 42 ), ">", new Double( 41 ), Boolean.TRUE}, {new Double( 42 ), ">=", new Double( 41 ), Boolean.TRUE}, {new Double( 42 ), ">=", new Double( 42 ), Boolean.TRUE},
                {new Double( 42 ), ">=", new Double( 100 ), Boolean.FALSE}, {new Double( 42 ), "<", new Double( 1 ), Boolean.FALSE}, {new Double( 42 ), "==", null, Boolean.FALSE}, {null, "==", null, Boolean.TRUE}, {null, "!=", null, Boolean.FALSE},
                {null, "!=", new Double( 42 ), Boolean.TRUE}, {null, "==", new Double( 42 ), Boolean.FALSE}, {null, "<", new Double( 43 ), Boolean.FALSE}, {null, ">=", new Double( -10 ), Boolean.FALSE}, {null, ">", new Double( -10 ), Boolean.FALSE},
                {null, "<=", new Double( 42 ), Boolean.FALSE}, {new Double( 42 ), "memberOf", col, Boolean.TRUE}, {new Double( 43 ), "memberOf", col, Boolean.FALSE}, {null, "memberOf", col, Boolean.FALSE},
                {new Double( 42 ), "memberOf", null, Boolean.FALSE}, {new Double( 42 ), "not memberOf", col, Boolean.FALSE}, {new Double( 43 ), "not memberOf", col, Boolean.TRUE}, {null, "not memberOf", col, Boolean.TRUE},
                {new Double( 42 ), "not memberOf", null, Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.PDOUBLE_TYPE );
    }

    @Test
    public void testFloat() {
        Collection col = new ArrayList();
        col.add( new Float( 42 ) );
        col.add( new Float( 45 ) );

        final Object[][] data = {{new Float( 42 ), "==", new Float( 42 ), Boolean.TRUE}, {new Float( 42 ), "<", new Float( 43 ), Boolean.TRUE}, {new Float( 42 ), ">=", new Float( 41 ), Boolean.TRUE},
                {new Float( 42 ), "!=", new Float( 41 ), Boolean.TRUE}, {new Float( 42 ), ">", new Float( 41 ), Boolean.TRUE}, {new Float( 42 ), ">=", new Float( 41 ), Boolean.TRUE}, {new Float( 42 ), ">=", new Float( 42 ), Boolean.TRUE},
                {new Float( 42 ), ">=", new Float( 100 ), Boolean.FALSE}, {new Float( 42 ), "<", new Float( 1 ), Boolean.FALSE}, {new Float( 42 ), "==", null, Boolean.FALSE}, {null, "==", null, Boolean.TRUE}, {null, "!=", null, Boolean.FALSE},
                {null, "!=", new Float( 42 ), Boolean.TRUE}, {null, "==", new Float( 42 ), Boolean.FALSE}, {null, "<", new Float( 43 ), Boolean.FALSE}, {null, ">=", new Float( -10 ), Boolean.FALSE}, {null, ">", new Float( -10 ), Boolean.FALSE},
                {null, "<=", new Float( 42 ), Boolean.FALSE}, {new Float( 42 ), "memberOf", col, Boolean.TRUE}, {new Float( 43 ), "memberOf", col, Boolean.FALSE}, {null, "memberOf", col, Boolean.FALSE},
                {new Float( 42 ), "memberOf", null, Boolean.FALSE}, {new Float( 42 ), "not memberOf", col, Boolean.FALSE}, {new Float( 43 ), "not memberOf", col, Boolean.TRUE}, {null, "not memberOf", col, Boolean.TRUE},
                {new Float( 42 ), "not memberOf", null, Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.PFLOAT_TYPE );
    }

    @Test
    public void testLong() {
        Collection col = new ArrayList();
        col.add( new Long( 42 ) );
        col.add( new Long( 45 ) );

        final Object[][] data = {{new Long( 42 ), "==", new Long( 42 ), Boolean.TRUE}, {new Long( 42 ), "<", new Long( 43 ), Boolean.TRUE}, {new Long( 42 ), ">=", new Long( 41 ), Boolean.TRUE}, {new Long( 42 ), "!=", new Long( 41 ), Boolean.TRUE},
                {new Long( 42 ), ">", new Long( 41 ), Boolean.TRUE}, {new Long( 42 ), ">=", new Long( 41 ), Boolean.TRUE}, {new Long( 42 ), ">=", new Long( 42 ), Boolean.TRUE}, {new Long( 42 ), ">=", new Long( 100 ), Boolean.FALSE},
                {new Long( 42 ), "<", new Long( 1 ), Boolean.FALSE}, {new Long( 42 ), "==", null, Boolean.FALSE}, {null, "==", null, Boolean.TRUE}, {null, "!=", null, Boolean.FALSE}, {null, "!=", new Long( 42 ), Boolean.TRUE},
                {null, "==", new Long( 42 ), Boolean.FALSE}, {null, "<", new Long( 43 ), Boolean.FALSE}, {null, ">=", new Long( -10 ), Boolean.FALSE}, {null, ">", new Long( -10 ), Boolean.FALSE}, {null, "<=", new Long( 42 ), Boolean.FALSE},
                {new Long( 42 ), "memberOf", col, Boolean.TRUE}, {new Long( 43 ), "memberOf", col, Boolean.FALSE}, {null, "memberOf", col, Boolean.FALSE}, {new Long( 42 ), "memberOf", null, Boolean.FALSE},
                {new Long( 42 ), "not memberOf", col, Boolean.FALSE}, {new Long( 43 ), "not memberOf", col, Boolean.TRUE}, {null, "not memberOf", col, Boolean.TRUE}, {new Long( 42 ), "not memberOf", null, Boolean.FALSE}};

        runEvaluatorTest( data,
                          ValueType.PLONG_TYPE );
    }

    @Test
    public void testCharacter() {
        Collection col = new ArrayList();
        col.add( new Character( 'a' ) );
        col.add( new Character( 'b' ) );

        final Object[][] data = {{new Character( 'a' ), "==", new Character( 'a' ), Boolean.TRUE}, {new Character( 'a' ), "<", new Character( 'b' ), Boolean.TRUE}, {new Character( 'a' ), ">=", new Character( 'a' ), Boolean.TRUE},
                {new Character( 'a' ), "!=", new Character( 'Z' ), Boolean.TRUE}, {new Character( 'z' ), ">", new Character( 'a' ), Boolean.TRUE}, {new Character( 'z' ), ">=", new Character( 'z' ), Boolean.TRUE},
                {new Character( 'z' ), ">=", new Character( 'a' ), Boolean.TRUE}, {new Character( 'a' ), ">=", new Character( 'z' ), Boolean.FALSE}, {new Character( 'z' ), "<", new Character( 'a' ), Boolean.FALSE},
                {new Character( 'z' ), "==", null, Boolean.FALSE}, {null, "==", null, Boolean.TRUE}, {null, "!=", null, Boolean.FALSE}, {null, "!=", new Character( 'z' ), Boolean.TRUE}, {null, "==", new Character( 'z' ), Boolean.FALSE},
                {null, "<", new Character( 'a' ), Boolean.FALSE}, {null, ">=", new Character( '\0' ), Boolean.FALSE}, {null, ">", new Character( '\0' ), Boolean.FALSE}, {null, "<=", new Character( 'a' ), Boolean.FALSE},
                {new Character( 'a' ), "memberOf", col, Boolean.TRUE}, {new Character( 'z' ), "memberOf", col, Boolean.FALSE}, {null, "memberOf", col, Boolean.FALSE}, {new Character( 'a' ), "memberOf", null, Boolean.FALSE},
                {new Character( 'a' ), "not memberOf", col, Boolean.FALSE}, {new Character( 'z' ), "not memberOf", col, Boolean.TRUE}, {null, "not memberOf", col, Boolean.TRUE}, {new Character( 'a' ), "not memberOf", null, Boolean.FALSE}};
        runEvaluatorTest( data,
                          ValueType.PCHAR_TYPE );
    }

    @Test
    public void testDate() throws Exception {

        final SimpleDateFormat df = new SimpleDateFormat( "dd-MMM-yyyy",
                                                          Locale.ENGLISH );
        Collection col = new ArrayList();
        col.add( df.parse( "10-Jul-1974" ) );
        col.add( df.parse( "11-Jul-1974" ) );

        //note that strings are also allowed on the right
        final Object[][] data = {{df.parse( "10-Jul-1974" ), "==", df.parse( "10-Jul-1974" ), Boolean.TRUE}, {df.parse( "10-Jul-1974" ), "<", df.parse( "11-Jul-1974" ), Boolean.TRUE},
                {df.parse( "10-Jul-1974" ), ">=", df.parse( "10-Jul-1974" ), Boolean.TRUE}, {df.parse( "10-Jul-1974" ), "!=", df.parse( "11-Jul-1974" ), Boolean.TRUE}, {df.parse( "10-Jul-2000" ), ">", df.parse( "10-Jul-1974" ), Boolean.TRUE},
                {df.parse( "10-Jul-1974" ), ">=", df.parse( "10-Jul-1974" ), Boolean.TRUE}, {df.parse( "11-Jul-1974" ), ">=", df.parse( "10-Jul-1974" ), Boolean.TRUE}, {df.parse( "10-Jul-1974" ), ">=", df.parse( "11-Jul-1974" ), Boolean.FALSE},
                {df.parse( "10-Jul-2000" ), "<", df.parse( "10-Jul-1974" ), Boolean.FALSE}, {df.parse( "10-Jul-1974" ), "<", df.parse( "11-Jul-1974" ), Boolean.TRUE}, {df.parse( "10-Jul-1974" ), "==", null, Boolean.FALSE},
                {df.parse( "10-Jul-1974" ), "!=", null, Boolean.TRUE}, {null, "==", null, Boolean.TRUE}, {null, "==", df.parse( "10-Jul-1974" ), Boolean.FALSE}, {null, "!=", null, Boolean.FALSE},
                {null, "!=", df.parse( "10-Jul-1974" ), Boolean.TRUE}, {null, "<", df.parse( "10-Jul-1974" ), Boolean.FALSE}, {null, ">=", new Date( 0 ), Boolean.FALSE}, {null, ">", new Date( 0 ), Boolean.FALSE},
                {null, "<=", df.parse( "10-Jul-1974" ), Boolean.FALSE}, {df.parse( "10-Jul-1974" ), "memberOf", col, Boolean.TRUE}, {df.parse( "15-Jul-1974" ), "memberOf", col, Boolean.FALSE}, {null, "memberOf", col, Boolean.FALSE},
                {df.parse( "10-Jul-1974" ), "memberOf", null, Boolean.FALSE}, {df.parse( "10-Jul-1974" ), "not memberOf", col, Boolean.FALSE}, {df.parse( "15-Jul-1974" ), "not memberOf", col, Boolean.TRUE}, {null, "not memberOf", col, Boolean.TRUE},
                {df.parse( "10-Jul-1974" ), "not memberOf", null, Boolean.FALSE}};
        runEvaluatorTest( data,
                          ValueType.DATE_TYPE );
    }

    @Test
    public void testByte() {
        Collection col = new ArrayList();
        col.add( new Byte( "1" ) );
        col.add( new Byte( "2" ) );

        final Object[][] data = {{new Byte( "1" ), "==", new Byte( "1" ), Boolean.TRUE}, {new Byte( "1" ), "==", new Byte( "2" ), Boolean.FALSE}, {new Byte( "1" ), "!=", new Byte( "2" ), Boolean.TRUE},
                {new Byte( "1" ), "!=", new Byte( "1" ), Boolean.FALSE}, {new Byte( "1" ), "<=", new Byte( "1" ), Boolean.TRUE}, {new Byte( "1" ), "==", null, Boolean.FALSE}, {new Byte( "1" ), "<", new Byte( "2" ), Boolean.TRUE},
                {new Byte( "2" ), ">=", new Byte( "1" ), Boolean.TRUE}, {new Byte( "2" ), ">", new Byte( "1" ), Boolean.TRUE}, {new Byte( "1" ), "<=", new Byte( "2" ), Boolean.TRUE}, {null, "==", null, Boolean.TRUE},
                {null, "!=", null, Boolean.FALSE}, {null, "!=", new Byte( "1" ), Boolean.TRUE}, {null, "==", new Byte( "1" ), Boolean.FALSE}, {null, "<", new Byte( Byte.MAX_VALUE ), Boolean.FALSE},
                {null, ">=", new Byte( Byte.MIN_VALUE ), Boolean.FALSE}, {null, ">", new Byte( Byte.MIN_VALUE ), Boolean.FALSE}, {null, "<=", new Byte( Byte.MAX_VALUE ), Boolean.FALSE}, {new Byte( "1" ), "memberOf", col, Boolean.TRUE},
                {new Byte( "3" ), "memberOf", col, Boolean.FALSE}, {null, "memberOf", col, Boolean.FALSE}, {new Byte( "1" ), "memberOf", null, Boolean.FALSE}, {new Byte( "1" ), "not memberOf", col, Boolean.FALSE},
                {new Byte( "3" ), "not memberOf", col, Boolean.TRUE}, {null, "not memberOf", col, Boolean.TRUE}, {new Byte( "1" ), "not memberOf", null, Boolean.FALSE}};
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
        final InternalReadAccessor extractor = new MockExtractor();
        for ( int i = 0; i < data.length; i++ ) {
            final Object[] row = data[i];
            boolean isNegated = ((String) row[1]).startsWith( "not " );
            String evaluatorStr = isNegated ? ((String) row[1]).substring( 4 ) : (String) row[1];
            final Evaluator evaluator = (Evaluator) registry.getEvaluatorDefinition( evaluatorStr ).getEvaluator( valueType,
                                                                                                      evaluatorStr,
                                                                                                      isNegated,
                                                                                                      null );
            assertNotNull( "Evaluator '"+(isNegated ? "not " : "")+evaluatorStr+"' not foung for "+valueType, evaluator );
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
                                                    final InternalReadAccessor extractor,
                                                    final Object[] row,
                                                    final Evaluator evaluator) {
        final FieldValue value = FieldFactory.getFieldValue( row[2] );
        final boolean result = evaluator.evaluate( ( InternalWorkingMemory ) new ReteooRuleBase( "id1" ).newStatefulSession(),
                                                   extractor,
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
                                                 final InternalReadAccessor extractor,
                                                 final Object[] row,
                                                 final Evaluator evaluator) {
        final VariableContextEntry context = this.getContextEntry( evaluator,
                                                                   extractor,
                                                                   valueType,
                                                                   row );
        final boolean result = evaluator.evaluateCachedRight( ( InternalWorkingMemory ) new ReteooRuleBase( "id1" ).newStatefulSession(),
                                                              context,
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
                                                final InternalReadAccessor extractor,
                                                final Object[] row,
                                                final Evaluator evaluator) {
        final VariableContextEntry context = this.getContextEntry( evaluator,
                                                                   extractor,
                                                                   valueType,
                                                                   row );
        final boolean result = evaluator.evaluateCachedLeft( ( InternalWorkingMemory ) new ReteooRuleBase( "id1" ).newStatefulSession(),
                                                             context,
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
                                                     final InternalReadAccessor extractor,
                                                     final Object[] row,
                                                     final Evaluator evaluator) {
        final boolean result = evaluator.evaluate( null,
                                                   extractor,
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

    private VariableContextEntry getContextEntry(final Evaluator evaluator,
                                                 final InternalReadAccessor extractor,
                                                 final ValueType valueType,
                                                 final Object[] row) {
        final Declaration declaration = new Declaration( "test",
                                                         extractor,
                                                         null );
        final ValueType coerced = evaluator.getCoercedValueType();
        
        if ( coerced.isIntegerNumber() ) {
            final LongVariableContextEntry context = new LongVariableContextEntry( extractor,
                                                                                   declaration,
                                                                                   evaluator );
            
            if ( row[2] == null ) {
                context.leftNull = true;
            } else {
                context.left = ((Number) row[2]).longValue();
            }

            if ( row[0] == null ) {
                context.rightNull = true;
            } else {
                context.right = ((Number) row[0]).longValue();
            }
            return context;
        } else if ( coerced.isChar() ) {
            final CharVariableContextEntry context = new CharVariableContextEntry( extractor,
                                                                                   declaration,
                                                                                   evaluator );

            if ( row[2] == null ) {
                context.leftNull = true;
            } else {
                context.left = ((Character) row[2]).charValue();
            }

            if ( row[0] == null ) {
                context.rightNull = true;
            } else {
                context.right = ((Character) row[0]).charValue();
            }
            return context;
        } else if ( coerced.isBoolean() ) {
            final BooleanVariableContextEntry context = new BooleanVariableContextEntry( extractor,
                                                                                         declaration,
                                                                                         evaluator );

            if ( row[2] == null ) {
                context.leftNull = true;
            } else {
                context.left = ((Boolean) row[2]).booleanValue();
            }

            if ( row[0] == null ) {
                context.rightNull = true;
            } else {
                context.right = ((Boolean) row[0]).booleanValue();
            }
            return context;
        } else if ( coerced.isFloatNumber() ) {
            final DoubleVariableContextEntry context = new DoubleVariableContextEntry( extractor,
                                                                                       declaration,
                                                                                       evaluator );
            if ( row[2] == null ) {
                context.leftNull = true;
            } else {
                context.left = ((Number) row[2]).doubleValue();
            }

            if ( row[0] == null ) {
                context.rightNull = true;
            } else {
                context.right = ((Number) row[0]).doubleValue();
            }
            return context;
        } else {
            final ObjectVariableContextEntry context = new ObjectVariableContextEntry( extractor,
                                                                                       declaration,
                                                                                       evaluator );
            if ( row[2] == null ) {
                context.leftNull = true;
            } else {
                context.left = row[2];
            }

            if ( row[0] == null ) {
                context.rightNull = true;
            } else {
                context.right = row[0];
            }
            return context;
        }
    }

    private static class MockExtractor
        implements
        InternalReadAccessor {

        private static final long serialVersionUID = 510l;

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public boolean isSelfReference() {
            return false;
        }

        public boolean getBooleanValue(InternalWorkingMemory workingMemory,
                                       final Object object) {
            return object != null ? ((Boolean) object).booleanValue() : false;
        }

        public byte getByteValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
            return object != null ? ((Number) object).byteValue() : (byte) 0;
        }

        public char getCharValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
            return object != null ? ((Character) object).charValue() : '\0';
        }

        public double getDoubleValue(InternalWorkingMemory workingMemory,
                                     final Object object) {
            return object != null ? ((Number) object).doubleValue() : 0.0;
        }

        public Class getExtractToClass() {
            return null;
        }

        public String getExtractToClassName() {
            return null;
        }

        public float getFloatValue(InternalWorkingMemory workingMemory,
                                   final Object object) {
            return object != null ? ((Number) object).floatValue() : (float) 0.0;
        }

        public int getHashCode(InternalWorkingMemory workingMemory,
                               final Object object) {
            return 0;
        }

        public int getIntValue(InternalWorkingMemory workingMemory,
                               final Object object) {
            return object != null ? ((Number) object).intValue() : 0;
        }

        public long getLongValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
            return object != null ? ((Number) object).longValue() : 0;
        }

        public Method getNativeReadMethod() {
            return null;
        }

        public String getNativeReadMethodName() {
            return null;
        }

        public short getShortValue(InternalWorkingMemory workingMemory,
                                   final Object object) {
            return object != null ? ((Number) object).shortValue() : (short) 0;
        }

        public Object getValue(InternalWorkingMemory workingMemory,
                               final Object object) {
            return object;
        }

        public boolean isNullValue(InternalWorkingMemory workingMemory,
                                   final Object object) {
            return object == null;
        }

        public ValueType getValueType() {
            // TODO Auto-generated method stub
            return null;
        }

        public int getIndex() {
            return 0;
        }

        public boolean isGlobal() {
            return false;
        }

        public boolean getBooleanValue(Object object) {
            // TODO Auto-generated method stub
            return false;
        }

        public byte getByteValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public char getCharValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public double getDoubleValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public float getFloatValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public int getHashCode(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public int getIntValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public long getLongValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public short getShortValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public Object getValue(Object object) {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isNullValue(Object object) {
            // TODO Auto-generated method stub
            return false;
        }

        public BigDecimal getBigDecimalValue(InternalWorkingMemory workingMemory,
                                             Object object) {
            return MathUtils.getBigDecimal( object );
        }

        public BigInteger getBigIntegerValue(InternalWorkingMemory workingMemory,
                                             Object object) {
            return MathUtils.getBigInteger( object );
        }

        public BigDecimal getBigDecimalValue(Object object) {
            return MathUtils.getBigDecimal( object );
        }

        public BigInteger getBigIntegerValue(Object object) {
            return MathUtils.getBigInteger( object );
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
