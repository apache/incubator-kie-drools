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

import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.service.api.PayloadData;

public class InVMPayloadData extends PayloadData<JobDescription> {

    private JobDescription jobDescription;

    public InVMPayloadData() {
        // do nothing
    }

    public void setJobDescription(JobDescription jobDescription) {
        this.jobDescription = jobDescription;
    }

    public JobDescription getJobDescription() {
        return jobDescription;
    }

    @Override
    public JobDescription getData() {
        return jobDescription;
    }

    public InVMPayloadData(JobDescription data) {
        this.jobDescription = data;
    }

    @Override
    public String toString() {
        return "InVMPayloadData [data=" + jobDescription + "]";
    }
}
