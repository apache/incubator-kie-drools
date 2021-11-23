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

package org.jbpm.bpmn2;

import java.util.Collections;

import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

public class CollaborationTest extends JbpmBpmn2TestCase {

    @Test
    public void testBoundaryMessageCollaboration() throws Exception {
        kruntime = createKogitoProcessRuntime("collaboration/Collaboration-BoundaryMessage.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        KogitoProcessInstance pid = kruntime.startProcess("collaboration.BoundaryMessage", Collections.singletonMap("MessageId", "2"));
        kruntime.signalEvent("Message-collaboration", new Message("1", "example"), pid.getStringId());
        assertProcessInstanceActive(pid);
        kruntime.signalEvent("Message-collaboration", new Message("2", "example"), pid.getStringId());
        assertProcessInstanceCompleted(pid);
    }

    @Test
    public void testStartMessageCollaboration() throws Exception {
        kruntime = createKogitoProcessRuntime("collaboration/Collaboration-StartMessage.bpmn2");
        kruntime.signalEvent("Message-collaboration", new Message("1", "example"));
        Assertions.assertEquals(1, getNumberOfProcessInstances("collaboration.StartMessage"));
    }

    @Test
    public void testStartMessageCollaborationNoMatch() throws Exception {
        kruntime = createKogitoProcessRuntime("collaboration/Collaboration-StartMessage.bpmn2");

        kruntime.signalEvent("Message-collaboration", new Message("2", "example"));
        Assertions.assertEquals(0, getNumberOfProcessInstances("collaboration.StartMessage"));
    }

    @Test
    public void testIntermediateMessageCollaboration() throws Exception {
        kruntime = createKogitoProcessRuntime("collaboration/Collaboration-IntermediateMessage.bpmn2");
        KogitoProcessInstance pid = kruntime.startProcess("collaboration.IntermediateMessage", Collections.singletonMap("MessageId", "2"));
        kruntime.signalEvent("Message-collaboration", new Message("1", "example"), pid.getStringId());
        assertProcessInstanceActive(pid);
        kruntime.signalEvent("Message-collaboration", new Message("2", "example"), pid.getStringId());
        assertProcessInstanceCompleted(pid);
    }

    @Test
    public void testInvalidIntermediateMessageCollaboration() throws Exception {
        kruntime = createKogitoProcessRuntime("collaboration/Collaboration-IntermediateMessage.bpmn2");

        KogitoProcessInstance pid = kruntime.startProcess("collaboration.IntermediateMessage", Collections.singletonMap("MessageId", "2"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            kruntime.signalEvent("Message-collaboration", new Message(null, "example"), pid.getStringId());
        });

        assertProcessInstanceActive(pid);
    }
}