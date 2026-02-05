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

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kie.kogito.app.jobs.api.JobDescriptionMerger;
import org.kie.kogito.app.jobs.api.JobDetailsEventAdapter;
import org.kie.kogito.app.jobs.api.JobExecutor;
import org.kie.kogito.app.jobs.api.JobScheduler;
import org.kie.kogito.app.jobs.api.JobSchedulerBuilder;
import org.kie.kogito.app.jobs.api.JobSchedulerListener;
import org.kie.kogito.app.jobs.api.JobSynchronization;
import org.kie.kogito.app.jobs.api.JobTimeoutExecution;
import org.kie.kogito.app.jobs.api.JobTimeoutInterceptor;
import org.kie.kogito.app.jobs.integrations.ProcessInstanceJobDescriptionMerger;
import org.kie.kogito.app.jobs.integrations.ProcessJobDescriptionMerger;
import org.kie.kogito.app.jobs.integrations.UserTaskInstanceJobDescriptorMerger;
import org.kie.kogito.app.jobs.spi.JobContext;
import org.kie.kogito.app.jobs.spi.JobContextFactory;
import org.kie.kogito.app.jobs.spi.JobStore;
import org.kie.kogito.app.jobs.spi.memory.MemoryJobContextFactory;
import org.kie.kogito.app.jobs.spi.memory.MemoryJobStore;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.RecipientInstance;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.SimpleTimerTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;

public class VertxJobScheduler implements JobScheduler, Handler<Long> {

    private record TimerInfo(String jobId, Integer retries, Long timerId, Date timeout) {

    }

    private static Logger LOG = LoggerFactory.getLogger(VertxJobScheduler.class);

    private Integer maxNumberOfRetries;

    private Long refreshJobsInterval;

    private List<EventPublisher> eventPublishers;

    private List<JobExecutor> jobExecutors;

    private JobStore jobStore;

    private Vertx vertx;

    private WorkerExecutor workerExecutor;

    private JobContextFactory jobContextFactory;

    private List<JobDetailsEventAdapter> jobEventAdapters;

    private List<JobSchedulerListener> jobSchedulerListeners;

    private List<JobTimeoutInterceptor> interceptors;

    private List<JobDescriptionMerger> jobDescriptionMergers;

    private ConcurrentMap<String, TimerInfo> jobsScheduled;

    private Long refreshJobsIntervalTimerId;

    private Long maxRefreshJobsIntervalWindow;

    private Long retryInterval;

    public Integer numberOfWorkerThreads;

    private JobSynchronization jobSynchronization;

    public class VertxJobSchedulerBuilder implements JobSchedulerBuilder {

        @Override
        public JobSchedulerBuilder withRetryInterval(Long retryInterval) {
            VertxJobScheduler.this.retryInterval = retryInterval;
            return this;
        }

        @Override
        public JobSchedulerBuilder withJobSynchronization(JobSynchronization jobSynchronization) {
            VertxJobScheduler.this.jobSynchronization = jobSynchronization;
            return this;
        }

        @Override
        public JobSchedulerBuilder withJobSchedulerListeners(JobSchedulerListener... jobSchedulerListeners) {
            VertxJobScheduler.this.jobSchedulerListeners.addAll(List.of(jobSchedulerListeners));
            return this;
        }

        @Override
        public JobSchedulerBuilder withMaxRefreshJobsIntervalWindow(Long maxRefreshJobsIntervalWindow) {
            VertxJobScheduler.this.maxRefreshJobsIntervalWindow = maxRefreshJobsIntervalWindow;
            return this;
        }

        @Override
        public JobSchedulerBuilder withRefreshJobsInterval(Long refreshJobsInterval) {
            VertxJobScheduler.this.refreshJobsInterval = refreshJobsInterval;
            return this;
        }

        @Override
        public JobSchedulerBuilder withMaxNumberOfRetries(Integer maxNumberOfRetries) {
            VertxJobScheduler.this.maxNumberOfRetries = maxNumberOfRetries;
            return this;
        }

