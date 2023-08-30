/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.index.graphql.query;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNamedType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;

import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static org.kie.kogito.index.storage.Constants.PROCESS_INSTANCES_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.storage.Constants.USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE;

public class GraphQLOrderByTypeMapper extends AbstractInputObjectTypeMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLOrderByTypeMapper.class);
    private static final String ORDER_BY = "OrderBy";

    public GraphQLOrderByTypeMapper(GraphQLSchema schema, Map<String, GraphQLType> additionalTypes) {
        super(schema, additionalTypes);
    }

    @Override
    public String getTypeName(GraphQLObjectType type) {
        return type.getName() + ORDER_BY;
    }

    @Override
    protected Function<GraphQLObjectType, List<GraphQLInputObjectField>> build() {
        return domain -> domain.getFieldDefinitions().stream()
                .filter(field -> !(field.getType() instanceof GraphQLList))
                .filter(field -> !PROCESS_INSTANCES_DOMAIN_ATTRIBUTE.equals(field.getName()))
                .filter(field -> !USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE.equals(field.getName()))
                .filter(field -> !"id".equals(field.getName()))
                .map(field -> {
                    LOGGER.debug("GraphQL mapping field: {}", field.getName());
                    String typeName = getTypeName(field);
                    if (typeName != null) {
                        return newInputObjectField().name(field.getName()).type(new GraphQLTypeReference(typeName)).build();
                    } else {
                        LOGGER.warn("Can not map order by type for field name: {}, type: {}", field.getName(), ((GraphQLNamedType) field.getType()).getName());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private String getTypeName(GraphQLFieldDefinition field) {
        String name = ((GraphQLNamedType) field.getType()).getName();
        switch (name) {
            case "Int":
            case "Long":
            case "BigDecimal":
            case "Float":
            case "String":
            case "Boolean":
            case "DateTime":
                return ORDER_BY;
            default:
                String typeName = name + ORDER_BY;
                if (getSchema().getType(typeName) == null && !getAdditionalTypes().containsKey(typeName)) {
                    GraphQLObjectType objectType = (GraphQLObjectType) getAdditionalTypes().get(name);
                    if (objectType == null) {
                        return null;
                    }
                    GraphQLInputObjectType type = new GraphQLOrderByTypeMapper(getSchema(), getAdditionalTypes()).apply(objectType);
                    if (type != null) {
                        getAdditionalTypes().put(typeName, type);
                    } else {
                        return null;
                    }
                }
                return typeName;
        }
    }
}
