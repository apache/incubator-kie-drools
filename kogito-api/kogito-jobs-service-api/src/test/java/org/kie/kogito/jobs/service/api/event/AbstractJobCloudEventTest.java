/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.service.api.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.jobs.service.api.event.TestConstants.*;

abstract class AbstractJobCloudEventTest<E extends JobCloudEvent<?>> {

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
        assertThat(event.getDataSchema()).isEqualTo(DATA_SCHEMA);
    }
}
