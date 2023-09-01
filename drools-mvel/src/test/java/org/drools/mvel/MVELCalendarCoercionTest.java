package org.drools.mvel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.drools.util.DateUtils;
import org.drools.mvel.expr.MVELCalendarCoercion;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MVELCalendarCoercionTest {

    @Test
    public void testCalendar() {
        MVELCalendarCoercion co = new MVELCalendarCoercion();
        assertThat(co.canConvertFrom(Calendar.class)).isTrue();
        assertThat(co.canConvertFrom(Number.class)).isFalse();

        Calendar d = Calendar.getInstance();
        assertThat(co.convertFrom(d)).isSameAs(d);
    }

    @Test
    public void testString() throws Exception {
        MVELCalendarCoercion co = new MVELCalendarCoercion();
        assertThat(co.canConvertFrom(Calendar.class)).isTrue();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.UK);

        String dt = df.format(df.parse("10-Jul-1974"));

        Date dt_ = DateUtils.parseDate(dt);
        Calendar cal = Calendar.getInstance();
        cal.setTime( dt_ );
        assertThat(co.convertFrom(dt)).isEqualTo(cal);
    }

}
