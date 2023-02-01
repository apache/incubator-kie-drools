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

import org.kie.kogito.jobs.service.api.JobLookupId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.jobs.service.api.event.TestConstants.*;

class DeleteJobEventTest extends AbstractJobCloudEventTest<DeleteJobEvent> {

    @Override
    DeleteJobEvent buildEvent() {
        return DeleteJobEvent.builder()
                .id(ID)
                .source(SOURCE)
                .dataSchema(DATA_SCHEMA)
                .time(TIME)
                .subject(SUBJECT)
                .lookupId(JobLookupId.fromCorrelationId(CORRELATION_ID))
                .build();
    }

    @Override
    String eventType() {
        return DeleteJobEvent.TYPE;
    }

    @Override
    void assertFields(DeleteJobEvent event) {
        super.assertFields(event);
        JobLookupId lookupId = event.getData();
        assertThat(lookupId.getCorrelationId()).isNotNull();
        assertThat(lookupId.getCorrelationId()).isEqualTo(CORRELATION_ID);
    }
}
