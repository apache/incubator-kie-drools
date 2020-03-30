/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package com.myspace.demo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.api.runtime.process.WorkItemNotFoundException;
import org.kie.kogito.Application;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.Sig;
import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.workitem.Policy;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/$name$")
public class $Type$Resource {

    Process<$Type$> process;

    Application application;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes =
            MediaType.APPLICATION_JSON_VALUE)
    public $Type$Output createResource_$name$(@RequestHeader HttpHeaders httpHeaders,
                                              @RequestParam(value = "businessKey", required = false) String businessKey,
                                              @RequestBody $Type$Input resource) {
        if (resource == null) {
            resource = new $Type$Input();
        }
        final $Type$Input value = resource;

        return org.kie.kogito.services.uow.UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            ProcessInstance<$Type$> pi = process.createInstance(businessKey, mapInput(value, new $Type$()));
            String startFromNode = httpHeaders.getFirst("X-KOGITO-StartFromNode");

            if (startFromNode != null) {
                pi.startFrom(startFromNode);
            } else {

                pi.start();
            }
            return getModel(pi);
        });
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<$Type$Output> getResources_$name$() {
        return process.instances().values().stream()
                .map(pi -> mapOutput(new $Type$Output(), pi.variables()))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public $Type$Output getResource_$name$(@PathVariable("id") String id) {
        return process.instances()
                .findById(id)
                .map(pi -> mapOutput(new $Type$Output(), pi.variables()))
                .orElse(null);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public $Type$Output deleteResource_$name$(@PathVariable("id") final String id) {
        return org.kie.kogito.services.uow.UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            ProcessInstance<$Type$> pi = process.instances()
                    .findById(id)
                    .orElse(null);
            if (pi == null) {
                return null;
            } else {
                pi.abort();
                return getModel(pi);
            }
        });
    }

    @PostMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public $Type$Output updateModel_$name$(@PathVariable("id") String id, @RequestBody $Type$ resource) {
        return org.kie.kogito.services.uow.UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            ProcessInstance<$Type$> pi = process.instances()
                    .findById(id)
                    .orElse(null);
            if (pi == null) {
                return null;
            } else {
                pi.updateVariables(resource);
                return mapOutput(new $Type$Output(), pi.variables());
            }
        });
    }

    @GetMapping(value = "/{id}/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getTasks_$name$(@PathVariable("id") String id,
                                               @RequestParam(value = "user", required = false) final String user,
                                               @RequestParam(value = "group", required = false) final List<String> groups) {
        return process.instances()
                .findById(id)
                .map(pi -> pi.workItems(policies(user, groups)))
                .map(l -> l.stream().collect(Collectors.toMap(WorkItem::getId, WorkItem::getName)))
                .orElse(null);
    }

    protected $Type$Output getModel(ProcessInstance<$Type$> pi) {
        if (pi.status() == ProcessInstance.STATE_ERROR && pi.error().isPresent()) {
            throw new ProcessInstanceExecutionException(pi.id(), pi.error().get().failedNodeId(), pi.error().get().errorMessage());
        }

        return mapOutput(new $Type$Output(), pi.variables());
    }

    protected Policy[] policies(String user, List<String> groups) {
        if (user == null) {
            return new Policy[0];
        }
        org.kie.kogito.auth.IdentityProvider identity = null;
        if (user != null) {
            identity = new org.kie.kogito.services.identity.StaticIdentityProvider(user, groups);
        }
        return new Policy[] {SecurityPolicy.of(identity)};
    }

    protected $Type$ mapInput($Type$Input input, $Type$ resource) {
        resource.fromMap(input.toMap());

        return resource;
    }

    protected $Type$Output mapOutput($Type$Output output, $Type$ resource) {
        output.fromMap(resource.getId(), resource.toMap());

        return output;
    }
}