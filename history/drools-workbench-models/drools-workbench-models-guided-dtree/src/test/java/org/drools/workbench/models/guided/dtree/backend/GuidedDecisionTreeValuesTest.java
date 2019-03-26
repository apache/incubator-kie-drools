/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.workbench.models.guided.dtree.backend;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BigDecimalValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BigIntegerValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.BooleanValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.ByteValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.DateValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.DoubleValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.FloatValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.IntegerValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.LongValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.ShortValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.impl.StringValue;
import org.junit.Test;

import static org.junit.Assert.*;

public class GuidedDecisionTreeValuesTest {

    @Test
    public void testBigDecimalValue() {
        final BigDecimal tv = new BigDecimal( "1000000.12345" );
        final BigDecimalValue v = new BigDecimalValue( tv );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "1000000.12345" );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "abc" );
        assertEquals( new BigDecimal( "0" ),
                      v.getValue() );
    }

    @Test
    public void testBigIntegerValue() {
        final BigInteger tv = new BigInteger( "1000000" );
        final BigIntegerValue v = new BigIntegerValue( tv );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "1000000" );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "abc" );
        assertEquals( new BigInteger( "0" ),
                      v.getValue() );
    }

    @Test
    public void testBooleanValue() {
        final Boolean tv = Boolean.TRUE;
        final BooleanValue v = new BooleanValue( tv );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "true" );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "abc" );
        assertEquals( Boolean.FALSE,
                      v.getValue() );
    }

    @Test
    public void testByteValue() {
        final Byte tv = new Byte( "8" );
        final ByteValue v = new ByteValue( tv );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "8" );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "abc" );
        assertEquals( new Byte( "0" ),
                      v.getValue() );
    }

    @Test
    public void testDateValue() {
        final Date tv = Calendar.getInstance().getTime();
        final DateValue v = new DateValue( tv );
        assertEquals( tv,
                      v.getValue() );

        try {
            v.setValue( "any string will do" );
            fail( "We should not be able to use DateValue.setValue(String)" );
        } catch ( UnsupportedOperationException e ) {
            //Swallow this is expected
        } catch ( Exception e ) {
            fail( "Unexpected exception thrown by DateValue.setValue(String)" );
        }
    }

    @Test
    public void testDoubleValue() {
        final Double tv = new Double( 8 );
        final DoubleValue v = new DoubleValue( tv );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "8" );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "abc" );
        assertEquals( new Double( 0 ),
                      v.getValue() );
    }

    @Test
    public void testFloatValue() {
        final Float tv = new Float( 1.2 );
        final FloatValue v = new FloatValue( tv );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "1.2" );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "abc" );
        assertEquals( new Float( "0" ),
                      v.getValue() );
    }

    @Test
    public void testIntegerValue() {
        final Integer tv = new Integer( 8 );
        final IntegerValue v = new IntegerValue( tv );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "8" );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "abc" );
        assertEquals( new Integer( 0 ),
                      v.getValue() );
    }

    @Test
    public void testLongValue() {
        final Long tv = new Long( 8 );
        final LongValue v = new LongValue( tv );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "8" );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "abc" );
        assertEquals( new Long( "0" ),
                      v.getValue() );
    }

    @Test
    public void testShortValue() {
        final Short tv = new Short( "8" );
        final ShortValue v = new ShortValue( tv );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "8" );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "abc" );
        assertEquals( new Short( "0" ),
                      v.getValue() );
    }

    @Test
    public void testStringValue() {
        final String tv = new String( "abc" );
        final StringValue v = new StringValue( tv );
        assertEquals( tv,
                      v.getValue() );

        v.setValue( "abc" );
        assertEquals( tv,
                      v.getValue() );
    }

}
