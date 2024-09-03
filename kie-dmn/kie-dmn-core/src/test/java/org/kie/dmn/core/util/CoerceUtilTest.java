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

package org.kie.dmn.core.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;

import static org.assertj.core.api.Assertions.assertThat;

class CoerceUtilTest {

    @Test
    void coerceValueCollectionToArrayConverted() {
        Object item = "TESTED_OBJECT";
        Object value = Collections.singleton(item);
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "string",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.STRING);
        Object retrieved = CoerceUtil.coerceValue(requiredType, value);
        assertThat(retrieved).isNotNull();
        assertThat(item).isEqualTo(retrieved);
    }

    @Test
    void coerceValueCollectionToArrayNotConverted() {
        Object item = "TESTED_OBJECT";
        Object value = Collections.singleton(item);
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "string",
                                                  null,
                                                  true,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.STRING);
        Object retrieved = CoerceUtil.coerceValue(requiredType, value);
        assertThat(retrieved).isNotNull();
        assertThat(value).isEqualTo(retrieved);

        value = "TESTED_OBJECT";
        requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "string",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.STRING);
        retrieved = CoerceUtil.coerceValue(requiredType, value);
        assertThat(retrieved).isNotNull();
        assertThat(value).isEqualTo(retrieved);

        requiredType = null;
        retrieved = CoerceUtil.coerceValue(requiredType, value);
        assertThat(value).isEqualTo(retrieved);

        value = null;
        requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                         "string",
                                                         null,
                                                         false,
                                                         null,
                                                         null,
                                                         null,
                                                         BuiltInType.STRING);
        retrieved = CoerceUtil.coerceValue(requiredType, value);
        assertThat(value).isEqualTo(retrieved);

    }

    @Test
    void coerceValueDateToDateTimeConverted() {
        Object value = LocalDate.now();
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "date and time",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.DATE_TIME);
        Object retrieved = CoerceUtil.coerceValue(requiredType, value);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(ZonedDateTime.class);
        ZonedDateTime zdtRetrieved = (ZonedDateTime)retrieved;
        assertThat(value).isEqualTo(zdtRetrieved.toLocalDate());
        assertThat(ZoneOffset.UTC).isEqualTo(zdtRetrieved.getOffset());

        assertThat(0).isEqualTo(zdtRetrieved.getHour());
        assertThat(0).isEqualTo(zdtRetrieved.getMinute());
        assertThat(0).isEqualTo(zdtRetrieved.getSecond());
    }

    @Test
    void coerceValueDateToDateTimeNotConverted() {
        Object value = "TEST_OBJECT";
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "date and time",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.DATE_TIME);
        Object retrieved = CoerceUtil.coerceValue(requiredType, value);
        assertThat(retrieved).isNotNull();
        assertThat(value).isEqualTo(retrieved);
        value = LocalDate.now();
        requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "date",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.DATE);
        retrieved = CoerceUtil.coerceValue(requiredType, value);
        assertThat(retrieved).isNotNull();
        assertThat(value).isEqualTo(retrieved);
    }

    @Test
    void actualCoerceValueCollectionToArray() {
        Object item = "TESTED_OBJECT";
        Object value = Collections.singleton(item);
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "string",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.STRING);
        Object retrieved = CoerceUtil.actualCoerceValue(requiredType, value);
        assertThat(retrieved).isNotNull();
        assertThat(item).isEqualTo(retrieved);
    }

    @Test
    void actualCoerceValueDateToDateTime() {
        Object value = LocalDate.now();
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "date and time",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.DATE_TIME);
        Object retrieved = CoerceUtil.actualCoerceValue(requiredType, value);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(ZonedDateTime.class);
        ZonedDateTime zdtRetrieved = (ZonedDateTime)retrieved;
        assertThat(value).isEqualTo(zdtRetrieved.toLocalDate());
        assertThat(ZoneOffset.UTC).isEqualTo(zdtRetrieved.getOffset());
        assertThat(0).isEqualTo(zdtRetrieved.getHour());
        assertThat(0).isEqualTo(zdtRetrieved.getMinute());
        assertThat(0).isEqualTo(zdtRetrieved.getSecond());
    }

    @Test
    void actualCoerceValueNotConverted() {
        Object value = BigDecimal.valueOf(1L);
        DMNType requiredType = new SimpleTypeImpl("http://www.omg.org/spec/DMN/20180521/FEEL/",
                                                  "number",
                                                  null,
                                                  false,
                                                  null,
                                                  null,
                                                  null,
                                                  BuiltInType.NUMBER);
        Object retrieved = CoerceUtil.actualCoerceValue(requiredType, value);
        assertThat(retrieved).isNotNull();
        assertThat(value).isEqualTo(retrieved);
    }
}