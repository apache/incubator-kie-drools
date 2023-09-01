package org.drools.mvel.field;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.drools.base.base.ValueType;
import org.drools.base.rule.accessor.FieldValue;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldFactoryTest {

    @Test
    public void testBigDecimal() {
        final FieldValue val = FieldFactory.getInstance().getFieldValue( "42.42",
                                                                         ValueType.BIG_DECIMAL_TYPE );
        assertThat(val.getValue().getClass()).isEqualTo(BigDecimal.class);
        assertThat(val.getValue().equals(new BigDecimal( "42.42" ))).isTrue();
    }

    @Test
    public void testBigInteger() {
        final FieldValue val = FieldFactory.getInstance().getFieldValue( "424242",
                                                                         ValueType.BIG_INTEGER_TYPE );
        assertThat(val.getValue().getClass()).isEqualTo(BigInteger.class);
        assertThat(val.getValue().equals(new BigInteger( "424242" ))).isTrue();
    }

    @Test
    public void testDate() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        String s = df.format(df.parse("10-Jul-1974"));
        final FieldValue val = FieldFactory.getInstance().getFieldValue( s,
                                                                         ValueType.DATE_TYPE );
        assertThat(val.getValue().getClass()).isEqualTo(Date.class);

        Date dt = (Date) val.getValue();
        assertThat(df.format(dt)).isEqualTo(s);

    }

}
