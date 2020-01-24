/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.parser.feel11;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoPeriod;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;

import static org.junit.Assert.assertEquals;

public class ParserHelperTest {

    @Test
    public void testDetermineTypeFromClass_Null() {
        assertEquals(ParserHelper.determineTypeFromClass(null), BuiltInType.UNKNOWN);
    }

    @Test
    public void testDetermineTypeFromClass_Number() {
        assertEquals(ParserHelper.determineTypeFromClass(Number.class), BuiltInType.NUMBER);
    }

    @Test
    public void testDetermineTypeFromClass_String() {
        assertEquals(ParserHelper.determineTypeFromClass(String.class), BuiltInType.STRING);
        assertEquals(ParserHelper.determineTypeFromClass(Character.class), BuiltInType.STRING);
    }

    @Test
    public void testDetermineTypeFromClass_Date() {
        assertEquals(ParserHelper.determineTypeFromClass(LocalDate.class), BuiltInType.DATE);
    }

    @Test
    public void testDetermineTypeFromClass_Time() {
        assertEquals(ParserHelper.determineTypeFromClass(LocalTime.class), BuiltInType.TIME);
        assertEquals(ParserHelper.determineTypeFromClass(OffsetTime.class), BuiltInType.TIME);
    }

    @Test
    public void testDetermineTypeFromClass_DateTime() {
        assertEquals(ParserHelper.determineTypeFromClass(ZonedDateTime.class), BuiltInType.DATE_TIME);
        assertEquals(ParserHelper.determineTypeFromClass(OffsetDateTime.class), BuiltInType.DATE_TIME);
        assertEquals(ParserHelper.determineTypeFromClass(LocalDateTime.class), BuiltInType.DATE_TIME);
        assertEquals(ParserHelper.determineTypeFromClass(Date.class), BuiltInType.DATE_TIME);
    }

    @Test
    public void testDetermineTypeFromClass_Duration() {
        assertEquals(ParserHelper.determineTypeFromClass(Duration.class), BuiltInType.DURATION);
        assertEquals(ParserHelper.determineTypeFromClass(ChronoPeriod.class), BuiltInType.DURATION);
    }

    @Test
    public void testDetermineTypeFromClass_Boolean() {
        assertEquals(ParserHelper.determineTypeFromClass(Boolean.class), BuiltInType.BOOLEAN);
    }

    @Test
    public void testDetermineTypeFromClass_UnaryTest() {
        assertEquals(ParserHelper.determineTypeFromClass(UnaryTest.class), BuiltInType.UNARY_TEST);
    }

    @Test
    public void testDetermineTypeFromClass_Range() {
        assertEquals(ParserHelper.determineTypeFromClass(Range.class), BuiltInType.RANGE);
    }

    @Test
    public void testDetermineTypeFromClass_Function() {
        assertEquals(ParserHelper.determineTypeFromClass(FEELFunction.class), BuiltInType.FUNCTION);
    }

    @Test
    public void testDetermineTypeFromClass_List() {
        assertEquals(ParserHelper.determineTypeFromClass(List.class), BuiltInType.LIST);
    }

    @Test
    public void testDetermineTypeFromClass_Context() {
        assertEquals(ParserHelper.determineTypeFromClass(Map.class), BuiltInType.CONTEXT);
    }

    @Test
    public void testDetermineTypeFromClass_JavaBackedType() {
        assertEquals(ParserHelper.determineTypeFromClass(Object.class), JavaBackedType.of(Object.class));
    }
}