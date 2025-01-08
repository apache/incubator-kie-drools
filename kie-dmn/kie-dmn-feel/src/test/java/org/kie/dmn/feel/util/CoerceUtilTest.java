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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.types.BuiltInType;

import static org.assertj.core.api.Assertions.assertThat;

class CoerceUtilTest {

    @Test
    void coerceParam() {
        // Coerce List to singleton
        Class<?> currentIdxActualParameterType = List.class;
        Class<?> expectedParameterType = Number.class;
        Object valueObject = 34;
        Object actualObject = List.of(valueObject);
        Optional<Object> retrieved = CoerceUtil.coerceParam(currentIdxActualParameterType, expectedParameterType, actualObject);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(valueObject);

        // Coerce single element to singleton list
        currentIdxActualParameterType = Number.class;
        expectedParameterType = List.class;
        actualObject = 34;
        retrieved = CoerceUtil.coerceParam(currentIdxActualParameterType, expectedParameterType, actualObject);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isInstanceOf(List.class);
        List lstRetrieved = (List) retrieved.get();
        assertThat(lstRetrieved).hasSize(1);
        assertThat(lstRetrieved.get(0)).isEqualTo(actualObject);

        // Coerce date to date and time
        actualObject = LocalDate.now();
        currentIdxActualParameterType = LocalDate.class;
        expectedParameterType = ZonedDateTime.class;
        retrieved = CoerceUtil.coerceParam(currentIdxActualParameterType, expectedParameterType, actualObject);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get() instanceof ZonedDateTime).isTrue();
        ZonedDateTime zdtRetrieved = (ZonedDateTime) retrieved.get();
        assertThat(zdtRetrieved.toLocalDate()).isEqualTo(actualObject);
        assertThat(zdtRetrieved.getOffset()).isEqualTo(ZoneOffset.UTC);
        assertThat(zdtRetrieved.getHour()).isEqualTo(0);
        assertThat(zdtRetrieved.getMinute()).isEqualTo(0);
        assertThat(zdtRetrieved.getSecond()).isEqualTo(0);
    }

    @Test
    void coerceParameterDateToDateTimeConverted() {
        Object value = LocalDate.now();
        Object retrieved = CoerceUtil.coerceParameter(BuiltInType.DATE_TIME, value);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(ZonedDateTime.class);
        ZonedDateTime zdtRetrieved = (ZonedDateTime) retrieved;
        assertThat(zdtRetrieved.toLocalDate()).isEqualTo(value);
        assertThat(zdtRetrieved.getOffset()).isEqualTo(ZoneOffset.UTC);
        assertThat(zdtRetrieved.getHour()).isEqualTo(0);
        assertThat(zdtRetrieved.getMinute()).isEqualTo(0);
        assertThat(zdtRetrieved.getSecond()).isEqualTo(0);
    }

    @Test
    void coerceParameterDateToDateTimeNotConverted() {
        Object value = "TEST_OBJECT";
        Object retrieved = CoerceUtil.coerceParameter(null, value);
        assertThat(retrieved).isEqualTo(value);

        value = null;
        retrieved = CoerceUtil.coerceParameter(BuiltInType.DATE, value);
        assertThat(retrieved).isEqualTo(value);
    }

    @Test
    void coerceParamsCollectionToArrayConverted() {
        Object item = "TESTED_OBJECT";
        Object value = Collections.singleton(item);
        Object[] actualParams1 = {value, "NOT_DATE"};
        Optional<Object[]> retrieved = CoerceUtil.coerceParams(Set.class, String.class, actualParams1, 0);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isPresent();
        Object[] retrievedObjects = retrieved.get();
        assertThat(retrievedObjects[0]).isEqualTo(item);
        assertThat(retrievedObjects[1]).isEqualTo(actualParams1[1]);


        item = LocalDate.now();
        value = Collections.singleton(item);
        Object[] actualParams2 = {value, "NOT_DATE"};
        retrieved = CoerceUtil.coerceParams(Set.class, ZonedDateTime.class, actualParams2, 0);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isPresent();
        retrievedObjects = retrieved.get();
        assertThat(retrievedObjects[0]).isInstanceOf(ZonedDateTime.class);
        ZonedDateTime zdtRetrieved = (ZonedDateTime) retrievedObjects[0];
        assertThat(zdtRetrieved.toLocalDate()).isEqualTo(item);
        assertThat(zdtRetrieved.getOffset()).isEqualTo(ZoneOffset.UTC);
        assertThat(zdtRetrieved.getHour()).isEqualTo(0);
        assertThat(zdtRetrieved.getMinute()).isEqualTo(0);
        assertThat(zdtRetrieved.getSecond()).isEqualTo(0);
        assertThat(retrievedObjects[1]).isEqualTo(actualParams2[1]);
    }

    @Test
    void coerceParamsToDateTimeConverted() {
        Object value = LocalDate.now();
        Object[] actualParams = {value, "NOT_DATE"};
        Optional<Object[]> retrieved = CoerceUtil.coerceParams(LocalDate.class, ZonedDateTime.class, actualParams, 0);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isPresent();
        Object[] retrievedObjects = retrieved.get();
        assertThat(retrievedObjects[0]).isInstanceOf(ZonedDateTime.class);
        ZonedDateTime zdtRetrieved = (ZonedDateTime) retrievedObjects[0];
        assertThat(zdtRetrieved.toLocalDate()).isEqualTo(value);
        assertThat(zdtRetrieved.getOffset()).isEqualTo(ZoneOffset.UTC);
        assertThat(zdtRetrieved.getHour()).isEqualTo(0);
        assertThat(zdtRetrieved.getMinute()).isEqualTo(0);
        assertThat(zdtRetrieved.getSecond()).isEqualTo(0);
        assertThat(retrievedObjects[1]).isEqualTo(actualParams[1]);
    }

    @Test
    void coerceParamsNotConverted() {
        Object item = "TESTED_OBJECT";
        Object value = Collections.singleton(item);
        Object[] actualParams1 = {value, "NOT_DATE"};
        Optional<Object[]> retrieved = CoerceUtil.coerceParams(Set.class, BigDecimal.class, actualParams1, 0);
        assertThat(retrieved).isNotNull();
         assertThat(retrieved).isEmpty();

        value = LocalDate.now();
        Object[] actualParams2 = {value, "NOT_DATE"};
        retrieved = CoerceUtil.coerceParams(LocalDate.class, String.class, actualParams2, 0);
        assertThat(retrieved).isNotNull();
         assertThat(retrieved).isEmpty();
    }

    @Test
    void actualCoerceParameterToDateTimeConverted() {
        Object value = LocalDate.now();
        Object retrieved = CoerceUtil.actualCoerceParameter(BuiltInType.DATE_TIME, value);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(ZonedDateTime.class);
        ZonedDateTime zdtRetrieved = (ZonedDateTime) retrieved;
        assertThat(zdtRetrieved.toLocalDate()).isEqualTo(value);
        assertThat(zdtRetrieved.getOffset()).isEqualTo(ZoneOffset.UTC);
        assertThat(zdtRetrieved.getHour()).isEqualTo(0);
        assertThat(zdtRetrieved.getMinute()).isEqualTo(0);
        assertThat(zdtRetrieved.getSecond()).isEqualTo(0);
    }

    @Test
    void actualCoerceParameterNotConverted() {
        Object value = "TEST_OBJECT";
        Object retrieved = CoerceUtil.actualCoerceParameter(BuiltInType.DATE_TIME, value);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(value);

        value = LocalDate.now();
        retrieved = CoerceUtil.actualCoerceParameter(BuiltInType.DATE, value);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(value);
    }

    @Test
    void actualCoerceParams() {
        Object value = LocalDate.now();
        Object[] actualParams = {value, "NOT_DATE"};
        Object coercedValue = BigDecimal.valueOf(1L);
        Object[] retrieved = CoerceUtil.actualCoerceParams(actualParams, coercedValue, 0);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSameSizeAs(actualParams);
        assertThat(retrieved[0]).isEqualTo(coercedValue);
        assertThat(retrieved[1]).isEqualTo(actualParams[1]);
    }

}