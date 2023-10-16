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
package org.kie.kogito.index.graphql.query;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNamedType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;

public abstract class AbstractInputObjectTypeMapper implements Function<GraphQLObjectType, GraphQLInputObjectType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractInputObjectTypeMapper.class);

    private GraphQLSchema schema;
    private Map<String, GraphQLType> additionalTypes;

    protected AbstractInputObjectTypeMapper(GraphQLSchema schema, Map<String, GraphQLType> additionalTypes) {
        this.schema = schema;
        this.additionalTypes = additionalTypes;
    }

    @Override
    public GraphQLInputObjectType apply(GraphQLObjectType domain) {
        LOGGER.debug("GraphQL mapping order by for: {}", domain.getName());
        String typeName = getTypeName(domain);
        final GraphQLInputObjectType existingType = getInputObjectType(typeName);
        if (existingType == null) {
            List<GraphQLInputObjectField> fields = build().apply(domain);
            if (fields.isEmpty()) {
                return null;
            } else {
                GraphQLInputObjectType.Builder builder = GraphQLInputObjectType.newInputObject().name(typeName);
                return builder.fields(fields).build();
            }
        } else {
            return existingType.transform(builder -> {
                builder.clearFields();
                builder.fields(build().apply(domain));
            });
        }
    }

    protected abstract Function<GraphQLObjectType, List<GraphQLInputObjectField>> build();

    protected abstract String getTypeName(GraphQLObjectType type);

    protected GraphQLSchema getSchema() {
        return schema;
    }

    protected Map<String, GraphQLType> getAdditionalTypes() {
        return additionalTypes;
    }

    protected GraphQLInputObjectType getInputObjectType(String name) {
        return (GraphQLInputObjectType) schema.getType(name);
    }

    protected String resolveBaseTypeName(GraphQLOutputType graphQLOutputType) {
        if (graphQLOutputType instanceof GraphQLList) {
            String baseType = ((GraphQLNamedType) graphQLOutputType.getChildren().get(0)).getName();
            if ("String".equals(baseType)) {
                return "StringArray";
            }
            return baseType;
        } else {
            return ((GraphQLNamedType) graphQLOutputType).getName();
        }
    }
}