        @Override
        public JobSchedulerBuilder withJobEventAdapters(JobDetailsEventAdapter... jobEventAdapters) {
            VertxJobScheduler.this.jobEventAdapters.addAll(List.of(jobEventAdapters));
            return this;
        }

        @Override
        public JobSchedulerBuilder withEventPublishers(EventPublisher... eventPublishers) {
            VertxJobScheduler.this.eventPublishers.addAll(List.of(eventPublishers));
            return this;
        }

        @Override
        public JobSchedulerBuilder withJobContextFactory(JobContextFactory jobContextFactory) {
            VertxJobScheduler.this.jobContextFactory = jobContextFactory;
            return this;
        }

        @Override
        public JobSchedulerBuilder withJobExecutors(JobExecutor... jobExecutors) {
            VertxJobScheduler.this.jobExecutors.addAll(List.of(jobExecutors));
            return this;
        }

        @Override
        public JobSchedulerBuilder withJobStore(JobStore jobStore) {
            VertxJobScheduler.this.jobStore = jobStore;
            return this;
        }

        @Override
        public JobScheduler build() {
            Collections.sort(VertxJobScheduler.this.interceptors);
            return VertxJobScheduler.this;
        }

        @Override
        public JobSchedulerBuilder withTimeoutInterceptor(JobTimeoutInterceptor... interceptors) {
            VertxJobScheduler.this.interceptors.addAll(List.of(interceptors));
            return this;
        }

        @Override
        public JobSchedulerBuilder withNumberOfWorkerThreads(Integer numberOfWorkerThreads) {
            VertxJobScheduler.this.numberOfWorkerThreads = numberOfWorkerThreads;
            return this;
        }

        @Override
        public JobSchedulerBuilder withJobDescriptorMergers(JobDescriptionMerger... jobDescriptionMergers) {
            VertxJobScheduler.this.jobDescriptionMergers.addAll(List.of(jobDescriptionMergers));
            return this;
        }

    }

    public VertxJobScheduler() {
        this.jobExecutors = new ArrayList<>();
        this.jobStore = new MemoryJobStore();
        this.jobsScheduled = new ConcurrentHashMap<>();
        this.eventPublishers = new ArrayList<>();
        this.jobStore = new MemoryJobStore();
        this.jobContextFactory = new MemoryJobContextFactory();
        this.jobEventAdapters = new ArrayList<>();
        this.jobSchedulerListeners = new ArrayList<>();
        this.interceptors = new ArrayList<>();
        this.jobDescriptionMergers = new ArrayList<>();
        this.jobDescriptionMergers.add(new UserTaskInstanceJobDescriptorMerger());
        this.jobDescriptionMergers.add(new ProcessInstanceJobDescriptionMerger());
        this.jobDescriptionMergers.add(new ProcessJobDescriptionMerger());

        this.jobSynchronization = new JobSynchronization() {
            @Override
            public void synchronize(Runnable action) {
                action.run();
            }
        };

        this.numberOfWorkerThreads = 10;
        this.maxNumberOfRetries = 3;
        this.refreshJobsInterval = 1000L;
        this.retryInterval = 10 * 1000L; // ten seconds
        this.maxRefreshJobsIntervalWindow = 5 * 60 * 1000L; // every 5 minute
    }

    @Override
    public void handle(Long timerId) {
        Callable<JobTimeoutExecution> current = new Callable<JobTimeoutExecution>() {
            @Override
            public JobTimeoutExecution call() throws Exception {
                syncWithJobStores();
                return new JobTimeoutExecution(null);
            }
        };
        for (JobTimeoutInterceptor interceptor : interceptors) {
            current = interceptor.chainIntercept(current);
        }
        this.workerExecutor.executeBlocking(current);
    }

