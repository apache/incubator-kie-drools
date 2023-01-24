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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNamedType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;

import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLNonNull.nonNull;
import static org.kie.kogito.index.storage.Constants.KOGITO_DOMAIN_ATTRIBUTE;

public class GraphQLInputObjectTypeMapper extends AbstractInputObjectTypeMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLInputObjectTypeMapper.class);
    private static final String ARGUMENT = "Argument";

    private boolean mapOperators;

    public GraphQLInputObjectTypeMapper(GraphQLSchema schema, Map<String, GraphQLType> additionalTypes, boolean mapOperators) {
        super(schema, additionalTypes);
        this.mapOperators = mapOperators;
    }

    public GraphQLInputObjectTypeMapper(GraphQLSchema schema, Map<String, GraphQLType> additionalTypes) {
        this(schema, additionalTypes, true);
    }

    @Override
    protected String getTypeName(GraphQLObjectType type) {
        return type.getName() + ARGUMENT;
    }

    @Override
    protected Function<GraphQLObjectType, List<GraphQLInputObjectField>> build() {
        return domain -> {
            List<GraphQLInputObjectField> fields = new ArrayList<>();
            if (mapOperators) {
                fields.add(newInputObjectField().name("and").type(GraphQLList.list(nonNull(new GraphQLTypeReference(getTypeName(domain))))).build());
                fields.add(newInputObjectField().name("or").type(GraphQLList.list(nonNull(new GraphQLTypeReference(getTypeName(domain))))).build());
                fields.add(newInputObjectField().name("not").type(new GraphQLTypeReference(getTypeName(domain))).build());
            }

            domain.getFieldDefinitions().forEach(field -> {
                LOGGER.debug("GraphQL mapping field: {}", field.getName());
                if (KOGITO_DOMAIN_ATTRIBUTE.equals(field.getName())) {
                    fields.add(newInputObjectField().name(KOGITO_DOMAIN_ATTRIBUTE).type(new GraphQLTypeReference("KogitoMetadataArgument")).build());
                } else if ("id".equals(field.getName())) {
                    fields.add(newInputObjectField().name("id").type(new GraphQLTypeReference("IdArgument")).build());
                } else {
                    GraphQLInputType inputTypeByField = getInputTypeByField(field);
                    if (inputTypeByField == null) {
                        LOGGER.warn("Can not map input type for field name: {}, type: {}", field.getName(), ((GraphQLNamedType) field.getType()).getName());
                    } else {
                        fields.add(newInputObjectField().name(field.getName()).type(inputTypeByField).build());
                    }
                }
            });
            return fields;
        };
    }

    private GraphQLInputType getInputTypeByField(GraphQLFieldDefinition field) {
        String name = resolveBaseTypeName(field.getType());
        switch (name) {
            case "Int":
            case "Long":
                return getInputObjectType("NumericArgument");
            case "String":
                return getInputObjectType("StringArgument");
            case "Boolean":
                return getInputObjectType("BooleanArgument");
            case "DateTime":
                return getInputObjectType("DateArgument");
            default:
                String typeName = name + ARGUMENT;
                GraphQLType schemaType = getExistingType(typeName);
                if (schemaType == null) {
                    GraphQLObjectType domain = (GraphQLObjectType) getAdditionalTypes().get(name);
                    if (domain == null) {
                        return null;
                    }
                    GraphQLInputObjectType type = new GraphQLInputObjectTypeMapper(getSchema(), getAdditionalTypes(), false).apply(domain);
                    getAdditionalTypes().put(typeName, type);
                    return type;
                } else {
                    return (GraphQLInputType) schemaType;
                }
        }
    }

    private GraphQLType getExistingType(String typeName) {
        GraphQLType schemaType = getSchema().getType(typeName);
        return schemaType == null ? getAdditionalTypes().get(typeName) : schemaType;
    }

}
