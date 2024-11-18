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
package org.kie.kogito.jobs.management.springboot;

import java.util.Optional;

import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.jobs.api.JobCallbackPayload;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;
import org.kie.kogito.services.jobs.impl.TriggerJobCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.kie.kogito.jobs.JobDescription.JOBS_CALLBACK_URI;
import static org.kie.kogito.jobs.api.JobCallbackResourceDef.JOBS_CALLBACK_POST_URI;
import static org.kie.kogito.jobs.api.JobCallbackResourceDef.LIMIT;
import static org.kie.kogito.jobs.api.JobCallbackResourceDef.LIMIT_DEFAULT_VALUE;
import static org.kie.kogito.jobs.api.JobCallbackResourceDef.PROCESS_ID;
import static org.kie.kogito.jobs.api.JobCallbackResourceDef.PROCESS_INSTANCE_ID;
import static org.kie.kogito.jobs.api.JobCallbackResourceDef.TIMER_ID;

@RestController
@RequestMapping(JOBS_CALLBACK_URI)
public class CallbackJobsServiceResource {

    @Autowired
    Processes processes;

    @Autowired
    Application application;

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping(value = JOBS_CALLBACK_POST_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> triggerTimer(@PathVariable(PROCESS_ID) String processId,
            @PathVariable(PROCESS_INSTANCE_ID) String processInstanceId,
            @PathVariable(TIMER_ID) String timerId,
            @RequestParam(value = LIMIT, defaultValue = LIMIT_DEFAULT_VALUE, required = false) Integer limit,
            @RequestBody String payload) {
        if (processId == null || processInstanceId == null) {
            return ResponseEntity.badRequest().body("Process id and Process instance id must be  given");
        }

        Optional<Process<? extends Model>> process = processes.processByProcessInstanceId(processInstanceId);
        if (process.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Process with id " + processId + " not found");
        }

        String correlationId = null;
        if (payload != null && !payload.isBlank()) {
            try {
                JobCallbackPayload jobPayload = objectMapper.readValue(payload, JobCallbackPayload.class);
                correlationId = jobPayload.getCorrelationId();
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload: " + payload + ". " + e.getMessage());
            }
        }

        return new TriggerJobCommand(processInstanceId, correlationId, timerId, limit, process.get(), application.unitOfWorkManager()).execute()
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Process instance with id " + processInstanceId + " not found");

    }
}
