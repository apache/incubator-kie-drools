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

package org.kie.kogito.taskassigning.process.service.client.impl.mp;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.kogito.taskassigning.process.service.client.ProcessServiceClient;

public class ProcessServiceClientMP implements ProcessServiceClient {

    private ProcessServiceClientRest client;

    public ProcessServiceClientMP(ProcessServiceClientRest client) {
        this.client = client;
    }

    @Override
    public Set<String> getAvailablePhases(String processId, String processInstanceId, String taskId, String workItemId, String user, List<String> groups) {
        HashSet<String> result = new HashSet<>();
        TaskSchema schema = client.getTaskSchema(processId, processInstanceId, taskId, workItemId, user, groups);
        if (schema != null && schema.getPhases() != null) {
            result.addAll(schema.getPhases());
        }
        return result;
    }

    @Override
    public void transitionTask(String processId, String processInstanceId, String taskId, String workItemId, String phase, String user, List<String> groups) {
        client.transitionTask(processId, processInstanceId, taskId, workItemId, phase, user, groups, "{}");
    }

    @Override
    public void close() throws IOException {
        client.close();
    }
}
