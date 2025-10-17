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
package org.kie.kogito.process.migration;

import java.util.*;
import java.util.function.Supplier;

import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;

public abstract class BaseProcessInstanceMigrationResource<T> implements ProcessInstanceMigration<T> {

    private Supplier<Processes> processes;

    private Application application;

    public BaseProcessInstanceMigrationResource(Processes processes, Application application) {
        this(() -> processes, application);
    }

    public BaseProcessInstanceMigrationResource(Supplier<Processes> processes, Application application) {
        this.processes = processes;
        this.application = application;
    }

    public T doMigrateInstance(String processId, ProcessMigrationSpec migrationSpec, String processInstanceId) {
        try {
            Process<? extends Model> process = processes.get().processById(processId);
            process.instances().migrateProcessInstances(migrationSpec.getTargetProcessId(), migrationSpec.getTargetProcessVersion(), processInstanceId);
            Map<String, Object> message = new HashMap<>();
            message.put("message", processInstanceId + " instance migrated");
            message.put("processInstanceId", processInstanceId);
            return buildOkResponse(message);
        } catch (Exception e) {
            return badRequestResponse(e.getMessage());
        }
    }

    public T doMigrateAllInstances(String processId, ProcessMigrationSpec migrationSpec) {
        try {
            Process<? extends Model> process = processes.get().processById(processId);
            long numberOfProcessInstanceMigrated = process.instances().migrateAll(migrationSpec.getTargetProcessId(), migrationSpec.getTargetProcessVersion());
            Map<String, Object> message = new HashMap<>();
            message.put("message", "All instances migrated");
            message.put("numberOfProcessInstanceMigrated", numberOfProcessInstanceMigrated);
            return buildOkResponse(message);
        } catch (Exception e) {
            return badRequestResponse(e.getMessage());
        }
    }

    protected abstract <R> T buildOkResponse(R body);

    protected abstract T badRequestResponse(String message);

    protected abstract T notFoundResponse(String message);

}
