/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.swf.tools.dataindex.jobs;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobsData {

    @JsonProperty("Jobs")
    private List<Job> jobsList;

    public JobsData() {
    }

    public JobsData(final List<Job> jobsList) {
        this.jobsList = jobsList;
    }

    public List<Job> getJobs() {
        return jobsList;
    }

    public void setJobs(final List<Job> jobsList) {
        this.jobsList = jobsList;
    }
}
