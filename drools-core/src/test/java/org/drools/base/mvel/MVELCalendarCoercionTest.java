/**
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
