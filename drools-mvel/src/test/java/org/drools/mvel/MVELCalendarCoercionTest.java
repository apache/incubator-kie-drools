/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.drools.core.util.DateUtils;
import org.drools.mvel.expr.MVELCalendarCoercion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MVELCalendarCoercionTest {

    @Test
    public void testCalendar() {
        MVELCalendarCoercion co = new MVELCalendarCoercion();
        assertTrue(co.canConvertFrom( Calendar.class ));
        assertFalse(co.canConvertFrom( Number.class ));

        Calendar d = Calendar.getInstance();
        assertSame(d, co.convertFrom( d ));
    }

    @Test
    public void testString() throws Exception {
        MVELCalendarCoercion co = new MVELCalendarCoercion();
        assertTrue(co.canConvertFrom( Calendar.class ));

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.UK);

        String dt = df.format(df.parse("10-Jul-1974"));

        Date dt_ = DateUtils.parseDate(dt);
        Calendar cal = Calendar.getInstance();
        cal.setTime( dt_ );
        assertEquals(cal, co.convertFrom( dt ));
    }

}
