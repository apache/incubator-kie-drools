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

package org.kie.kogito.taskassigning.service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.taskassigning.ClientServices;
import org.kie.kogito.taskassigning.index.service.client.DataIndexServiceClient;
import org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstance;
import org.kie.kogito.taskassigning.service.config.TaskAssigningConfig;
import org.kie.kogito.taskassigning.service.config.TaskAssigningConfigUtil;

@ApplicationScoped
public class TaskServiceConnector {

    private TaskAssigningConfig config;
    private ClientServices clientServices;
    private DataIndexServiceClient indexServiceClient;

    public TaskServiceConnector() {
        //CDI proxying
    }

    @Inject
    public TaskServiceConnector(TaskAssigningConfig config, ClientServices clientServices) {
        this.config = config;
        this.clientServices = clientServices;
    }

    public List<UserTaskInstance> findAllTasks(List<String> state, int pageSize) {
        List<UserTaskInstance> result = new ArrayList<>();
        List<UserTaskInstance> partialResult;
        Set<String> stateSet = new HashSet<>(state);
        int offset = 0;
        boolean finished = false;
        ZonedDateTime startedAfter = null;
        int lastIndex;
        indexServiceClient = ensureServiceClient();
        while (!finished) {
            partialResult = indexServiceClient.findTasks(null, startedAfter, UserTaskInstance.Field.STARTED.name(),
                                                         true, offset, pageSize);
            result.addAll(filterByState(stateSet, partialResult));
            if (partialResult.isEmpty() || partialResult.size() < pageSize) {
                finished = true;
            } else {
                offset = 0;
                lastIndex = partialResult.size() - 1;
                startedAfter = partialResult.get(lastIndex).getStarted();
                while (lastIndex >= 0 && startedAfter.equals(partialResult.get(lastIndex).getStarted())) {
                    offset++;
                    lastIndex--;
                }
                startedAfter = startedAfter.minus(1, ChronoUnit.MILLIS);
            }
        }
        return result;
    }

    private List<UserTaskInstance> filterByState(Set<String> stateSet, List<UserTaskInstance> userTaskList) {
        if (stateSet.isEmpty()) {
            return userTaskList;
        }
        return userTaskList.stream().filter(userTaskInstance -> stateSet.contains(userTaskInstance.getState())).
                collect(Collectors.toList());
    }

    private DataIndexServiceClient ensureServiceClient() {
        if (indexServiceClient == null) {
            indexServiceClient = TaskAssigningConfigUtil.createDataIndexServiceClient(clientServices, config);
        }
        return indexServiceClient;
    }
}