    private void syncWithJobStores() {
        ZonedDateTime maxWindowLoad = DateUtil.now().plus(Duration.ofMillis(maxRefreshJobsIntervalWindow));
        OffsetDateTime maxWindow = maxWindowLoad.toOffsetDateTime();
        LOG.debug("Syncing jobs with job store till {}", maxWindow);
        JobContext jobContext = jobContextFactory.newContext();
        List<JobDetails> jobDetailsList = jobStore.loadActiveJobs(jobContext, maxWindow);

        // this cover scenarios where the database jobs are already stored
        for (JobDetails currentJobDetails : jobDetailsList) {
            String mapKey = getMapKey(currentJobDetails);
            jobsScheduled.compute(mapKey, (key, timerInfo) -> {
                if (timerInfo == null) {
                    // we schedule this (no need to trigger an event as it was already trigger during scheduling)
                    // this is new job loaded by this instance
                    LOG.trace("sync job new job {}", currentJobDetails);
                    return addTimerInfo(currentJobDetails);
                }

                // there is timer and changed but we check the timeout is after. we remove it.
                // as it is not in this window
                if (DateUtil.dateToOffsetDateTime(timerInfo.timeout()).isAfter(maxWindow)) {
                    LOG.trace("sync job removed job {}", currentJobDetails);
                    // we remove it
                    removeTimerInfo(timerInfo);
                    return null;
                }

                if (DateUtil.dateToOffsetDateTime(timerInfo.timeout()).isBefore(maxWindow) && !timerInfo.timeout().equals(currentJobDetails.getTrigger().hasNextFireTime())) {
                    // timeout has changed and it is in our window. we should reschedule
                    LOG.trace("sync job changed job {}", currentJobDetails);
                    removeTimerInfo(timerInfo);
                    return addTimerInfo(currentJobDetails);
                }

                // timeout has not changed
                return timerInfo;
            });
        }

        // the ones left are the ones we need to be removed as they are not in database or active anymore
        List<String> databaseJobKeys = jobDetailsList.stream().map(this::getMapKey).toList();
        List<String> keysToBeRemoved = new ArrayList<>(jobsScheduled.keySet());
        keysToBeRemoved.removeAll(databaseJobKeys);

        for (String keyToBeRemoved : keysToBeRemoved) {
            jobsScheduled.compute(keyToBeRemoved, (key, timerInfo) -> {
                if (timerInfo != null) {
                    removeTimerInfo(timerInfo);
                }
                return null;
            });
        }
    }

    @Override
    public void init() {
        this.vertx = Vertx.builder().build();
        this.workerExecutor = this.vertx.createSharedWorkerExecutor("Jobs", numberOfWorkerThreads);
        this.maxRefreshJobsIntervalWindow = Math.max(maxRefreshJobsIntervalWindow, refreshJobsInterval);
        this.refreshJobsIntervalTimerId = this.vertx.setPeriodic(0L, refreshJobsInterval, this);

        LOG.info("Initializing Job Service Logic \n" +
                "\tMaxRefreshJobsIntervalWindow: {} (millis)\n" +
                "\tMaxIntervalLimitToRetryMillis: {} (millis)\n" +
                "\tMaxNumberOfRetries: {}\n" +
                "\tRefreshJobsInterval: {} (millis)\n" +
                "\tNumber of worker threads {}\n" +
                "\tStore: {}",
                maxRefreshJobsIntervalWindow,
                retryInterval,
                maxNumberOfRetries,
                refreshJobsInterval,
                numberOfWorkerThreads,
                jobStore);
    }

    @Override
    public void close() {
        this.vertx.cancelTimer(this.refreshJobsIntervalTimerId);

        // clean up
        this.workerExecutor.close();
        this.vertx.close();
        this.jobsScheduled.clear();

        this.refreshJobsIntervalTimerId = null;
        this.workerExecutor = null;
        this.vertx = null;
    }

    @Override
    public String schedule(JobDescription jobDescription) {
        JobDetails jobDetails = JobDetailsHelper.newScheduledJobDetails(jobDescription);
        jobStore.persist(jobContextFactory.newContext(), jobDetails);
        JobDetails scheduledJobDetails = doSchedule(jobDetails);
        jobSchedulerListeners.forEach(l -> l.onSchedule(scheduledJobDetails));
        return scheduledJobDetails.getId();
    }

