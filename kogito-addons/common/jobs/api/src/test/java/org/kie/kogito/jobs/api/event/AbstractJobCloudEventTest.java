/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.api.event;

import java.net.URI;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractJobCloudEventTest<E extends JobCloudEvent<?>> {

    static final String ID = "ID";
    static final String SPEC_VERSION = "1.0";
    static final URI SOURCE = URI.create("http://localhost:8080/kogito-process");
    static final ZonedDateTime TIME = ZonedDateTime.parse("2021-11-24T18:00:00.000+01:00");
    static final String SUBJECT = "SUBJECT";
    static final String DATA_CONTENT_TYPE = "DATA_CONTENT_TYPE";
    static final String DATA_SCHEMA = "http://localhost:8080/data-schema";

    abstract E buildEvent();

    abstract String eventType();

    @Test
    void builder() {
        E event = buildEvent();
        assertFields(event);
    }

    void assertFields(E event) {
        assertThat(event.getId()).isEqualTo(ID);
        assertThat(event.getSpecVersion()).isEqualTo(SPEC_VERSION);
        assertThat(event.getSource()).isEqualTo(SOURCE);
        assertThat(event.getType()).isEqualTo(eventType());
        assertThat(event.getTime()).isEqualTo(TIME);
        assertThat(event.getSubject()).isEqualTo(SUBJECT);
        assertThat(event.getDataContentType()).isEqualTo(DATA_CONTENT_TYPE);
        assertThat(event.getDataSchema()).isEqualTo(DATA_SCHEMA);
    }
}
