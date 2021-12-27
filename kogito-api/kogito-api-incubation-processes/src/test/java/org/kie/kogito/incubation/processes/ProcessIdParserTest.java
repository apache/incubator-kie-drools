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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProcessIdParserTest {
    @Test
    public void testProcessId() {
        LocalProcessId processId = ProcessIdParser.parse("/processes/p", LocalProcessId.class);
        assertEquals(processId, new LocalProcessId("p"));
    }

    @Test
    public void testInvalidProcessId() {
        assertThrows(IllegalArgumentException.class,
                () -> ProcessIdParser.parse("/processes", LocalProcessId.class));
    }

    @Test
    public void testProcessIdPart() {
        LocalProcessId processId = ProcessIdParser.parse("/processes/p/instances/pi", LocalProcessId.class);
        assertEquals(processId, new LocalProcessId("p"));
    }

    @Test
    public void testProcessInstanceId() {
        ProcessInstanceId instanceId = ProcessIdParser.parse("/processes/p/instances/pi", ProcessInstanceId.class);
        assertEquals(instanceId, new LocalProcessId("p").instances().get("pi"));
    }

    @Test
    public void testInvalidProcessInstanceId() {
        assertThrows(IllegalArgumentException.class,
                () -> ProcessIdParser.parse("/processes/p", ProcessInstanceId.class));
    }

    @Test
    public void testTaskId() {
        TaskId taskId = ProcessIdParser.parse("/processes/p/instances/pi/tasks/t", TaskId.class);
        assertEquals(taskId, new LocalProcessId("p").instances().get("pi").tasks().get("t"));
    }

    @Test
    public void testCommentId() {
        String id = "/processes/p/instances/pi/tasks/t/instances/ti/comments/c";
        CommentId commentId = ProcessIdParser.parse(id, CommentId.class);
        assertEquals(commentId, new LocalProcessId("p").instances().get("pi").tasks().get("t").instances().get("ti").comments().get("c"));

        TaskId taskId = ProcessIdParser.parse(id, TaskId.class);
        assertEquals(taskId, new LocalProcessId("p").instances().get("pi").tasks().get("t"));

    }

    @Test
    public void testTaskInstanceId() {
        String id = "/processes/p/instances/pi/tasks/t/instances/ti";
        TaskInstanceId taskInstanceId = ProcessIdParser.parse(id, TaskInstanceId.class);
        assertEquals(taskInstanceId, new LocalProcessId("p").instances().get("pi").tasks().get("t").instances().get("ti"));

        TaskId taskId = ProcessIdParser.parse(id, TaskId.class);
        assertEquals(taskId, new LocalProcessId("p").instances().get("pi").tasks().get("t"));
    }

    @Test
    public void testAttachmentId() {
        String id = "/processes/p/instances/pi/tasks/t/instances/ti/attachments/a";
        AttachmentId attachmentId = ProcessIdParser.parse(id, AttachmentId.class);
        assertEquals(attachmentId, new LocalProcessId("p").instances().get("pi").tasks().get("t").instances().get("ti").attachments().get("a"));

        TaskId taskId = ProcessIdParser.parse(id, TaskId.class);
        assertEquals(taskId, new LocalProcessId("p").instances().get("pi").tasks().get("t"));
    }

    @Test
    public void testSignalId() {
        SignalId signalId = ProcessIdParser.parse("/processes/p/instances/pi/signals/s", SignalId.class);
        assertEquals(signalId, new LocalProcessId("p").instances().get("pi").signals().get("s"));
    }

    @Test
    public void testLocalId() {
        LocalId id = ProcessIdParser.parse("/processes/p/instances/pi/tasks/t", LocalId.class);
        assertEquals(id, new LocalProcessId("p").instances().get("pi").tasks().get("t"));
    }

}