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
import org.kie.kogito.jobs.descriptors.UserTaskInstanceJobDescription;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.UserTasks;

public class UserTaskInstanceJobExecutor implements JobExecutor {
    public static final String SIGNAL = "timerTriggered";

    private UserTasks userTasks;

    private UnitOfWorkManager uom;

    public UserTaskInstanceJobExecutor(UserTasks userTasks, UnitOfWorkManager unitOfWorkManager) {
        this.userTasks = userTasks;
        this.uom = unitOfWorkManager;
    }

    @Override
    public boolean accept(JobDetails jobDetails) {
        return JobDetailsHelper.extractJobDescription(jobDetails) instanceof UserTaskInstanceJobDescription;
    }

    @Override
    public void execute(JobDetails jobDetails) {
        UserTaskInstanceJobDescription userTaskInstanceJobDescription = (UserTaskInstanceJobDescription) JobDetailsHelper.extractJobDescription(jobDetails);

        UnitOfWorkExecutor.executeInUnitOfWork(uom, () -> {
            UserTaskInstance userTaskInstance = userTasks.instances().findById(userTaskInstanceJobDescription.userTaskInstanceId()).get();
            userTaskInstance.trigger(userTaskInstanceJobDescription);
            return null;
        });
    }

}
