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
package org.kie.kogito.core.process.incubation.quarkus.support;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jbpm.process.instance.impl.humantask.HumanTaskHelper;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.kie.kogito.Application;
import org.kie.kogito.MappableToModel;
import org.kie.kogito.Model;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.incubation.common.*;
import org.kie.kogito.incubation.processes.*;
import org.kie.kogito.incubation.processes.services.contexts.Policy;
import org.kie.kogito.incubation.processes.services.contexts.ProcessMetaDataContext;
import org.kie.kogito.incubation.processes.services.contexts.TaskMetaDataContext;
import org.kie.kogito.incubation.processes.services.humantask.HumanTaskService;
import org.kie.kogito.internal.process.runtime.KogitoNode;
import org.kie.kogito.process.*;
import org.kie.kogito.process.Process;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;

class HumanTaskServiceImpl implements HumanTaskService {

    private final Application application;
    private final ProcessService svc;
    private final Processes processes;

    HumanTaskServiceImpl(Application application, ProcessService svc, Processes processes) {
        this.application = application;
        this.svc = svc;
        this.processes = processes;
    }

    private Process<MappableToModel<Model>> parseProcess(LocalProcessId pid) {
        return (Process<MappableToModel<Model>>) processes.processById(pid.processId());
    }

    @Override
    public ExtendedDataContext get(LocalId id, MetaDataContext meta) {
        TaskMetaDataContext metaCtx = meta.as(TaskMetaDataContext.class);
        SecurityPolicy securityPolicy = convertPolicyObject(metaCtx.policy());
        try {
            TaskIds taskIds = ProcessIdParser.select(id, TaskIds.class); // /tasks
            ProcessInstanceId instanceId = taskIds.processInstanceId();
            Process<MappableToModel<Model>> process = parseProcess(instanceId.processId());
            String processInstanceIdString = instanceId.processInstanceId();

            List<String> tasks = svc.getTasks(
                    process,
                    processInstanceIdString,
                    securityPolicy).orElseThrow().stream()
                    .map(wi -> taskIds.get(wi.getName()).instances().get(wi.getId()).asLocalUri().path())
                    .collect(Collectors.toList());
            MapDataContext mdc = MapDataContext.create();
            mdc.set("tasks", tasks);
            return ExtendedDataContext.of(mdc, EmptyDataContext.Instance);

        } catch (IllegalArgumentException e) {
            TaskInstanceId taskInstanceId = ProcessIdParser.select(id, TaskInstanceId.class); // /tasks/id

            ProcessInstanceId instanceId = taskInstanceId.taskId().processInstanceId();
            Process<MappableToModel<Model>> process = parseProcess(instanceId.processId());

            String taskInstanceIdString = taskInstanceId.taskInstanceId();
            String processInstanceIdString = instanceId.processInstanceId();
            WorkItem workItem =
                    svc.getTask(
                            process,
                            processInstanceIdString,
                            taskInstanceIdString,
                            securityPolicy, Function.identity()).orElseThrow(() -> new IllegalArgumentException("Cannot find ID " + id.asLocalUri().path()));
            return ExtendedDataContext.ofData(MapDataContext.of(workItem.getResults()));
        }
    }

    @Override
    public ExtendedDataContext create(LocalId id, DataContext dataContext) {
        TaskId taskId = ProcessIdParser.select(id, TaskId.class);
        ProcessInstanceId instanceId = taskId.processInstanceId();
        Process<MappableToModel<Model>> process = parseProcess(instanceId.processId());

        ExtendedDataContext edc = dataContext.as(ExtendedDataContext.class);
        TaskMetaDataContext mdc = edc.meta().as(TaskMetaDataContext.class);
        SecurityPolicy securityPolicy = convertPolicyObject(mdc.policy());

        WorkItem workItem = svc.signalTask(process, instanceId.processInstanceId(), taskId.taskId(), securityPolicy)
                .orElseThrow();

        return ExtendedDataContext.of(ProcessMetaDataContext.of(taskId), MapDataContext.from(workItem));
    }

