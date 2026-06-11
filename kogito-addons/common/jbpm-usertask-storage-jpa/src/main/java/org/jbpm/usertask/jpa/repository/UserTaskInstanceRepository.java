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

import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.usertask.UserTaskFilter;

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
                .createQuery(jpql, UserTaskInstanceEntity.class)
                .setParameter("userId", userId)
                .setParameter("roles", roles);

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
