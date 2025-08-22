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
package org.kie.kogito.app.jobs.jpa;

import java.time.OffsetDateTime;
import java.util.List;

import org.kie.kogito.app.jobs.jpa.model.JobDetailsEntity;
import org.kie.kogito.app.jobs.spi.JobContext;
import org.kie.kogito.app.jobs.spi.JobStore;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;

import jakarta.persistence.EntityManager;

public class JPAJobStore implements JobStore {

    private static final List<String> JOB_ACTIVE_STATUSES = List.of(JobStatus.RETRY.toString(), JobStatus.SCHEDULED.toString());

    @Override
    public List<JobDetails> loadActiveJobs(JobContext jobContext, OffsetDateTime maxWindowsLoad) {
        EntityManager entityManager = jobContext.getContext();

        List<JobDetailsEntity> timers = entityManager.createQuery("SELECT o FROM JobDetailsEntity o WHERE o.status IN (:activeStatus) AND o.fireTime <= :maxWindowsLoad", JobDetailsEntity.class)
                .setParameter("activeStatus", JOB_ACTIVE_STATUSES)
                .setParameter("maxWindowsLoad", maxWindowsLoad)
                .getResultList();
        return timers.stream()
                .map(JobDetailsEntityHelper::from)
                .toList();
    }

    @Override
    public JobDetails find(JobContext jobContext, String jobId) {
        EntityManager entityManager = jobContext.getContext();
        return JobDetailsEntityHelper.from(entityManager.find(JobDetailsEntity.class, jobId));
    }

    @Override
    public void persist(JobContext jobContext, JobDetails jobDetails) {
        EntityManager entityManager = jobContext.getContext();
        entityManager.persist(JobDetailsEntityHelper.merge(jobDetails, new JobDetailsEntity()));
    }

    @Override
    public void update(JobContext jobContext, JobDetails jobDetails) {
        EntityManager entityManager = jobContext.getContext();
        entityManager.merge(JobDetailsEntityHelper.merge(jobDetails, new JobDetailsEntity()));
    }

    @Override
    public void remove(JobContext jobContext, String jobId) {
        EntityManager entityManager = jobContext.getContext();
        entityManager.createQuery("DELETE FROM JobDetailsEntity o WHERE o.id = :jobId").setParameter("jobId", jobId).executeUpdate();
    }

    @Override
    public boolean shouldRun(JobContext jobContext, String jobId) {
        EntityManager entityManager = jobContext.getContext();
        int updated = entityManager.createQuery("UPDATE JobDetailsEntity o SET status = :status WHERE o.status IN (:activeStatus) AND o.id = :jobId")
                .setParameter("jobId", jobId)
                .setParameter("activeStatus", JOB_ACTIVE_STATUSES)
                .setParameter("status", JobStatus.RUNNING.toString())
                .executeUpdate();
        return updated > 0;
    }

}