    @Override
    public String reschedule(JobDescription jobDescription) {
        JobContext jobContext = jobContextFactory.newContext();
        JobDetails jobDetails = jobStore.find(jobContext, jobDescription.id());

        JobDetails canceledJobDetails = JobDetails.builder().of(jobDetails).status(JobStatus.CANCELED).build();
        LOG.trace("doCancel {}", canceledJobDetails);
        fireEvents(canceledJobDetails);

        JobDetails rescheduledJobDetails = JobDetailsHelper.newScheduledJobDetails(jobDescription);
        LOG.trace("doSchedule {}", rescheduledJobDetails);
        fireEvents(rescheduledJobDetails);
        jobSchedulerListeners.forEach(l -> l.onReschedule(rescheduledJobDetails));
        jobStore.update(jobContextFactory.newContext(), rescheduledJobDetails);
        addOrUpdateTxTimer(rescheduledJobDetails);

        return rescheduledJobDetails.getId();
    }

    @Override
    public void cancel(String jobId) {
        JobContext jobContext = jobContextFactory.newContext();
        JobDetails jobDetails = jobStore.find(jobContext, jobId);
        JobDetails cancelledJobDetails = doCancel(jobDetails);
        jobSchedulerListeners.forEach(l -> l.onCancel(cancelledJobDetails));
        jobStore.remove(jobContext, cancelledJobDetails.getId());
    }

    private void timeout(Long timerId, String jobId) {
        LOG.debug("Executing timeout with timer Id {} and jobId {}", timerId, jobId);
        workerExecutor.executeBlocking(newTimeoutTask(timerId, jobId));
    }

    private Callable<JobTimeoutExecution> newTimeoutTask(Long timerId, String jobId) {
        Callable<JobTimeoutExecution> current = new Callable<JobTimeoutExecution>() {
            @Override
            public JobTimeoutExecution call() throws Exception {

                LOG.trace("Timeout task {} with jobId {} newTimeoutTask", timerId, jobId);
                JobContext jobContext = jobContextFactory.newContext();
                // we check now if we should run
                boolean shouldRun = jobStore.shouldRun(jobContext, jobId);
                if (!shouldRun) {
                    LOG.trace("Timeout {} with jobId {} won't run", timerId, jobId);
                    VertxJobScheduler.this.jobsScheduled.remove(jobId);
                    return null;
                }

                LOG.debug("Timeout {} with jobId {} will be executed", timerId, jobId);
                JobDetails jobDetails = jobStore.find(jobContext, jobId);
                try {
                    JobDetails runningJobDetails = doRun(jobDetails);
                    LOG.trace("Timeout {} with jobId {} have been executed", timerId, jobId);
                    JobDetails executeJobDetails = doExecute(runningJobDetails);
                    LOG.trace("Timeout {} with jobId {} will be rescheduled if required", timerId, jobId);
                    JobDetails nextJobDetails = computeNextJobDetailsIfAny(executeJobDetails);
                    removeIfFinal(timerId, jobContext, nextJobDetails);
                    jobSchedulerListeners.forEach(l -> l.onExecution(jobDetails));
                    return new JobTimeoutExecution(nextJobDetails);
                } catch (Exception exception) {
                    LOG.trace("Timeout {} with jobId {} will be retried if possible", timerId, jobId, exception);
                    JobDetails nextJobDetails = computeRetryIfAny(jobDetails);
                    fireEvents(nextJobDetails);
                    removeIfFinal(timerId, jobContext, nextJobDetails);
                    jobSchedulerListeners.forEach(l -> l.onFailure(jobDetails));
                    return new JobTimeoutExecution(nextJobDetails, exception);
                }

            }
        };
        for (JobTimeoutInterceptor interceptor : interceptors) {
            current = interceptor.chainIntercept(current);
        }
        return current;
    }

    private void removeIfFinal(Long timerId, JobContext jobContext, JobDetails nextJobDetails) {
        String jobId = nextJobDetails.getId();
        switch (nextJobDetails.getStatus()) {
            case EXECUTED:
            case CANCELED:
                LOG.trace("Timeout {} with jobId {} will be removed", timerId, jobId);
                removeTxTimer(nextJobDetails);
                jobStore.remove(jobContext, jobId);
                break;
            case SCHEDULED:
            case RETRY:
                LOG.trace("Timeout {} with jobId {} will be updated and scheduled", timerId, jobId);
                jobStore.update(jobContext, nextJobDetails);
                doSchedule(nextJobDetails);
                break;
            case ERROR:
                LOG.trace("Timeout {} with jobId {} will be set to error", timerId, jobId);
                removeTxTimer(nextJobDetails);
                jobStore.update(jobContext, nextJobDetails);
                break;
            default:
                LOG.trace("Timeout {} with jobId {} is RUNNING and should not happen", timerId, jobId);
                break;
        }
    }

