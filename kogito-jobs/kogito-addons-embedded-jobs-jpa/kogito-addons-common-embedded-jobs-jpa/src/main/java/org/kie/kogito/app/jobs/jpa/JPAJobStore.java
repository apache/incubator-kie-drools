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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.kogito.app.jobs.jpa.model.JobDetailsEntity;
import org.kie.kogito.app.jobs.spi.JobContext;
import org.kie.kogito.app.jobs.spi.JobStore;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

public class JPAJobStore implements JobStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(JPAJobStore.class);
    private static final List<String> JOB_ACTIVE_STATUSES = List.of(JobStatus.RETRY.toString(), JobStatus.SCHEDULED.toString());

    @Override
    public List<JobDetails> loadActiveJobs(JobContext jobContext, OffsetDateTime maxWindowsLoad) {
        JPAJobContext jpaJobContext = validateContext(jobContext);

        String queryString = buildQueryWithCteWhenFiltering(jpaJobContext,
                "SELECT o FROM JobDetailsEntity o WHERE o.status IN (:activeStatus) AND o.fireTime <= :maxWindowsLoad");

        LOGGER.trace("loadActiveJobs() - Query: {}", queryString);
        LOGGER.trace("loadActiveJobs() - Filtering enabled: {}", isFilterByLocalProcess(jpaJobContext));

        TypedQuery<JobDetailsEntity> jobDetailsEntityTypedQuery = jpaJobContext.getEntityManager()
                .createQuery(queryString, JobDetailsEntity.class)
                .setParameter("activeStatus", JOB_ACTIVE_STATUSES)
                .setParameter("maxWindowsLoad", maxWindowsLoad);
        bindDataIsolationKeysWhenFiltering(jpaJobContext, jobDetailsEntityTypedQuery);
        List<JobDetailsEntity> timers = jobDetailsEntityTypedQuery
                .getResultList();

        LOGGER.trace("loadActiveJobs() - Returned {} jobs", timers.size());

        return timers.stream()
                .map(JobDetailsEntityHelper::from)
                .toList();
    }

    @Override
    public JobDetails find(JobContext jobContext, String jobId) {
        JPAJobContext jpaJobContext = validateContext(jobContext);
        EntityManager entityManager = jpaJobContext.getEntityManager();
        JobDetails details = JobDetailsEntityHelper.from(entityManager.find(JobDetailsEntity.class, jobId));
        return details;
    }

    @Override
    public void persist(JobContext jobContext, JobDetails jobDetails) {
        JPAJobContext jpaJobContext = validateContext(jobContext);
        EntityManager entityManager = jpaJobContext.getEntityManager();
        entityManager.persist(JobDetailsEntityHelper.merge(jobDetails, new JobDetailsEntity()));
    }

    @Override
    public void update(JobContext jobContext, JobDetails jobDetails) {
        JPAJobContext jpaJobContext = validateContext(jobContext);
        EntityManager entityManager = jpaJobContext.getEntityManager();
        entityManager.merge(JobDetailsEntityHelper.merge(jobDetails, new JobDetailsEntity()));
    }

    @Override
    public void remove(JobContext jobContext, String jobId) {
        JPAJobContext jpaJobContext = validateContext(jobContext);
        EntityManager entityManager = jpaJobContext.getEntityManager();
        JobDetailsEntity entity = entityManager.find(JobDetailsEntity.class, jobId);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    @Override
    public boolean shouldRun(JobContext jobContext, String jobId) {
        JPAJobContext jpaJobContext = validateContext(jobContext);
        EntityManager entityManager = jpaJobContext.getEntityManager();

        String baseQuery = "UPDATE JobDetailsEntity o SET status = :status WHERE o.status IN (:activeStatus) AND o.id = :jobId";

        String queryString = buildUpdateQueryWithExistsWhenFiltering(jpaJobContext, baseQuery);

        LOGGER.trace("shouldRun() - jobId: {}, Query: {}", jobId, queryString);
        LOGGER.trace("shouldRun() - Filtering enabled: {}", isFilterByLocalProcess(jpaJobContext));

        Query query = entityManager.createQuery(queryString)
                .setParameter("jobId", jobId)
                .setParameter("activeStatus", JOB_ACTIVE_STATUSES)
                .setParameter("status", JobStatus.RUNNING.toString());

        bindDataIsolationKeysWhenFiltering(jpaJobContext, query);

        int updated = query.executeUpdate();

        LOGGER.trace("shouldRun() - jobId: {}, updated: {}", jobId, updated);
        return updated > 0;
    }

    protected JPAJobContext validateContext(JobContext jobContext) {
        if (!(jobContext instanceof JPAJobContext) || ((JPAJobContext) jobContext).getEntityManager() == null) {
            throw new RuntimeException("JPAJobStore requires JPAJobContext instance with EntityManager instance.");
        }
        return (JPAJobContext) jobContext;
    }

    private boolean isFilterByLocalProcess(JPAJobContext context) {
        return context.getProcesses() != null;
    }

    private String buildQueryWithCteWhenFiltering(JPAJobContext context, String baseQueryWithWhereClause) {
        if (!isFilterByLocalProcess(context)) {
            return baseQueryWithWhereClause;
        }

        List<DataIsolationKeyDescriptor> keys = context.getProcesses().processes().stream()
                .map(DataIsolationKeyDescriptor::fromProcess)
                .toList();

        String unionSelects = IntStream.range(0, keys.size())
                .mapToObj(i -> "SELECT CAST(:processId" + i + " AS STRING) AS processId, CAST(:processVersion" + i + " AS STRING) AS processVersion")
                .collect(Collectors.joining(" UNION ALL "));

        String cte = "WITH allowed_processes AS (" + unionSelects + ") ";

        String existsClause = "o.processId IN (SELECT ap.processId FROM allowed_processes ap)" +
                " AND (" +
                "o.processVersion IS NULL" +
                " OR " +
                "EXISTS (SELECT 1 FROM allowed_processes ap1 WHERE ap1.processId = o.rootProcessId AND ap1.processVersion = o.rootProcessVersion)" +
                " OR " +
                "(o.rootProcessId IS NULL AND EXISTS (SELECT 1 FROM allowed_processes ap2 WHERE ap2.processId = o.processId AND ap2.processVersion = o.processVersion))" +
                ")";

        return cte + baseQueryWithWhereClause + " AND " + existsClause;
    }

    private String buildUpdateQueryWithExistsWhenFiltering(JPAJobContext context, String baseUpdateQuery) {
        if (!isFilterByLocalProcess(context)) {
            return baseUpdateQuery;
        }

        List<DataIsolationKeyDescriptor> keys = context.getProcesses().processes().stream()
                .map(DataIsolationKeyDescriptor::fromProcess)
                .toList();

        String unionSelects = IntStream.range(0, keys.size())
                .mapToObj(i -> "SELECT CAST(:processId" + i + " AS STRING) AS processId, CAST(:processVersion" + i + " AS STRING) AS processVersion")
                .collect(Collectors.joining(" UNION ALL "));

        String existsClause = "o.processId IN (SELECT ap.processId FROM (" + unionSelects + ") ap)" +
                " AND (" +
                "o.processVersion IS NULL" +
                " OR " +
                "EXISTS (SELECT 1 FROM (" + unionSelects + ") ap1 WHERE ap1.processId = o.rootProcessId AND ap1.processVersion = o.rootProcessVersion)" +
                " OR " +
                "(o.rootProcessId IS NULL AND EXISTS (SELECT 1 FROM (" + unionSelects + ") ap2 WHERE ap2.processId = o.processId AND ap2.processVersion = o.processVersion))" +
                ")";

        return baseUpdateQuery + " AND " + existsClause;
    }

    private <T extends Query> void bindDataIsolationKeysWhenFiltering(JPAJobContext context, T query) {
        if (isFilterByLocalProcess(context)) {
            List<DataIsolationKeyDescriptor> keys = context.getProcesses().processes().stream()
                    .map(DataIsolationKeyDescriptor::fromProcess)
                    .toList();

            for (int i = 0; i < keys.size(); i++) {
                DataIsolationKeyDescriptor key = keys.get(i);
                query.setParameter("processId" + i, key.processId());
                query.setParameter("processVersion" + i, key.processVersion());
                LOGGER.trace("Local process id '{}' and version '{}'", key.processId(), key.processVersion());
            }
        }
    }

}
