/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.io.jaxb;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

class OffsetDateTimeJaxbAdapterTest {

    private final OffsetDateTimeJaxbAdapter offsetDateTimeJaxbAdapter = new OffsetDateTimeJaxbAdapter();

    @Test
    public void unmarshall() {
        OffsetDateTime offsetDateTime = offsetDateTimeJaxbAdapter.unmarshal("2020-01-01T12:00:05.1+02:00");
        assertThat(offsetDateTime).isEqualTo(OffsetDateTime.of(2020, 1, 1, 12, 0, 05, 100000000, ZoneOffset.ofHours(2)));
    }

    @Test
    public void nullOrEmpty_shouldUnmarshallAsNull() {
        assertThat(offsetDateTimeJaxbAdapter.unmarshal(null)).isNull();
    }

    @Test
    public void marshall() {
        String offsetDateTimeString =
                offsetDateTimeJaxbAdapter.marshal(OffsetDateTime.of(2020, 1, 1, 12, 0, 05, 100000000, ZoneOffset.ofHours(2)));
        assertThat(offsetDateTimeString).isEqualTo("2020-01-01T12:00:05.1+02:00");
    }
}