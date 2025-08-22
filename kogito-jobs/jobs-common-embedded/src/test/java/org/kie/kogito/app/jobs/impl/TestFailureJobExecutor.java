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
package org.kie.kogito.app.jobs.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.kogito.app.jobs.api.JobExecutor;
import org.kie.kogito.jobs.service.model.JobDetails;

public class TestFailureJobExecutor implements JobExecutor {

    private List<String> jobIds;
    private Integer numberOfFailures;

    public TestFailureJobExecutor(Integer numberOfFailures) {
        this.jobIds = new ArrayList<>();
        this.numberOfFailures = numberOfFailures;
    }

    @Override
    public boolean accept(JobDetails jobDescription) {
        return true;
    }

    @Override
    public void execute(JobDetails jobDescription) {
        if (numberOfFailures > 0) {
            numberOfFailures--;
            throw new RuntimeException("Failure expected");
        }

        jobIds.add(jobDescription.getId());
    }

    public List<String> getJobsExecuted() {
        return jobIds;
    }
}
