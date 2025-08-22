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
package org.kie.kogito.app.jobs.jpa.quarkus;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.kie.kogito.app.jobs.api.JobSchedulerListener;
import org.kie.kogito.jobs.service.model.JobDetails;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestJobSchedulerListener implements JobSchedulerListener {

    private CountDownLatch latch;

    void setCount(Integer count) {
        latch = new CountDownLatch(count);
    }

    public boolean await(long timeout, TimeUnit unit) throws Exception {
        return latch.await(timeout, unit);
    }

    @Override
    public void onExecution(JobDetails jobDetails) {
        latch.countDown();
    }

}
