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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.kogito.app.audit.api.DataAuditContext;
import org.kie.kogito.app.audit.spi.GraphQLSchemaQuery;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchemaElement;
import jakarta.persistence.EntityManager;

import static org.kie.kogito.app.audit.jpa.queries.mapper.DateTimeUtil.toDateTime;

public class JPADynamicQuery extends JPAAbstractQuery<Object> implements GraphQLSchemaQuery {

    private String name;
    private String query;

    public JPADynamicQuery(String name, String query) {
        this.name = name;
        this.query = query;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Object fetch(DataFetchingEnvironment dataFetchingEnvironment) {
        Map<String, Object> arguments = dataFetchingEnvironment.getArguments();
        DataAuditContext context = dataFetchingEnvironment.getLocalContext();
        EntityManager entityManager = context.getContext();

        GraphQLSchemaElement element = dataFetchingEnvironment.getFieldType();
        if (arguments.isEmpty()) {
            return toJson(element, executeWithQueryEntityManagerAndArguments(entityManager, query));
        } else {
            return toJson(element, executeWithQueryEntityManagerAndArguments(entityManager, query, arguments));
        }
    }

    private Object toJson(GraphQLSchemaElement element, List<Object> data) {
        if (data.isEmpty()) {
            return Collections.emptyList();
        }

        if (element instanceof GraphQLList) {
            GraphQLSchemaElement child = element.getChildren().get(0);
            List<Object> transformedData = new ArrayList<>();
            for (Object row : data) {
                transformedData.add(toJsonObject(child, row));
            }
            return transformedData;
        } else if (element instanceof GraphQLSchemaElement) {
            return toJsonObject(element, data);
        }
        return null;
    }

    private Object toJsonObject(GraphQLSchemaElement element, Object data) {
        if (element instanceof GraphQLScalarType) {
            return transform((GraphQLScalarType) element, data);
        } else if (element instanceof GraphQLObjectType) {
            return toComplexObject((GraphQLObjectType) element, (Object[]) data);
        }
        return null;
    }

    private Object toComplexObject(GraphQLObjectType type, Object[] data) {
        Map<String, Object> newPojo = new HashMap<>();
        for (int i = 0; i < data.length; i++) {
            GraphQLFieldDefinition definition = type.getFieldDefinitions().get(i);
            newPojo.put(definition.getName(), transform(definition.getType(), data[i]));
        }
        return newPojo;
    }

    private Object transform(GraphQLOutputType outputType, Object source) {
        if (source == null) {
            return null;
        }
        Object target = source;
        if (outputType instanceof GraphQLScalarType) {
            GraphQLScalarType scalarType = (GraphQLScalarType) outputType;
            if ("DateTime".equals(scalarType.getName())) {
                target = toDateTime(source);
            }

        }
        return target;
    }

}
