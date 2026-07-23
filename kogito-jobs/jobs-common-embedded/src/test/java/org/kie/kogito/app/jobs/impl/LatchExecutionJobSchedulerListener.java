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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.kie.kogito.app.jobs.api.JobSchedulerListener;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatchExecutionJobSchedulerListener implements JobSchedulerListener {

    private static final Logger LOG = LoggerFactory.getLogger(LatchExecutionJobSchedulerListener.class);

    private CountDownLatch latch;

    private AtomicInteger count;

    public LatchExecutionJobSchedulerListener() {
        this(1);
    }

    public LatchExecutionJobSchedulerListener(Integer executions) {
        latch = new CountDownLatch(executions);
        count = new AtomicInteger(0);
    }

    @Override
    public void onSchedule(JobDetails jobDetails) {
        // do nothing
    }

    @Override
    public void onReschedule(JobDetails jobDetails) {
        // do nothing
    }

    @Override
    public void onCancel(JobDetails jobDetails) {
        // do nothing
    }

    @Override
    public void onFailure(JobDetails jobDetails) {
        // do nothing
    }

    @Override
    public void onExecution(JobDetails jobDetails) {
        LOG.info("executing {}", jobDetails);
        latch.countDown();
        count.incrementAndGet();
    }

    public Integer getCount() {
        return count.get();
    }

    public void waitForExecution() throws InterruptedException {
        latch.await();
    }

    public void waitForExecution(Long timeout) throws InterruptedException {
        latch.await(timeout, TimeUnit.MILLISECONDS);
    }

    public boolean isExecuted() {
        return latch.getCount() == 0;
    }
}
