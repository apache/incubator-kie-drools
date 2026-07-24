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
package org.kie.kogito.jobs.embedded;

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.descriptors.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.descriptors.UserTaskInstanceJobDescription;
import org.kie.kogito.jobs.service.api.Recipient;
import org.kie.kogito.jobs.service.exception.JobExecutionException;
import org.kie.kogito.jobs.service.executor.JobExecutor;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.RecipientInstance;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;
import org.kie.kogito.services.jobs.impl.TriggerJobCommand;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.UserTasks;

import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import static org.kie.kogito.services.uow.UnitOfWorkExecutor.executeInUnitOfWork;

@ApplicationScoped
@Alternative
public class EmbeddedJobExecutor implements JobExecutor {

    @Inject
    Instance<Processes> processes;

    @Inject
    Instance<UserTasks> userTasks;

    @Inject
    Application application;

    @Override
    public Uni<JobExecutionResponse> execute(JobDetails jobDetails) {
        RecipientInstance recipientModel = (RecipientInstance) jobDetails.getRecipient();
        InVMRecipient recipient = (InVMRecipient) recipientModel.getRecipient();
        JobDescription jobDescription = recipient.getPayload().getData();
        if (jobDescription instanceof ProcessInstanceJobDescription processInstanceJobDescription && processes.isResolvable()) {
            return processJobDescription(jobDetails, processInstanceJobDescription);
        } else if (jobDescription instanceof UserTaskInstanceJobDescription userTaskInstanceJobDescription && userTasks.isResolvable()) {
            return processJobDescription(jobDetails, userTaskInstanceJobDescription);
        }

        return Uni.createFrom().item(
                JobExecutionResponse.builder()
                        .code("401")
                        .jobId(jobDetails.getId())
                        .now()
                        .message("job cannot be processed")
                        .build());
    }

    private Uni<JobExecutionResponse> processJobDescription(JobDetails jobDetails, UserTaskInstanceJobDescription userTaskInstanceJobDescription) {
        Supplier<Void> execute = () -> executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            Optional<UserTaskInstance> userTaskInstance = userTasks.get().instances().findById(userTaskInstanceJobDescription.userTaskInstanceId());
            if (userTaskInstance.isEmpty()) {
                return null;
            }
            UserTaskInstance instance = userTaskInstance.get();
            instance.trigger(userTaskInstanceJobDescription);
            return null;
        });

        return Uni.createFrom().item(execute)
                .onFailure()
                .transform(
                        unexpected -> new JobExecutionException(jobDetails.getId(), "Unexpected error when executing Embedded request for job: " + jobDetails.getId() + ". " + unexpected.getMessage(),
                                unexpected))
                .onItem()
                .transform(res -> JobExecutionResponse.builder()
                        .message("Embedded job executed")
                        .code(String.valueOf(200))
                        .now()
                        .jobId(jobDetails.getId())
                        .build());

    }

    private Uni<JobExecutionResponse> processJobDescription(JobDetails jobDetails, ProcessInstanceJobDescription processInstanceJobDescription) {
        String timerId = processInstanceJobDescription.timerId();
        String processInstanceId = processInstanceJobDescription.processInstanceId();
        Optional<Process<? extends Model>> process = processes.get().processByProcessInstanceId(processInstanceId);
        if (process.isEmpty()) {
            return Uni.createFrom().item(
                    JobExecutionResponse.builder()
                            .code("401")
                            .jobId(jobDetails.getId())
                            .now()
                            .message("job does not belong to this container")
                            .build());
        }

        Integer limit = jobDetails.getRetries();

        Supplier<Boolean> execute = () -> executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            TriggerJobCommand command = new TriggerJobCommand(processInstanceId, jobDetails.getCorrelationId(), timerId, limit, process.get(), application.unitOfWorkManager());
            return command.execute();
        });

        return Uni.createFrom()
                .item(execute)
                .onFailure()
                .transform(
                        unexpected -> new JobExecutionException(jobDetails.getId(), "Unexpected error when executing Embedded request for job: " + jobDetails.getId() + ". " + unexpected.getMessage(),
                                unexpected))
                .onItem()
                .transform(res -> JobExecutionResponse.builder()
                        .message("Embedded job executed")
                        .code(String.valueOf(200))
                        .now()
                        .jobId(jobDetails.getId())
                        .build());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<? extends Recipient> type() {
        return InVMRecipient.class;
    }

}
