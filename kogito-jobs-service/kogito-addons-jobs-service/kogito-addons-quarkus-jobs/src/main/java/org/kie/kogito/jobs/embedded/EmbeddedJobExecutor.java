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

import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.jobs.service.api.Recipient;
import org.kie.kogito.jobs.service.exception.JobExecutionException;
import org.kie.kogito.jobs.service.executor.JobExecutor;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.RecipientInstance;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;
import org.kie.kogito.services.jobs.impl.TriggerJobCommand;

import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

@ApplicationScoped
@Alternative
public class EmbeddedJobExecutor implements JobExecutor {

    @Inject
    Processes processes;

    @Inject
    Application application;

    @Override
    public Uni<JobExecutionResponse> execute(JobDetails jobDetails) {

        String correlationId = jobDetails.getCorrelationId();
        RecipientInstance recipientModel = (RecipientInstance) jobDetails.getRecipient();
        InVMRecipient recipient = (InVMRecipient) recipientModel.getRecipient();
        String timerId = recipient.getPayload().getData().timerId();
        String processId = recipient.getPayload().getData().processId();
        Process<? extends Model> process = processes.processById(processId);
        String processInstanceId = recipient.getPayload().getData().processInstanceId();
        Integer limit = jobDetails.getRetries();

        TriggerJobCommand command = new TriggerJobCommand(processInstanceId, correlationId, timerId, limit, process, application.unitOfWorkManager());

        return Uni.createFrom().item(command::execute)
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