    @Override
    public ExtendedDataContext abort(LocalId id, MetaDataContext metaDataContext) {
        MapDataContext mdc = metaDataContext.as(MapDataContext.class);
        mdc.set("phase", "abort");

        return transition(id, ExtendedDataContext.of(mdc, EmptyDataContext.Instance));
    }

    @Override
    public ExtendedDataContext complete(LocalId processId, DataContext dataContext) {
        ExtendedDataContext edc = dataContext.as(ExtendedDataContext.class);
        MapDataContext mdc = edc.meta().as(MapDataContext.class);
        mdc.set("phase", "complete");

        return transition(processId, ExtendedDataContext.of(mdc, edc.data()));
    }

    @Override
    public ExtendedDataContext transition(LocalId id, DataContext dataContext) {
        ExtendedDataContext edc = dataContext.as(ExtendedDataContext.class);
        TaskMetaDataContext mdc = edc.meta().as(TaskMetaDataContext.class);
        SecurityPolicy securityPolicy = convertPolicyObject(mdc.policy());
        String phase = mdc.phase();
        Objects.requireNonNull(phase, "Phase must be specified");

        TaskInstanceId taskInstanceId = ProcessIdParser.select(id, TaskInstanceId.class);

        // must validate the task id
        TaskId taskId = taskInstanceId.taskId();
        ProcessInstanceId instanceId = taskId.processInstanceId();
        Process<MappableToModel<Model>> process = parseProcess(instanceId.processId());

        Collection<KogitoNode> tasks = process.findNodes(n -> n instanceof HumanTaskNode &&
                ((HumanTaskNode) n).getWork().getParameter("TaskName").equals(taskId.taskId()));

        if (tasks.isEmpty()) {
            throw new IllegalArgumentException("No such taskId " + taskId.taskId());
        }

        String taskInstanceIdString = taskInstanceId.taskInstanceId();
        String processInstanceIdString = instanceId.processInstanceId();

        Map<String, Object> map = dataContext.as(MapDataContext.class).toMap();

        MappableToModel<Model> model = process.createModel();
        model.fromMap(map);
        Model result = svc.taskTransition(
                process,
                processInstanceIdString,
                taskInstanceIdString,
                phase,
                securityPolicy,
                model)
                .orElseThrow();

        return ExtendedDataContext.ofData(MapDataContext.of(result.toMap()));
    }

    @Override
    public ExtendedDataContext update(LocalId id, DataContext dataContext) {
        ExtendedDataContext edc = dataContext.as(ExtendedDataContext.class);
        TaskMetaDataContext mdc = edc.meta().as(TaskMetaDataContext.class);
        SecurityPolicy securityPolicy = convertPolicyObject(mdc.policy());

        TaskInstanceId taskInstanceId = ProcessIdParser.select(id, TaskInstanceId.class);

        ProcessInstanceId instanceId = taskInstanceId.taskId().processInstanceId();
        Process<MappableToModel<Model>> process = parseProcess(instanceId.processId());

        String taskInstanceIdString = taskInstanceId.taskInstanceId();
        String processInstanceIdString = instanceId.processInstanceId();

        Map<String, Object> map = dataContext.as(MapDataContext.class).toMap();

        Map<String, Object> result = UnitOfWorkExecutor.executeInUnitOfWork(
                application.unitOfWorkManager(), () -> process
                        .instances()
                        .findById(processInstanceIdString)
                        .map(pi -> {
                            pi.updateWorkItem(
                                    taskInstanceIdString,
                                    wi -> HumanTaskHelper.updateContent(wi, map), securityPolicy);
                            return pi.variables().toModel();
                        }))
                .orElseThrow().toMap();

        return ExtendedDataContext.ofData(MapDataContext.of(result));
    }

    private SecurityPolicy convertPolicyObject(Policy policy) {
        return SecurityPolicy.of(IdentityProviders.of(policy.user(), policy.groups()));
    }
}
