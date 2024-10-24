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
import java.util.Optional;

import jakarta.persistence.EntityManager;

public abstract class BaseRepository<T, K> {

    protected UserTaskJPAContext context;

    public BaseRepository(UserTaskJPAContext context) {
        this.context = context;
    }

    public Optional<T> findById(K id) {
        return Optional.ofNullable(getEntityManager().find(getEntityClass(), id));
    }

    public List<T> findAll() {
        return getEntityManager().createQuery("from " + getEntityClass().getName(), getEntityClass()).getResultList();
    }

    public T persist(T entity) {
        getEntityManager().persist(entity);

        return entity;
    }

    public T update(T entity) {
        return this.getEntityManager().merge(entity);
    }

    public T remove(T entity) {
        this.getEntityManager().remove(entity);
        return entity;
    }

    public abstract Class<T> getEntityClass();

    protected EntityManager getEntityManager() {
        return context.getEntityManager();
    }
}
