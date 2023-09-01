/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
