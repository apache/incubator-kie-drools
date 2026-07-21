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

package org.jbpm.usertask.jpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.kie.kogito.Model;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.process.Process;
import org.kie.kogito.usertask.UserTaskFilter;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import static org.jbpm.usertask.jpa.model.UserTaskInstanceEntity.BASE_IDENTITY_QUERY;
import static org.jbpm.usertask.jpa.model.UserTaskInstanceEntity.DELETE_BY_ID;
import static org.jbpm.usertask.jpa.model.UserTaskInstanceEntity.PROCESS_ID_FILTER_CLAUSE;
import static org.jbpm.usertask.jpa.model.UserTaskInstanceEntity.PROCESS_INSTANCE_ID_FILTER_CLAUSE;
import static org.jbpm.usertask.jpa.model.UserTaskInstanceEntity.STATUS_FILTER_CLAUSE;
import static org.jbpm.usertask.jpa.model.UserTaskInstanceEntity.TASKNAME_FILTER_CLAUSE;

public class UserTaskInstanceRepository extends BaseRepository<UserTaskInstanceEntity, String> {

    public UserTaskInstanceRepository(UserTaskJPAContext context) {
        super(context);
    }

    @Override
    public List<UserTaskInstanceEntity> findAll() {
        String baseQuery = "SELECT e FROM UserTaskInstanceEntity e";
        TypedQuery<UserTaskInstanceEntity> query = getEntityManager()
                .createQuery(buildQueryWithProcessFiltering(baseQuery, "e"), UserTaskInstanceEntity.class);
        bindProcessFilteringParameters(query);
        return query.getResultList();
    }

    @Override
    public Optional<UserTaskInstanceEntity> findById(String id) {
        String baseQuery = "SELECT e FROM UserTaskInstanceEntity e WHERE id=:userTaskId";
        TypedQuery<UserTaskInstanceEntity> query = getEntityManager().createQuery(
                buildQueryWithProcessFiltering(baseQuery, "e"), UserTaskInstanceEntity.class);
        bindProcessFilteringParameters(query);
        query.setParameter("userTaskId", id);
        return query.getResultList().stream().findFirst();
    }

    public List<UserTaskInstanceEntity> findByIdentity(IdentityProvider identityProvider) {
        return findByIdentity(identityProvider, null);
    }

    public List<UserTaskInstanceEntity> findByIdentity(IdentityProvider identityProvider, UserTaskFilter filter) {
        String userId = identityProvider.getName();
        Collection<String> roles = identityProvider.getRoles();

        String jpql = BASE_IDENTITY_QUERY;

        boolean hasProcessIdFilter = filter != null
                && filter.processId() != null
                && !filter.processId().isEmpty();

        boolean hasProcessInstanceIdFilter = filter != null
                && filter.processInstanceId() != null
                && !filter.processInstanceId().isEmpty();

        boolean hasTaskNameFilter = filter != null
                && filter.taskName() != null
                && !filter.taskName().isEmpty();

        boolean hasStatusFilter = filter != null
                && filter.statuses() != null
                && !filter.statuses().isEmpty();

        if (hasProcessIdFilter) {
            jpql = jpql.concat(PROCESS_ID_FILTER_CLAUSE);
        }

        if (hasProcessInstanceIdFilter) {
            jpql = jpql.concat(PROCESS_INSTANCE_ID_FILTER_CLAUSE);
        }

        if (hasTaskNameFilter) {
            jpql = jpql.concat(TASKNAME_FILTER_CLAUSE);
        }

        if (hasStatusFilter) {
            jpql = jpql.concat(STATUS_FILTER_CLAUSE);
        }

        TypedQuery<UserTaskInstanceEntity> query = getEntityManager()
                .createQuery(buildQueryWithProcessFiltering(jpql, "userTask"), UserTaskInstanceEntity.class)
                .setParameter("userId", userId)
                .setParameter("roles", roles);
        bindProcessFilteringParameters(query);

        if (hasProcessIdFilter) {
            query.setParameter("processId", filter.processId());
        }

        if (hasProcessInstanceIdFilter) {
            query.setParameter("processInstanceId", filter.processInstanceId());
        }

        if (hasTaskNameFilter) {
            query.setParameter("taskName", filter.taskName());
        }

        if (hasStatusFilter) {
            query.setParameter("statusFilter", filter.statuses());
        }

        return query.getResultList();
    }

    protected Collection<Process<? extends Model>> getProcessIdsForFiltering() {
        return context.getProcesses() != null ? context.getProcesses().processes() : null;
    }

    protected boolean isFilterByLocalProcess() {
        Collection<Process<? extends Model>> processIds = getProcessIdsForFiltering();
        return processIds != null && !processIds.isEmpty();
    }

    protected String buildQueryWithProcessFiltering(String baseQuery, String entityReference) {
        if (!isFilterByLocalProcess()) {
            return baseQuery;
        }

        Collection<Process<? extends Model>> processIds = getProcessIdsForFiltering();
        String unionSelects = IntStream.range(0, processIds.size())
                .mapToObj(i -> "SELECT CAST(:processId" + i + " AS STRING) AS processId, CAST(:processVersion" + i + " AS STRING) AS processVersion")
                .collect(Collectors.joining(" UNION ALL "));
        String cte = "WITH allowed_processes AS (" + unionSelects + ") ";
        String whereClause = baseQuery.toLowerCase().contains(" where ") ? " AND " : " WHERE ";

        String existsClause = whereClause +

        // Pre-filter based on indexed processId
                String.format("%1$s.processInfo.processId IN (SELECT ap.processId FROM allowed_processes ap)", entityReference) +
                " AND (" +
                // Exact tuple checks wrapped inside their own isolated OR block
                String.format("%1$s.processInfo.processVersion IS NULL", entityReference) +
                " OR " +
                String.format("EXISTS (SELECT 1 FROM allowed_processes ap1 WHERE ap1.processId = %1$s.processInfo.rootProcessId AND ap1.processVersion = %1$s.processInfo.rootProcessVersion)",
                        entityReference)
                +
                " OR " +
                String.format(
                        "(%1$s.processInfo.rootProcessId IS NULL AND EXISTS (SELECT 1 FROM allowed_processes ap2 WHERE ap2.processId = %1$s.processInfo.processId AND ap2.processVersion = %1$s.processInfo.processVersion))",
                        entityReference)
                +
                ")";

        return cte + baseQuery + existsClause;
    }

    protected <Q extends Query> void bindProcessFilteringParameters(Q query) {
        if (isFilterByLocalProcess()) {
            List<Process<? extends Model>> processIdList = List.copyOf(getProcessIdsForFiltering());
            for (int i = 0; i < processIdList.size(); i++) {
                query.setParameter("processId" + i, processIdList.get(i).id());
                query.setParameter("processVersion" + i, processIdList.get(i).version());
            }
        }
    }

    public UserTaskInstanceEntity delete(UserTaskInstanceEntity entity) {
        getEntityManager().detach(entity);
        getEntityManager().createNamedQuery(DELETE_BY_ID)
                .setParameter("taskId", entity.getId())
                .executeUpdate();
        return entity;
    }

    @Override
    public Class<UserTaskInstanceEntity> getEntityClass() {
        return UserTaskInstanceEntity.class;
    }
}
