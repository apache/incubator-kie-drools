package org.drools.mvel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.drools.util.DateUtils;
import org.drools.mvel.expr.MVELDateCoercion;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MVELDateCoercionTest {

    @Test
    public void testDate() {
        MVELDateCoercion co = new MVELDateCoercion();
        assertThat(co.canConvertFrom(Date.class)).isTrue();
        assertThat(co.canConvertFrom(Number.class)).isFalse();

        Date d = new Date();
        assertThat(co.convertFrom(d)).isSameAs(d);
    }

    @Test
    public void testString() throws Exception {
        MVELDateCoercion co = new MVELDateCoercion();
        assertThat(co.canConvertFrom(Date.class)).isTrue();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.UK);

        String dt = df.format(df.parse("10-Jul-1974"));
        Date dt_ = DateUtils.parseDate(dt);
        assertThat(co.convertFrom(dt)).isEqualTo(dt_);
    }

}
