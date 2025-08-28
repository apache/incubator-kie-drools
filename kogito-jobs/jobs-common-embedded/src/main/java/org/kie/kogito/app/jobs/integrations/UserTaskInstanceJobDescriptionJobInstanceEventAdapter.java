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
package org.kie.kogito.app.jobs.integrations;

import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.api.JobBuilder;
import org.kie.kogito.jobs.descriptors.UserTaskInstanceJobDescription;
import org.kie.kogito.jobs.service.model.JobDetails;

public class UserTaskInstanceJobDescriptionJobInstanceEventAdapter extends AbstractJobDescriptionJobInstanceEventAdapter {

    public UserTaskInstanceJobDescriptionJobInstanceEventAdapter(String serviceURL) {
        super(serviceURL);
    }

    @Override
    public boolean accept(JobDetails jobDetails) {
        return extractJobDescription(jobDetails) instanceof UserTaskInstanceJobDescription;
    }

    @Override
    protected void doAdaptPayload(JobBuilder jobBuilder, JobDescription jobDescription) {
        if (jobDescription instanceof UserTaskInstanceJobDescription userTaskInstanceJobDescription) {
            jobBuilder.processInstanceId(userTaskInstanceJobDescription.processInstanceId());
            jobBuilder.processId(userTaskInstanceJobDescription.processId());
            jobBuilder.nodeInstanceId(userTaskInstanceJobDescription.nodeInstanceId());
            jobBuilder.rootProcessInstanceId(userTaskInstanceJobDescription.rootProcessInstanceId());
            jobBuilder.rootProcessId(userTaskInstanceJobDescription.rootProcessId());
        }
    }

}
