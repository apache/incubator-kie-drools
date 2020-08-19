/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.guided.dtable.shared.model;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.DataType.DataTypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

// These tests check legacy (pre-6.4.CR1) objects that have been de-serialized can be correctly read.
// DTCellValue52.valueString was set to an empty String statically and hence when the object was de-serialized
// it was possible for instances to have both a 'valueString (=="")' and '<another>Value' for types other than
// DataTypes.DataType.TYPE_STRING. For example. valueBoolean=true, valueString="", dataType=BOOLEAN.
public class DTCellValue52Test {

    private DTCellValue52 dcv;

    private Field fieldBoolean;
    private Field fieldDate;
    private Field fieldNumeric;
    private Field fieldString;
    private Field fieldDataType;

    private static final Date now = Calendar.getInstance().getTime();

    @Before
    public void setup() throws Exception {
        dcv = new DTCellValue52();
        final Class<?> c = dcv.getClass();
        fieldBoolean = c.getDeclaredField("valueBoolean");
        fieldDate = c.getDeclaredField("valueDate");
        fieldNumeric = c.getDeclaredField("valueNumeric");
        fieldString = c.getDeclaredField("valueString");
        fieldDataType = c.getDeclaredField("dataType");
        fieldBoolean.setAccessible(true);
        fieldDate.setAccessible(true);
        fieldNumeric.setAccessible(true);
        fieldString.setAccessible(true);
        fieldDataType.setAccessible(true);
    }

    @Test
    public void testGetBooleanValue() throws Exception {
        dcv.setBooleanValue(true);
        fieldDate.set(dcv,
                      now);
        fieldNumeric.set(dcv,
                         1L);
        fieldString.set(dcv,
                        "woot");
        assertEquals(DataTypes.BOOLEAN,
                     dcv.getDataType());
        assertTrue(dcv.getBooleanValue());
        assertNull(dcv.getDateValue());
        assertNull(dcv.getNumericValue());
        assertNull(dcv.getStringValue());
    }

    @Test
    public void testGetDateValue() throws Exception {
        fieldBoolean.set(dcv,
                         true);
        dcv.setDateValue(now);
        fieldNumeric.set(dcv,
                         1L);
        fieldString.set(dcv,
                        "woot");
        assertEquals(DataTypes.DATE,
                     dcv.getDataType());
        assertNull(dcv.getBooleanValue());
        assertEquals(now,
                     dcv.getDateValue());
        assertNull(dcv.getNumericValue());
        assertNull(dcv.getStringValue());
    }

    @Test
    public void testGetLocalDateValue() throws Exception {
        fieldBoolean.set(dcv,
                         true);
        dcv.setLocalDateValue("2020-Aug-19");
        fieldNumeric.set(dcv,
                         1L);
        assertEquals(DataTypes.LOCAL_DATE,
                     dcv.getDataType());
        assertNull(dcv.getBooleanValue());
        assertEquals("2020-Aug-19",
                     dcv.getLocalDateValue());
        assertNull(dcv.getDateValue());
        assertNull(dcv.getNumericValue());
    }

    @Test
    public void testGetNumericValue() throws Exception {
        fieldBoolean.set(dcv,
                         true);
        fieldDate.set(dcv,
                      now);
        dcv.setNumericValue(1L);
        fieldString.set(dcv,
                        "woot");
        assertEquals(DataTypes.NUMERIC_LONG,
                     dcv.getDataType());
        assertNull(dcv.getBooleanValue());
        assertNull(dcv.getDateValue());
        assertEquals(1L,
                     dcv.getNumericValue());
        assertNull(dcv.getStringValue());
    }

    @Test
    public void testGetStringValue() throws Exception {
        fieldBoolean.set(dcv,
                         true);
        fieldDate.set(dcv,
                      now);
        fieldNumeric.set(dcv,
                         1L);
        dcv.setStringValue("woot");
        assertEquals(DataTypes.STRING,
                     dcv.getDataType());
        assertNull(dcv.getBooleanValue());
        assertNull(dcv.getDateValue());
        assertNull(dcv.getNumericValue());
        assertEquals("woot",
                     dcv.getStringValue());
    }

    @Test
    public void testDefaultValue() throws Exception {
        final DTCellValue52 defaultValue = new DTCellValue52(1);
        final DTCellValue52 clone = new DTCellValue52(defaultValue);
        assertEquals(DataTypes.NUMERIC_INTEGER,
                     clone.getDataType());
        assertNull(clone.getBooleanValue());
        assertNull(clone.getDateValue());
        assertEquals(1,
                     clone.getNumericValue());
        assertNull(clone.getStringValue());
    }

    @Test
    public void testDefaultValueNull() throws Exception {
        final DTCellValue52 defaultValue = null;
        final DTCellValue52 clone = new DTCellValue52(defaultValue);
        assertEquals(DataTypes.STRING,
                     clone.getDataType());
        assertNull(clone.getBooleanValue());
        assertNull(clone.getDateValue());
        assertNull(clone.getNumericValue());
        assertNull(clone.getStringValue());
    }

    @Test
    public void testNonEmptyDefaultValue() {
        final String localDateDefaultValue = new DTCellValue52(DataTypes.LOCAL_DATE,
                                                               false,
                                                               "1-AUG-2020").getLocalDateValue();
        assertNotNull(localDateDefaultValue);
        assertFalse(localDateDefaultValue.isEmpty());
    }

    @Test
    public void testEmptyDefaultValue() {
        final String localDateDefaultValue = new DTCellValue52(DataTypes.LOCAL_DATE,
                                                               true,
                                                               "1-AUG-2020").getLocalDateValue();
        assertNull(localDateDefaultValue);
    }
}
