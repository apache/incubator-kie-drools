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

import java.util.List;
import java.util.Map;

import org.kie.kogito.app.audit.api.DataAuditContext;
import org.kie.kogito.app.audit.spi.GraphQLSchemaQuery;

import graphql.schema.DataFetchingEnvironment;
import jakarta.persistence.EntityManager;

public class JPAComplexNamedQuery<T, R> extends JPAAbstractQuery<R> implements GraphQLSchemaQuery<List<T>> {

    private String name;
    private String namedQuery;
    private DataMapper<T, R> dataMapper;

    public JPAComplexNamedQuery(String name, DataMapper<T, R> dataMapper) {
        this(name, name, dataMapper);
    }

    public JPAComplexNamedQuery(String name, String namedQuery, DataMapper<T, R> dataMapper) {
        this.name = name;
        this.namedQuery = namedQuery;
        this.dataMapper = dataMapper;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<T> fetch(DataFetchingEnvironment dataFetchingEnvironment) {
        Map<String, Object> arguments = dataFetchingEnvironment.getArguments();
        DataAuditContext context = dataFetchingEnvironment.getLocalContext();
        EntityManager entityManager = context.getContext();

        if (arguments.isEmpty()) {
            return dataMapper.produce(executeWithNamedQueryEntityManager(entityManager, namedQuery));
        } else {
            return dataMapper.produce(executeWithNamedQueryEntityManagerAndArguments(entityManager, namedQuery, arguments));
        }
    }

}
