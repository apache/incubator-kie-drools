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
import java.util.Date;

import org.drools.core.util.DateUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DateObjectFactoryTest {

    @Test
    public void date() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final Object timeObject = DateObjectFactory.createTimeObject(Date.class, "12-Sep-2011");

        assertTrue(timeObject instanceof Date);
        assertEquals("12-Sep-2011", DateUtils.format((Date) timeObject));
    }

    @Test
    public void localDate() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final Object timeObject = DateObjectFactory.createTimeObject(LocalDate.class, "12-Sep-2011");

        assertTrue(timeObject instanceof LocalDate);
        final LocalDate localDate = (LocalDate) timeObject;
        assertEquals(2011, localDate.getYear());
        assertEquals(Month.SEPTEMBER, localDate.getMonth());
        assertEquals(12, localDate.getDayOfMonth());
    }
}