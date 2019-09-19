/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.json;

import java.util.UUID;

import javax.json.JsonObject;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.event.KogitoProcessCloudEvent;
import org.kie.kogito.index.model.ProcessInstanceState;

import static javax.json.Json.createValue;
import static org.kie.kogito.index.TestUtils.getProcessCloudEvent;

public class ProcessInstanceMetaMapperTest {

    @Test
    public void testProcessInstanceMapper() {
        String processId = "travels";
        String rootProcessId = "root_travels";
        String processInstanceId = UUID.randomUUID().toString();
        String rootProcessInstanceId = UUID.randomUUID().toString();
        KogitoProcessCloudEvent event = getProcessCloudEvent(processId, processInstanceId, ProcessInstanceState.COMPLETED, rootProcessInstanceId, rootProcessId, rootProcessInstanceId);
        JsonObject json = new ProcessInstanceMetaMapper().apply(event);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(json)
                .isNotNull()
                .containsEntry("id", createValue(processInstanceId))
                .containsEntry("processId", createValue(processId))
                .containsEntry("rootProcessInstanceId", createValue(rootProcessInstanceId))
                .containsEntry("parentProcessInstanceId", createValue(rootProcessInstanceId))
                .containsEntry("rootProcessId", createValue(rootProcessId))
                .containsEntry("state", createValue(ProcessInstanceState.COMPLETED.ordinal()))
                .containsEntry("endpoint", createValue(event.getSource().toString()))
                .containsEntry("start", createValue(event.getData().getStart().toInstant().toEpochMilli()))
                .containsEntry("end", createValue(event.getData().getEnd().toInstant().toEpochMilli()));

        softly.assertAll();
    }
}
