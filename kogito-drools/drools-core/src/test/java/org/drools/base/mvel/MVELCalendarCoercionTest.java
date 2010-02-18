package org.drools.base.mvel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.drools.core.util.DateUtils;
import org.drools.type.DateFormatsImpl;

public class MVELCalendarCoercionTest extends TestCase {

    public void testCalendar() {
        MVELCalendarCoercion co = new MVELCalendarCoercion();
        assertTrue(co.canConvertFrom( Calendar.class ));
        assertFalse(co.canConvertFrom( Number.class ));

        Calendar d = Calendar.getInstance();
        assertSame(d, co.convertFrom( d ));
    }

    public void testString() throws Exception {
        MVELCalendarCoercion co = new MVELCalendarCoercion();
        assertTrue(co.canConvertFrom( Calendar.class ));

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

        String dt = df.format(df.parse("10-Jul-1974"));

        Date dt_ = DateUtils.parseDate( dt,
                                        new DateFormatsImpl() );
        Calendar cal = Calendar.getInstance();
        cal.setTime( dt_ );
        assertEquals(cal, co.convertFrom( dt ));
    }

}
