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

package org.kie.dmn.feel.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.types.BuiltInType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoerceUtilTest {

    @Test
    void coerceParameterDateToDateTimeConverted() {
        Object value = LocalDate.now();
        Object retrieved = CoerceUtil.coerceParameter(BuiltInType.DATE_TIME, value);
        assertNotNull(retrieved);
        assertTrue(retrieved instanceof ZonedDateTime);
        ZonedDateTime zdtRetrieved = (ZonedDateTime) retrieved;
        assertEquals(value, zdtRetrieved.toLocalDate());
        assertEquals(ZoneOffset.UTC, zdtRetrieved.getOffset());
        assertEquals(0, zdtRetrieved.getHour());
        assertEquals(0, zdtRetrieved.getMinute());
        assertEquals(0, zdtRetrieved.getSecond());
    }

    @Test
    void coerceParameterDateToDateTimeNotConverted() {
        Object value = "TEST_OBJECT";
        Object retrieved = CoerceUtil.coerceParameter(null, value);
        assertEquals(value, retrieved);

        value = null;
        retrieved = CoerceUtil.coerceParameter(BuiltInType.DATE, value);
        assertEquals(value, retrieved);
    }

    @Test
    void coerceParamsCollectionToArrayConverted() {
        Object item = "TESTED_OBJECT";
        Object value = Collections.singleton(item);
        Object[] actualParams1 = {value, "NOT_DATE"};
        Optional<Object[]> retrieved = CoerceUtil.coerceParams(Set.class, String.class, actualParams1, 0);
        assertNotNull(retrieved);
        assertTrue(retrieved.isPresent());
        Object[] retrievedObjects = retrieved.get();
        assertEquals(item, retrievedObjects[0]);
        assertEquals(actualParams1[1], retrievedObjects[1]);


        item = LocalDate.now();
        value = Collections.singleton(item);
        Object[] actualParams2 = {value, "NOT_DATE"};
        retrieved = CoerceUtil.coerceParams(Set.class, ZonedDateTime.class, actualParams2, 0);
        assertNotNull(retrieved);
        assertTrue(retrieved.isPresent());
        retrievedObjects = retrieved.get();
        assertTrue(retrievedObjects[0] instanceof ZonedDateTime);
        ZonedDateTime zdtRetrieved = (ZonedDateTime) retrievedObjects[0];
        assertEquals(item, zdtRetrieved.toLocalDate());
        assertEquals(ZoneOffset.UTC, zdtRetrieved.getOffset());
        assertEquals(0, zdtRetrieved.getHour());
        assertEquals(0, zdtRetrieved.getMinute());
        assertEquals(0, zdtRetrieved.getSecond());
        assertEquals(actualParams2[1], retrievedObjects[1]);
    }

    @Test
    void coerceParamsToDateTimeConverted() {
        Object value = LocalDate.now();
        Object[] actualParams = {value, "NOT_DATE"};
        Optional<Object[]> retrieved = CoerceUtil.coerceParams(LocalDate.class, ZonedDateTime.class, actualParams, 0);
        assertNotNull(retrieved);
        assertTrue(retrieved.isPresent());
        Object[] retrievedObjects = retrieved.get();
        assertTrue(retrievedObjects[0] instanceof ZonedDateTime);
        ZonedDateTime zdtRetrieved = (ZonedDateTime) retrievedObjects[0];
        assertEquals(value, zdtRetrieved.toLocalDate());
        assertEquals(ZoneOffset.UTC, zdtRetrieved.getOffset());
        assertEquals(0, zdtRetrieved.getHour());
        assertEquals(0, zdtRetrieved.getMinute());
        assertEquals(0, zdtRetrieved.getSecond());
        assertEquals(actualParams[1], retrievedObjects[1]);
    }

    @Test
    void coerceParamsNotConverted() {
        Object item = "TESTED_OBJECT";
        Object value = Collections.singleton(item);
        Object[] actualParams1 = {value, "NOT_DATE"};
        Optional<Object[]> retrieved = CoerceUtil.coerceParams(Set.class, BigDecimal.class, actualParams1, 0);
        assertNotNull(retrieved);
        assertTrue(retrieved.isEmpty());

        value = LocalDate.now();
        Object[] actualParams2 = {value, "NOT_DATE"};
        retrieved = CoerceUtil.coerceParams(LocalDate.class, String.class, actualParams2, 0);
        assertNotNull(retrieved);
        assertTrue(retrieved.isEmpty());
    }

    @Test
    void actualCoerceParameterToDateTimeConverted() {
        Object value = LocalDate.now();
        Object retrieved = CoerceUtil.actualCoerceParameter(BuiltInType.DATE_TIME, value);
        assertNotNull(retrieved);
        assertTrue(retrieved instanceof ZonedDateTime);
        ZonedDateTime zdtRetrieved = (ZonedDateTime) retrieved;
        assertEquals(value, zdtRetrieved.toLocalDate());
        assertEquals(ZoneOffset.UTC, zdtRetrieved.getOffset());
        assertEquals(0, zdtRetrieved.getHour());
        assertEquals(0, zdtRetrieved.getMinute());
        assertEquals(0, zdtRetrieved.getSecond());
    }

    @Test
    void actualCoerceParameterNotConverted() {
        Object value = "TEST_OBJECT";
        Object retrieved = CoerceUtil.actualCoerceParameter(BuiltInType.DATE_TIME, value);
        assertNotNull(retrieved);
        assertEquals(value, retrieved);

        value = LocalDate.now();
        retrieved = CoerceUtil.actualCoerceParameter(BuiltInType.DATE, value);
        assertNotNull(retrieved);
        assertEquals(value, retrieved);
    }

    @Test
    void actualCoerceParams() {
        Object value = LocalDate.now();
        Object[] actualParams = {value, "NOT_DATE"};
        Object coercedValue = BigDecimal.valueOf(1L);
        Object[] retrieved = CoerceUtil.actualCoerceParams(actualParams, coercedValue, 0);
        assertNotNull(retrieved);
        assertEquals(actualParams.length, retrieved.length);
        assertEquals(coercedValue, retrieved[0]);
        assertEquals(actualParams[1], retrieved[1]);
    }

}