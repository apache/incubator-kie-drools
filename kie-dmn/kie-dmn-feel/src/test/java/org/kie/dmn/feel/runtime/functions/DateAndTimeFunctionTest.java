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
package org.kie.dmn.feel.runtime.functions;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import org.junit.jupiter.api.Test;

class DateAndTimeFunctionTest {

    private final DateAndTimeFunction dateTimeFunction = DateAndTimeFunction.INSTANCE;

    @Test
    void invokeFromString() {
        FEELFnResult<TemporalAccessor> retrievedResult = dateTimeFunction.invoke("2017-08-10T10:20:00@Europe/Paris");
        assertThat(retrievedResult).isNotNull();
        assertThat(retrievedResult.isRight()).isTrue();
        TemporalAccessor retrieved = retrievedResult.getOrElse(null);
        assertThat(retrieved).isNotNull().isInstanceOf(ZonedDateTime.class);
        ZonedDateTime retrievedZonedDateTime = (ZonedDateTime) retrieved;
        assertThat(retrievedZonedDateTime.getYear()).isEqualTo(2017);
        assertThat(retrievedZonedDateTime.getMonthValue()).isEqualTo(8);
        assertThat(retrievedZonedDateTime.getDayOfMonth()).isEqualTo(10);
        assertThat(retrievedZonedDateTime.getHour()).isEqualTo(10);
        assertThat(retrievedZonedDateTime.getMinute()).isEqualTo(20);
        assertThat(retrievedZonedDateTime.getSecond()).isZero();
        assertThat(retrievedZonedDateTime.getZone()).isEqualTo(ZoneId.of("Europe/Paris"));
    }
}
