/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.remote.ejb.test.process;

import java.util.List;

import org.assertj.core.api.Assertions;

import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.jbpm.remote.ejb.test.TestKjars;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.junit.Test;

import org.kie.internal.query.QueryContext;

public class EGetNodeInstanceTest extends RemoteEjbTest {

    @Test()
    public void getNodeInstanceForWorkItem() {
        Long pid = ejb.startProcess("org.jboss.qa.bpms.HumanTask");

        Long workItemId = null;
        List<NodeInstanceDesc> nodeList = ejb.getProcessInstanceHistoryActive(pid, new QueryContext());
        for (NodeInstanceDesc node : nodeList) {
            if (node.getName().equals("Hello")) {
                workItemId = node.getWorkItemId();
            }
        }

        if (workItemId == null) {
            throw new RuntimeException("Work item Id not found.");
        }

        NodeInstanceDesc nodeDesc = ejb.getNodeInstanceForWorkItem(workItemId);

        Assertions.assertThat(nodeDesc).isNotNull();
        Assertions.assertThat(nodeDesc.getDeploymentId()).isEqualTo(TestKjars.INTEGRATION.getGav());
        Assertions.assertThat(nodeDesc.getProcessInstanceId()).isEqualTo(pid);
        Assertions.assertThat(nodeDesc.getNodeId()).isEqualTo("_E4906EEE-6F73-4A8A-9E8E-E046EE35C10F");
        Assertions.assertThat(nodeDesc.getName()).isEqualTo("Hello");
        Assertions.assertThat(nodeDesc.getNodeType()).isEqualTo("HumanTaskNode");
        Assertions.assertThat(nodeDesc.isCompleted()).isFalse();
    }

}
