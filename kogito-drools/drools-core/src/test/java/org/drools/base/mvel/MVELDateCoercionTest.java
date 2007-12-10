package org.drools.base.mvel;

import java.util.Date;

import junit.framework.TestCase;

import org.drools.util.DateUtils;

public class MVELDateCoercionTest extends TestCase {

    public void testDate() {
        MVELDateCoercion co = new MVELDateCoercion();
        assertTrue(co.canConvertFrom( Date.class ));
        assertFalse(co.canConvertFrom( Number.class ));

        Date d = new Date();
        assertSame(d, co.convertFrom( d ));
    }

    public void testString() {
        MVELDateCoercion co = new MVELDateCoercion();
        assertTrue(co.canConvertFrom( Date.class ));

        String dt = "10-Jul-1974";
        Date dt_ = DateUtils.parseDate( dt );
        assertEquals(dt_, co.convertFrom( dt ));
    }

}
