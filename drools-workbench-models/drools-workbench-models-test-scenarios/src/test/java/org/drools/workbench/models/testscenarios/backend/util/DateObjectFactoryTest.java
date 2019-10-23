/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.testscenarios.backend.util;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.util.Date;

import org.drools.core.util.DateUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DateObjectFactoryTest {

    @Test
    public void date() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final Object timeObject = DateObjectFactory.createDateObject(Date.class, "12-Sep-2011");

        assertTrue(timeObject instanceof Date);
        assertEquals("12-Sep-2011", DateUtils.format((Date) timeObject));
    }

    @Test(expected = IllegalArgumentException.class)
    public void dateInvalidValue() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        DateObjectFactory.createDateObject(Date.class, "12345");
    }

    @Test(expected = NoSuchMethodException.class)
    public void dateInvalidClass() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        DateObjectFactory.createDateObject(String.class, "12-Sep-2011");
    }

    @Test
    public void localDate() {
        final Object timeObject = DateObjectFactory.createLocalDateObject("12-Sep-2011");

        assertTrue(timeObject instanceof LocalDate);
        final LocalDate localDate = (LocalDate) timeObject;
        assertEquals(2011, localDate.getYear());
        assertEquals(Month.SEPTEMBER, localDate.getMonth());
        assertEquals(12, localDate.getDayOfMonth());
    }

    @Test(expected = DateTimeParseException.class)
    public void localDateInvalidValue() {
        DateObjectFactory.createLocalDateObject("1234");
    }
}