/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.task.management;

import java.util.List;

import org.kie.kogito.auth.IdentityProviderFactory;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.task.management.service.TaskInfo;
import org.kie.kogito.task.management.service.TaskManagementOperations;
import org.kie.kogito.task.management.service.TaskManagementService;
import org.kie.kogito.usertask.UserTaskConfig;
import org.kie.kogito.usertask.UserTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/management/usertasks/")
public class TaskManagementRestController {

    @Autowired
    IdentityProviderFactory identityProviderFactory;

    TaskManagementOperations taskService;

    @Autowired
    public TaskManagementRestController(UserTasks userTasks, UserTaskConfig userTaskConfig, ProcessConfig processConfig) {
        this.taskService = new TaskManagementService(userTasks, userTaskConfig, processConfig);
    }

    @PutMapping(value = "{taskId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateTask(
            @PathVariable("taskId") String taskId,
            @RequestParam(value = "user", required = false) String user,
            @RequestParam(value = "group", required = false) List<String> groups,
            @RequestBody TaskInfo taskInfo) {
        taskService.updateTask(taskId, taskInfo, true, identityProviderFactory.getIdentity(user, groups));
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "{taskId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskInfo> partialUpdateTask(
            @PathVariable("taskId") String taskId,
            @RequestParam(value = "user", required = false) String user,
            @RequestParam(value = "group", required = false) List<String> groups,
            @RequestBody TaskInfo taskInfo) {
        return ResponseEntity.ok(taskService.updateTask(taskId, taskInfo, false, identityProviderFactory.getIdentity(user, groups)));
    }

    @GetMapping(value = "{taskId}", produces = APPLICATION_JSON_VALUE)
    public TaskInfo getTask(
            @PathVariable("taskId") String taskId,
            @RequestParam(value = "user", required = false) String user,
            @RequestParam(value = "group", required = false) List<String> groups) {
        return taskService.getTask(taskId);
    }
}
