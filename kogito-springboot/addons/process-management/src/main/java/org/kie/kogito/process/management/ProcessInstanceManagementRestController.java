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
package org.kie.kogito.process.management;

import org.kie.kogito.Application;
import org.kie.kogito.process.Processes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/management/processes")
public class ProcessInstanceManagementRestController extends BaseProcessInstanceManagementResource<ResponseEntity> {

    @Autowired
    @Lazy
    public ProcessInstanceManagementRestController(Processes processes, Application application) {
        super(processes, application);
    }

    @Override
    public <R> ResponseEntity buildOkResponse(R body) {
        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity badRequestResponse(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @Override
    public ResponseEntity notFoundResponse(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    @Override
    @GetMapping(value = "{processId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getProcessInfo(@PathVariable("processId") String processId) {
        return doGetProcessInfo(processId);
    }

    @Override
    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getProcesses() {
        return doGetProcesses();
    }

    @Override
    @GetMapping(value = "{processId}/nodes", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getProcessNodes(@PathVariable("processId") String processId) {
        return doGetProcessNodes(processId);
    }

    @Override
    @PostMapping(value = "{processId}/instances/{processInstanceId}/migrate", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity migrateInstance(@PathVariable("processId") String processId, @PathVariable("processInstanceId") String processInstanceId, ProcessMigrationSpec migrationSpec) {
        return doMigrateInstance(processId, migrationSpec, processInstanceId);
    }

    @Override
    @PostMapping(value = "{processId}/migrate", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity migrateAllInstances(String processId, ProcessMigrationSpec migrationSpec) {
        return doMigrateAllInstances(processId, migrationSpec);
    }

    @Override
    @GetMapping(value = "{processId}/instances/{processInstanceId}/error", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getInstanceInError(@PathVariable("processId") String processId,
            @PathVariable("processInstanceId") String processInstanceId) {
        return doGetInstanceInError(processId, processInstanceId);
    }

    @Override
    @GetMapping(value = "{processId}/instances/{processInstanceId}/nodeInstances", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getWorkItemsInProcessInstance(@PathVariable("processId") String processId, @PathVariable("processInstanceId") String processInstanceId) {
        return doGetWorkItemsInProcessInstance(processId, processInstanceId);
    }

    @Override
    @GetMapping(value = "{processId}/instances/{processInstanceId}/timers", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getProcessInstanceTimers(@PathVariable("processId") String processId, @PathVariable("processInstanceId") String processInstanceId) {
        return doGetProcessInstanceTimers(processId, processInstanceId);
    }

    @Override
    @PostMapping(value = "{processId}/instances/{processInstanceId}/retrigger", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity retriggerInstanceInError(@PathVariable("processId") String processId, @PathVariable("processInstanceId") String processInstanceId) {
        return doRetriggerInstanceInError(processId, processInstanceId);
    }

    @Override
    @PostMapping(value = "{processId}/instances/{processInstanceId}/skip", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity skipInstanceInError(@PathVariable("processId") String processId, @PathVariable("processInstanceId") String processInstanceId) {
        return doSkipInstanceInError(processId, processInstanceId);
    }

    @Override
    @PostMapping(value = "{processId}/instances/{processInstanceId}/nodes/{nodeId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity triggerNodeInstanceId(@PathVariable("processId") String processId, @PathVariable("processInstanceId") String processInstanceId, @PathVariable("nodeId") String nodeId) {
        return doTriggerNodeInstanceId(processId, processInstanceId, nodeId);
    }

    @Override
    @PostMapping(value = "{processId}/instances/{processInstanceId}/nodeInstances/{nodeInstanceId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity retriggerNodeInstanceId(@PathVariable("processId") String processId, @PathVariable("processInstanceId") String processInstanceId,
            @PathVariable("nodeInstanceId") String nodeInstanceId) {
        return doRetriggerNodeInstanceId(processId, processInstanceId, nodeInstanceId);
    }

    @Override
    @DeleteMapping(value = "{processId}/instances/{processInstanceId}/nodeInstances/{nodeInstanceId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity cancelNodeInstanceId(@PathVariable("processId") String processId, @PathVariable("processInstanceId") String processInstanceId,
            @PathVariable("nodeInstanceId") String nodeInstanceId) {
        return doCancelNodeInstanceId(processId, processInstanceId, nodeInstanceId);
    }

    @Override
    @GetMapping(value = "{processId}/instances/{processInstanceId}/nodeInstances/{nodeInstanceId}/timers", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getNodeInstanceTimers(@PathVariable("processId") String processId, @PathVariable("processInstanceId") String processInstanceId,
            @PathVariable("nodeInstanceId") String nodeInstanceId) {
        return doGetNodeInstanceTimers(processId, processInstanceId, nodeInstanceId);
    }

    @Override
    @DeleteMapping(value = "{processId}/instances/{processInstanceId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity cancelProcessInstanceId(@PathVariable("processId") String processId, @PathVariable("processInstanceId") String processInstanceId) {
        return doCancelProcessInstanceId(processId, processInstanceId);
    }

}
