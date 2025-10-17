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
import org.kie.kogito.process.migration.BaseProcessInstanceMigrationResource;
import org.kie.kogito.process.migration.ProcessMigrationSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/management/processes")
public class ProcessInstanceMigrationRestController extends BaseProcessInstanceMigrationResource<ResponseEntity> {

    @Autowired
    @Lazy
    public ProcessInstanceMigrationRestController(Processes processes, Application application) {
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
    @PostMapping(value = "{processId}/instances/{processInstanceId}/migrate", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity migrateInstance(@PathVariable("processId") String processId, @PathVariable("processInstanceId") String processInstanceId, ProcessMigrationSpec migrationSpec) {
        return doMigrateInstance(processId, migrationSpec, processInstanceId);
    }

    @Override
    @PostMapping(value = "{processId}/migrate", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity migrateAllInstances(String processId, ProcessMigrationSpec migrationSpec) {
        return doMigrateAllInstances(processId, migrationSpec);
    }

}
