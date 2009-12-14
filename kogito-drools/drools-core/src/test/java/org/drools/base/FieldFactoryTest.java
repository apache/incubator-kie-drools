package org.drools.base;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.drools.builder.impl.DateFormatsImpl;
import org.drools.spi.FieldValue;

public class FieldFactoryTest extends TestCase {

    public void testBigDecimal() {
        final FieldValue val = FieldFactory.getFieldValue( "42.42",
                                                           ValueType.BIG_DECIMAL_TYPE,
                                                           new DateFormatsImpl() );
        assertEquals( BigDecimal.class,
                      val.getValue().getClass() );
        assertTrue( val.getValue().equals( new BigDecimal( "42.42" ) ) );
    }

    public void testBigInteger() {
        final FieldValue val = FieldFactory.getFieldValue( "424242",
                                                           ValueType.BIG_INTEGER_TYPE,
                                                           new DateFormatsImpl() );
        assertEquals( BigInteger.class,
                      val.getValue().getClass() );
        assertTrue( val.getValue().equals( new BigInteger( "424242" ) ) );
    }

    public void testDate() throws Exception {
    	SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
    	String s = df.format(df.parse("10-Jul-1974"));
        final FieldValue val = FieldFactory.getFieldValue( s, ValueType.DATE_TYPE,
                                                           new DateFormatsImpl() );
        assertEquals( Date.class, val.getValue().getClass() );

        Date dt = (Date) val.getValue();
        assertEquals(s, df.format(dt));

    }

}
