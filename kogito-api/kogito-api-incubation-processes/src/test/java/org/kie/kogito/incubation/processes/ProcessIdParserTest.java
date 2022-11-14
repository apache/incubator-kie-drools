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
package org.kie.kogito.incubation.processes;

import org.junit.jupiter.api.Test;
import org.kie.kogito.incubation.common.LocalId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class ProcessIdParserTest {
    @Test
    public void testProcessId() {
        LocalProcessId processId = ProcessIdParser.parse("/processes/p", LocalProcessId.class);
        assertThat(new LocalProcessId("p")).isEqualTo(processId);
    }

    @Test
    public void testInvalidProcessId() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> ProcessIdParser.parse("/processes", LocalProcessId.class));
    }

    @Test
    public void testProcessIdPart() {
        LocalProcessId processId = ProcessIdParser.parse("/processes/p/instances/pi", LocalProcessId.class);
        assertThat(new LocalProcessId("p")).isEqualTo(processId);
    }

    @Test
    public void testProcessInstanceId() {
        ProcessInstanceId instanceId = ProcessIdParser.parse("/processes/p/instances/pi", ProcessInstanceId.class);
        assertThat(new LocalProcessId("p").instances().get("pi")).isEqualTo(instanceId);
    }

    @Test
    public void testInvalidProcessInstanceId() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> ProcessIdParser.parse("/processes/p", ProcessInstanceId.class));
    }

    @Test
    public void testTaskId() {
        TaskId taskId = ProcessIdParser.parse("/processes/p/instances/pi/tasks/t", TaskId.class);
        assertThat(new LocalProcessId("p").instances().get("pi").tasks().get("t")).isEqualTo(taskId);
    }

    @Test
    public void testCommentId() {
        String id = "/processes/p/instances/pi/tasks/t/instances/ti/comments/c";
        CommentId commentId = ProcessIdParser.parse(id, CommentId.class);
        assertThat(new LocalProcessId("p").instances().get("pi").tasks().get("t").instances().get("ti").comments().get("c")).isEqualTo(commentId);

        TaskId taskId = ProcessIdParser.parse(id, TaskId.class);
        assertThat(new LocalProcessId("p").instances().get("pi").tasks().get("t")).isEqualTo(taskId);

    }

    @Test
    public void testTaskInstanceId() {
        String id = "/processes/p/instances/pi/tasks/t/instances/ti";
        TaskInstanceId taskInstanceId = ProcessIdParser.parse(id, TaskInstanceId.class);
        assertThat(new LocalProcessId("p").instances().get("pi").tasks().get("t").instances().get("ti")).isEqualTo(taskInstanceId);

        TaskId taskId = ProcessIdParser.parse(id, TaskId.class);
        assertThat(new LocalProcessId("p").instances().get("pi").tasks().get("t")).isEqualTo(taskId);
    }

    @Test
    public void testAttachmentId() {
        String id = "/processes/p/instances/pi/tasks/t/instances/ti/attachments/a";
        AttachmentId attachmentId = ProcessIdParser.parse(id, AttachmentId.class);
        assertThat(new LocalProcessId("p").instances().get("pi").tasks().get("t").instances().get("ti").attachments().get("a")).isEqualTo(attachmentId);

        TaskId taskId = ProcessIdParser.parse(id, TaskId.class);
        assertThat(new LocalProcessId("p").instances().get("pi").tasks().get("t")).isEqualTo(taskId);
    }

    @Test
    public void testSignalId() {
        SignalId signalId = ProcessIdParser.parse("/processes/p/instances/pi/signals/s", SignalId.class);
        assertThat(new LocalProcessId("p").instances().get("pi").signals().get("s")).isEqualTo(signalId);
    }

    @Test
    public void testLocalId() {
        LocalId id = ProcessIdParser.parse("/processes/p/instances/pi/tasks/t", LocalId.class);
        assertThat(new LocalProcessId("p").instances().get("pi").tasks().get("t")).isEqualTo(id);
    }

}