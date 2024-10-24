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

import java.util.List;

import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.kie.kogito.auth.IdentityProvider;

import jakarta.persistence.TypedQuery;

import static org.jbpm.usertask.jpa.model.UserTaskInstanceEntity.GET_INSTANCES_BY_IDENTITY;

public class UserTaskInstanceRepository extends BaseRepository<UserTaskInstanceEntity, String> {

    public UserTaskInstanceRepository(UserTaskJPAContext context) {
        super(context);
    }

    public List<UserTaskInstanceEntity> findByIdentity(IdentityProvider identityProvider) {
        TypedQuery<UserTaskInstanceEntity> query = getEntityManager().createNamedQuery(GET_INSTANCES_BY_IDENTITY, UserTaskInstanceEntity.class);
        query.setParameter("userId", identityProvider.getName());
        query.setParameter("roles", identityProvider.getRoles());
        return query.getResultList();
    }

    @Override
    public Class<UserTaskInstanceEntity> getEntityClass() {
        return UserTaskInstanceEntity.class;
    }
}