    // add tx timer and remove tx timer
    private void addOrUpdateTxTimer(JobDetails jobDetails) {
        this.jobSynchronization.synchronize(new Runnable() {
            @Override
            public void run() {
                String mapKey = getMapKey(jobDetails);
                jobsScheduled.compute(mapKey, (key, timerInfo) -> {
                    if (timerInfo != null) {
                        removeTimerInfo(timerInfo);
                    }
                    return addTimerInfo(jobDetails);
                });
            }
        });
    }

    private void removeTxTimer(JobDetails jobDetails) {
        this.jobSynchronization.synchronize(new Runnable() {
            @Override
            public void run() {
                String mapKey = getMapKey(jobDetails);
                jobsScheduled.computeIfPresent(mapKey, (key, timerInfo) -> {
                    removeTimerInfo(timerInfo);
                    return null;
                });
            }
        });
    }

    private String getMapKey(JobDetails jobDetails) {
        return jobDetails.getId() + "-" + jobDetails.getRetries();
    }

    // vertx calls
    private TimerInfo addTimerInfo(JobDetails jobDetails) {
        LOG.trace("addTimerInfo {}", jobDetails);
        // if it is negative means it should be executed right away
        ZonedDateTime trigger = DateUtil.fromDate(jobDetails.getTrigger().hasNextFireTime());
        ZonedDateTime now = DateUtil.now();
        Long diff = ChronoUnit.MILLIS.between(now, trigger);
        Long delay = Math.max(1, diff);
        Long timerId = this.vertx.setTimer(delay, new Handler<Long>() {
            @Override
            public void handle(Long timerId) {
                timeout(timerId, jobDetails.getId());
            }
        });
        return new TimerInfo(jobDetails.getId(), jobDetails.getRetries(), timerId, jobDetails.getTrigger().hasNextFireTime());
    }

    private void removeTimerInfo(TimerInfo timerInfo) {
        LOG.trace("removeTimerInfo {}", timerInfo);
        Long timerId = timerInfo.timerId();
        this.vertx.cancelTimer(timerId);
    }

    // lifecycle calls
    private JobDetails doSchedule(JobDetails jobDetails) {
        addOrUpdateTxTimer(jobDetails);
        LOG.trace("doSchedule {}", jobDetails);
        fireEvents(jobDetails);
        return jobDetails;
    }

    private JobDetails doRun(JobDetails jobDetails) {
        JobDetails runJobDetails = JobDetails.builder().of(jobDetails).status(JobStatus.RUNNING).build();
        LOG.trace("doRun {}", runJobDetails);
        fireEvents(runJobDetails);
        return runJobDetails;
    }

    private JobDetails doCancel(JobDetails jobDetails) {
        removeTxTimer(jobDetails);
        JobDetails canceledJobDetails = JobDetails.builder().of(jobDetails).status(JobStatus.CANCELED).build();
        LOG.trace("doCancel {}", canceledJobDetails);
        fireEvents(canceledJobDetails);
        return canceledJobDetails;
    }

    private JobDetails doExecute(JobDetails jobDetails) {
        List<JobExecutor> validExecutors = jobExecutors.stream().filter(executor -> executor.accept(jobDetails)).toList();
        LOG.trace("valid executors are: {}", validExecutors);
        validExecutors.forEach(executor -> executor.execute(jobDetails));
        JobDetails executedJobDetails = JobDetails.builder().of(jobDetails).status(JobStatus.EXECUTED).incrementExecutionCounter().build();
        LOG.trace("doExecute {}", executedJobDetails);
        fireEvents(executedJobDetails);
        return executedJobDetails;
    }

