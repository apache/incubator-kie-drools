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

package org.kie.kogito.svg.rest;

import java.util.Optional;

import org.kie.kogito.svg.ProcessSvgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/svg")
public class SpringBootProcessSvgResource {

    @Autowired
    ProcessSvgService service;

    @GetMapping(value = "processes/{processId}", produces = "image/svg+xml")
    public ResponseEntity getProcessSvg(@PathVariable("processId") String processId) {
        Optional<String> processSvg = service.getProcessSvg(processId);
        if (processSvg.isPresent()) {
            return ResponseEntity.ok(processSvg.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Process with id " + processId + " not found");
        }
    }

    @GetMapping(value = "processes/{processId}/instances/{processInstanceId}", produces = "image/svg+xml")
    public ResponseEntity getExecutionPathByProcessInstanceId(@PathVariable("processId") String processId,
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Optional<String> processInstanceSvg = service.getProcessInstanceSvg(processId, processInstanceId, authHeader);
        if (processInstanceSvg.isPresent()) {
            return ResponseEntity.ok(processInstanceSvg.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Process with id " + processId + " not found");
        }
    }

    @Autowired
    protected void setProcessSvgService(ProcessSvgService service) {
        this.service = service;
    }
}
