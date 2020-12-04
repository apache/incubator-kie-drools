/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.management.springboot;

import java.util.Optional;

import org.kie.kogito.Application;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.Sig;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.kie.kogito.timer.TimerInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management/jobs")
public class CallbackJobsServiceResource {

    @Autowired
    Processes processes;

    @Autowired
    Application application;

    @PostMapping(value = "{processId}/instances/{processInstanceId}/timers/{timerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity triggerTimer(@PathVariable("processId") String processId,
                                       @PathVariable("processInstanceId") String processInstanceId,
                                       @PathVariable("timerId") String timerId,
                                       @RequestParam(value = "limit", defaultValue = "0", required = false) Integer limit) {
        if (processId == null || processInstanceId == null) {
            return ResponseEntity.badRequest().body("Process id and Process instance id must be  given");
        }

        Process<?> process = processes.processById(processId);
        if (process == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Process with id " + processId + " not found");
        }

        return UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            Optional<? extends ProcessInstance<?>> processInstanceFound = process.instances().findById(processInstanceId);
            if (processInstanceFound.isPresent()) {
                ProcessInstance<?> processInstance = processInstanceFound.get();
                String[] ids = timerId.split("_");
                processInstance.send(Sig.of("timerTriggered", TimerInstance.with(Long.parseLong(ids[1]), timerId, limit)));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Process instance with id " + processInstanceId + " not found");
            }
            return ResponseEntity.ok().build();
        });
    }
}