package org.drools.base.mvel;

import java.util.Calendar;
import java.util.Date;

import org.drools.base.evaluators.DateFactory;

import junit.framework.TestCase;

public class MVELCalendarCoercionTest extends TestCase {

    public void testCalendar() {
        MVELCalendarCoercion co = new MVELCalendarCoercion();
        assertTrue(co.canConvertFrom( Calendar.class ));
        assertFalse(co.canConvertFrom( Number.class ));

        Calendar d = Calendar.getInstance();
        assertSame(d, co.convertFrom( d ));
    }

    public void testString() {
        MVELCalendarCoercion co = new MVELCalendarCoercion();
        assertTrue(co.canConvertFrom( Calendar.class ));

        String dt = "10-Jul-1974";
        Date dt_ = DateFactory.parseDate( dt );
        Calendar cal = Calendar.getInstance();
        cal.setTime( dt_ );
        assertEquals(cal, co.convertFrom( dt ));
    }

}