    private JobDetails computeRetryIfAny(JobDetails jobDetails) {
        LOG.trace("doRetryIfAny {}", jobDetails);
        Integer retryCounter = jobDetails.getRetries();
        if (retryCounter < this.maxNumberOfRetries) {

            Date now = Date.from(DateUtil.now().plus(Duration.ofMillis(retryInterval)).toInstant());
            Trigger newTrigger = setTriggerDate(jobDetails.getTrigger(), now);
            JobDescription jobDescriptionMerged = setJobDescription(jobDetails, newTrigger);
            JobDetails retryJobDetails = JobDetails.builder().of(jobDetails)
                    .trigger(newTrigger)
                    .recipient(new RecipientInstance(new InVMRecipient(new InVMPayloadData(jobDescriptionMerged))))
                    .status(JobStatus.RETRY)
                    .executionTimeout(jobDetails.getExecutionTimeout() + retryInterval)
                    .incrementRetries()
                    .build();
            LOG.trace("Do retry with {}", retryJobDetails);
            return retryJobDetails;
        } else {
            JobDetails errorJobDetails = JobDetails.builder().of(jobDetails).status(JobStatus.ERROR).build();
            LOG.trace("Do not retry {}", errorJobDetails);
            return errorJobDetails;
        }
    }

    private Trigger setTriggerDate(Trigger oldTrigger, Date newOriginDate) {
        SimpleTimerTrigger oldSimpleTimerTrigger = (SimpleTimerTrigger) oldTrigger;
        SimpleTimerTrigger newTrigger = new SimpleTimerTrigger(
                newOriginDate,
                oldSimpleTimerTrigger.getPeriod(),
                oldSimpleTimerTrigger.getPeriodUnit(),
                oldSimpleTimerTrigger.getRepeatCount(),
                oldSimpleTimerTrigger.getEndTime(),
                oldSimpleTimerTrigger.getZoneId());
        return newTrigger;
    }

    private JobDescription setJobDescription(JobDetails jobDetails, Trigger newTrigger) {
        JobDescription jobDescription = jobDetails.getRecipient().<InVMPayloadData> getRecipient().getPayload().getJobDescription();

        JobDescription newJobDescription = jobDescriptionMergers.stream()
                .filter(merger -> merger.accept(jobDescription))
                .map(merger -> merger.mergeTrigger(jobDescription, newTrigger))
                .findFirst()
                .orElseThrow();
        return newJobDescription;
    }

    private JobDetails computeNextJobDetailsIfAny(JobDetails jobDetails) {
        // there is a problem here. If we retried the job the origin, the current time is different.
        // so we set the current time as the time of execution so we do execute things at fixed interval time.
        ((SimpleTimerTrigger) jobDetails.getTrigger()).setNextFireTime(Date.from(Instant.now()));
        jobDetails.getTrigger().nextFireTime();
        if (jobDetails.getTrigger().hasNextFireTime() != null) {
            // we set the date for the trigger so we compute new job description
            JobDescription jobDescriptionMerged = setJobDescription(jobDetails, jobDetails.getTrigger());
            JobDetails nextJobDetails = JobDetails.builder()
                    .of(jobDetails)
                    .recipient(new RecipientInstance(new InVMRecipient(new InVMPayloadData(jobDescriptionMerged))))
                    .status(JobStatus.SCHEDULED)
                    .retries(0)
                    .executionTimeout(jobDetails.getTrigger().hasNextFireTime().getTime())
                    .build();
            LOG.trace("computeNextJobDetailsIfAny {}", nextJobDetails);
            return nextJobDetails;
        }
        LOG.trace("computeNextJobDetailsIfAny {}", jobDetails);
        return jobDetails;
    }

    private void fireEvents(JobDetails jobDetails) {
        List<DataEvent<byte[]>> jobInstanceEvents = jobEventAdapters.stream().filter(e -> e.accept(jobDetails)).map(e -> e.adapt(jobDetails)).toList();
        for (DataEvent<byte[]> jobEvent : jobInstanceEvents) {
            eventPublishers.forEach(e -> e.publish(jobEvent));
        }
    }

}
