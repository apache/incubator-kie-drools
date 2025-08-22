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
package org.kie.kogito.app.jobs.integregations;

import org.kie.kogito.app.jobs.api.JobExecutor;
import org.kie.kogito.app.jobs.impl.JobDetailsHelper;
import org.kie.kogito.jobs.descriptors.ProcessJobDescription;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.SignalFactory;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.kie.kogito.timer.TimerInstance;
import org.kie.kogito.uow.UnitOfWorkManager;

public class ProcessJobExecutor implements JobExecutor {
    public static final String SIGNAL = "timerTriggered";

    private Processes processes;

    private UnitOfWorkManager uom;

    public ProcessJobExecutor(Processes processes, UnitOfWorkManager unitOfWorkManager) {
        this.processes = processes;
        this.uom = unitOfWorkManager;
    }

    @Override
    public boolean accept(JobDetails jobDetails) {
        return JobDetailsHelper.extractJobDescription(jobDetails) instanceof ProcessJobDescription;
    }

    @Override
    public void execute(JobDetails jobDetails) {
        ProcessJobDescription processJobDescription = (ProcessJobDescription) JobDetailsHelper.extractJobDescription(jobDetails);

        UnitOfWorkExecutor.executeInUnitOfWork(uom, () -> {
            Process<?> processDefinition = processes.processById(processJobDescription.processId());
            if (processDefinition == null) {
                return null;
            }
            processDefinition.send(SignalFactory.of(SIGNAL, TimerInstance.with(jobDetails.getId(), jobDetails.getId(), -1)));
            return null;
        });
    }

}
