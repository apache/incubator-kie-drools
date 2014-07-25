/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.base;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.core.spi.FieldValue;
import org.drools.core.type.DateFormatsImpl;

public class FieldFactoryTest {

    @Test
    public void testBigDecimal() {
        final FieldValue val = FieldFactory.getInstance().getFieldValue( "42.42",
                                                                         ValueType.BIG_DECIMAL_TYPE,
                                                                         new DateFormatsImpl() );
        assertEquals( BigDecimal.class,
                      val.getValue().getClass() );
        assertTrue( val.getValue().equals( new BigDecimal( "42.42" ) ) );
    }

    @Test
    public void testBigInteger() {
        final FieldValue val = FieldFactory.getInstance().getFieldValue( "424242",
                                                                         ValueType.BIG_INTEGER_TYPE,
                                                                         new DateFormatsImpl() );
        assertEquals( BigInteger.class,
                      val.getValue().getClass() );
        assertTrue( val.getValue().equals( new BigInteger( "424242" ) ) );
    }

    @Test
    public void testDate() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        String s = df.format(df.parse("10-Jul-1974"));
        final FieldValue val = FieldFactory.getInstance().getFieldValue( s,
                                                                         ValueType.DATE_TYPE,
                                                                         new DateFormatsImpl() );
        assertEquals( Date.class, val.getValue().getClass() );

        Date dt = (Date) val.getValue();
        assertEquals(s, df.format(dt));

    }

}
