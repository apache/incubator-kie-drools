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
package org.kie.kogito.addons.quarkus.jobs.service.embedded;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "kogito.jobs-service", namingStrategy = ConfigMapping.NamingStrategy.VERBATIM)
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface KogitoAddonsQuarkusJobsServiceEmbeddedRuntimeConfig {

    /**
     * Embedded jobs service url.
     */
    String url();

    /**
     * The current chunk size in minutes the scheduler handles, it is used to keep a limited number of jobs scheduled
     * in the in-memory scheduler.
     */
    @WithDefault("10")
    long schedulerChunkInMinutes();

    /**
     * Minimal delay used by scheduler before firing any job.
     */
    @WithDefault("1000")
    long schedulerMinTimerDelayInMillis();

    /**
     * The interval the jobs loading method runs to fetch the persisted jobs from the repository.
     */
    @WithDefault("10")
    long loadJobIntervalInMinutes();

    /**
     * The interval based on the current time the jobs loading method uses to fetch jobs "FROM (now -
     * loadJobFromCurrentTimeIntervalInMinutes) TO schedulerChunkInMinutes
     */
    @WithDefault("60")
    long loadJobFromCurrentTimeIntervalInMinutes();

    /**
     * Maximum amount of time the jobs service will be retrying to get a successful execution for a job.
     */
    @WithDefault("60000")
    long maxIntervalLimitToRetryMillis();

    /**
     * Delay between retries when a job execution fails, and it must be retried.
     */
    @WithDefault("1000")
    long backoffRetryMillis();

    /**
     * Flag to allow and force a job with expirationTime in the past to be executed immediately. If false an
     * exception will be thrown.
     */
    @WithDefault("true")
    boolean forceExecuteExpiredJobs();

    /**
     * Flag to allow that jobs that where timed-out when the jobs service was down, must be fired immediately at the
     * jobs service next startup.
     */
    @WithDefault("true")
    boolean forceExecuteExpiredJobsOnServiceStart();

    /**
     * Number of retries configured for the periodic jobs loading procedure. Every time the procedure is started this
     * value is considered.
     */
    @WithDefault("3")
    int loadJobRetries();

    /**
     * Error strategy to apply when the periodic jobs loading procedure has exceeded the jobLoadReties.
     */
    @WithDefault("NONE")
    String loadJobErrorStrategy();

    /**
     * Heartbeat interval for the JobsServiceInstanceManager.
     */
    @WithName("management.heartbeat.interval-in-seconds")
    @WithDefault("1")
    int heardBeatIntervalInSeconds();

    /**
     * Heartbeat expiration time for the JobsServiceInstanceManager.
     */
    @WithName("management.heartbeat.expiration-in-seconds")
    @WithDefault("10")
    int heartbeatExpirationInSeconds();

    /**
     * Jobs Service id for the JobsServiceInstanceManager.
     */
    @WithName("management.heartbeat.management-id")
    @WithDefault("kogito-jobs-service-leader")
    String leaderManagementId();

    /**
     * Leader check interval for the JobsServiceInstanceManager.
     */
    @WithName("management.leader-check.interval-in-seconds")
    @WithDefault("1")
    int leaderCheckIntervalInSeconds();

    /**
     * Availability health check enabling.
     */
    @WithName("health-enabled")
    Optional<Boolean> healthEnabled();
}
