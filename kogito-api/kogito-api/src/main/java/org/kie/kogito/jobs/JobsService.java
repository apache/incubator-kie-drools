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
package org.kie.kogito.jobs;

/**
 * JobsService provides an entry point for working with different types of jobs
 * that are meant by default to run in background.
 *
 */
public interface JobsService {

    /**
     * Schedules process job that is responsible for starting new process instances
     * based on the given description.
     * 
     * @param context of the job
     * @param description defines what kind of process should be started upon expiration time
     * @return returns unique id of the job
     */
    String scheduleJob(JobDescription description);

    /**
     * Cancels given job
     * 
     * @param id unique id of the job
     * @return returns true if the cancellation was successful, otherwise false
     */
    boolean cancelJob(String id);
}
