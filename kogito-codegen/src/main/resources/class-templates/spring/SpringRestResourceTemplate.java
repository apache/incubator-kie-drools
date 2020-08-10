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
import org.jbpm.util.JsonSchemaUtil;
import org.kie.kogito.Application;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.impl.Sig;
import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.workitem.Policy;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.kie.kogito.services.identity.StaticIdentityProvider;
import org.kie.kogito.auth.IdentityProvider;
import org.jbpm.process.instance.impl.humantask.HumanTaskTransition;

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
import org.springframework.http.ResponseEntity;
import org.springframework.http.Status;

@RestController
@RequestMapping("/$name$")
public class $Type$Resource {

    Process<$Type$> process;

    Application application;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public $Type$Output createResource_$name$(@RequestHeader HttpHeaders httpHeaders,
                                              @RequestParam(value = "businessKey", required = false) String businessKey,
                                              @RequestBody $Type$Input resource) {
        return UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            $Type$Input inputModel = resource != null ? resource : new $Type$Input();
            ProcessInstance<$Type$> pi = process.createInstance(businessKey, inputModel.toModel());
            String startFromNode = httpHeaders.getHeaderString("X-KOGITO-StartFromNode");
            if (startFromNode != null) {
                pi.startFrom(startFromNode);
            } else {
                pi.start();
            }
            return pi.checkError().variables().toOutput();
        });

    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<$Type$Output> getResources_$name$() {
        return process.instances()
                      .findById(id)
                      .map(pi -> pi.variables().toOutput())
                      .orElse(null);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public $Type$Output getResource_$name$(@PathVariable("id") String id) {
        return process.instances()
                      .findById(id, ProcessInstanceReadMode.READ_ONLY)
                      .map(pi -> pi.variables().toOutput())
                      .orElse(null);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public $Type$Output deleteResource_$name$(@PathVariable("id") final String id) {
        return UnitOfWorkExecutor.executeInUnitOfWork(
                                                      application.unitOfWorkManager(),
                                                      () -> process
                                                                   .instances()
                                                                   .findById(id)
                                                                   .map(pi -> {
                                                                       pi.abort();
                                                                       return pi.checkError().variables().toOutput();
                                                                   })
                                                                   .orElse(null));
    }

    @PostMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE,
                 consumes = MediaType.APPLICATION_JSON_VALUE)
    public $Type$Output updateModel_$name$(@PathVariable("id") String id, @RequestBody $Type$ resource) {
        return UnitOfWorkExecutor.executeInUnitOfWork(
                                                      application.unitOfWorkManager(),
                                                      () -> process
                                                                   .instances()
                                                                   .findById(id)
                                                                   .map(pi -> pi
                                                                                .updateVariables(resource)
                                                                                .toOutput())
                                                                   .orElse(null));
    }

    @GetMapping(value = "/{id}/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getTasks_$name$(@PathVariable("id") String id,
                                               @RequestParam(value = "user", required = false) final String user,
                                               @RequestParam(value = "group",
                                                             required = false) final List<String> groups) {
        return process.instances()
                      .findById(id, ProcessInstanceReadMode.READ_ONLY)
                      .map(pi -> pi.workItems(policies(user, groups)))
                      .map(l -> l.stream().collect(Collectors.toMap(WorkItem::getId, WorkItem::getName)))
                      .orElse(null);
    }

    protected Policy[] policies(String user, List<String> groups) {
        if (user == null) {
            return new Policy[0];
        }
        IdentityProvider identity = null;
        if (user != null) {
            identity = new StaticIdentityProvider(user, groups);
        }
        return new Policy[]{SecurityPolicy.of(identity)};
    }
}
