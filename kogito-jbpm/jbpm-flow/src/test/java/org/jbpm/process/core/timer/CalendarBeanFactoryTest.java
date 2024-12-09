/*
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

package org.jbpm.process.core.timer;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CalendarBeanFactoryTest {

    @Test
    void testCreateCalendarBean() {
        // This test relies on src/test/resources/calendar.properties:
        // checked values comes from it
        CalendarBean calendarBean = CalendarBeanFactory.createCalendarBean();
        assertThat(calendarBean).isNotNull();
        assertThat(calendarBean.getStartHour()).isEqualTo(10);
        assertThat(calendarBean.getEndHour()).isEqualTo(16);
        assertThat(calendarBean.getHoursInDay()).isEqualTo(6);
        assertThat(calendarBean.getDaysPerWeek()).isEqualTo(5);
        assertThat(calendarBean.getWeekendDays()).contains(Calendar.SATURDAY, Calendar.SUNDAY);
        assertThat(calendarBean.getHolidays()).isEmpty();
        assertThat(calendarBean.getTimezone()).isEqualTo(TimeZone.getDefault().getID());
    }
}
