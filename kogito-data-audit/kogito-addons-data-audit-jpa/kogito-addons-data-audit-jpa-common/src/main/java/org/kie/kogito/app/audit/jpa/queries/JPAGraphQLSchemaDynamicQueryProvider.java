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

import java.util.ArrayList;
import java.util.List;

import org.kie.kogito.app.audit.api.DataAuditContext;
import org.kie.kogito.app.audit.jpa.model.AuditQuery;
import org.kie.kogito.app.audit.spi.GraphQLSchemaQuery;
import org.kie.kogito.app.audit.spi.GraphQLSchemaQueryProvider;

import jakarta.persistence.EntityManager;

public class JPAGraphQLSchemaDynamicQueryProvider implements GraphQLSchemaQueryProvider {

    @Override
    public List<GraphQLSchemaQuery> queries(DataAuditContext dataAuditContext) {
        EntityManager entityManager = dataAuditContext.getContext();
        List<AuditQuery> queriesRegistered = entityManager.createQuery("SELECT o FROM AuditQuery o", AuditQuery.class).getResultList();
        List<GraphQLSchemaQuery> queries = new ArrayList<>();
        for (AuditQuery auditQuery : queriesRegistered) {
            queries.add(new JPADynamicQuery(auditQuery.getIdentifier(), auditQuery.getQuery()));
        }
        return queries;

    }
}
