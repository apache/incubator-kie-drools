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
package org.kie.kogito.app.audit.jpa.queries;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public abstract class JPAAbstractQuery<R> {

    protected <T> List<T> executeWithNamedQueryEntityManager(EntityManager entityManager, String queryName, Class<T> clazz) {
        return executeWithNamedQueryEntityManager("META-INF/data-audit-orm.xml", entityManager, queryName);
    }

    protected <T> List<T> executeWithNamedQueryEntityManager(String file, EntityManager entityManager, String queryName) {
        String query = MappingFile.findInFile(file, entityManager, queryName);
        return entityManager.createNativeQuery(query).getResultList();
    }

    protected <T> List<T> executeWithNamedQueryEntityManager(EntityManager entityManager, String query) {
        return executeWithNamedQueryEntityManagerAndArguments(entityManager, query, Collections.emptyMap());
    }

    protected <T> List<T> executeWithNamedQueryEntityManagerAndArguments(EntityManager entityManager, String query, Map<String, Object> arguments) {
        return executeWithNamedQueryEntityManagerAndArguments("META-INF/data-audit-orm.xml", entityManager, query, arguments);
    }

    protected <T> List<T> executeWithNamedQueryEntityManagerAndArguments(String file, EntityManager entityManager, String queryName, Map<String, Object> arguments) {
        String query = MappingFile.findInFile(file, entityManager, queryName);

        Map<String, Object> parameters = new HashMap<>(arguments);
        Query jpaQuery = entityManager.createNativeQuery(query);
        @SuppressWarnings("unchecked")
        Map<String, Object> pagination = (Map<String, Object>) parameters.remove("pagination");
        parameters.forEach(jpaQuery::setParameter);
        if (pagination != null) {
            if (pagination.get("limit") != null) {
                jpaQuery.setMaxResults((Integer) pagination.get("limit"));
            } else {
                jpaQuery.setMaxResults(10);
            }
            if (pagination.get("offset") != null) {
                jpaQuery.setFirstResult((Integer) pagination.get("offset"));
                jpaQuery.setFirstResult(0);
            }
        } else {
            jpaQuery.setFirstResult(0);
            jpaQuery.setMaxResults(10);
        }

        return jpaQuery.getResultList();

    }
}
