/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Map;
import java.util.function.Consumer;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNamedType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLNonNull.nonNull;
import static org.kie.kogito.index.Constants.KOGITO_DOMAIN_ATTRIBUTE;

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
    protected Consumer<GraphQLInputObjectType.Builder> build(GraphQLObjectType domain) {
        return builder -> {
            if (mapOperators) {
                builder.field(newInputObjectField().name("and").type(GraphQLList.list(nonNull(new GraphQLTypeReference(getTypeName(domain))))));
                builder.field(newInputObjectField().name("or").type(GraphQLList.list(nonNull(new GraphQLTypeReference(getTypeName(domain))))));
            }

            domain.getFieldDefinitions().forEach(field -> {
                LOGGER.debug("GraphQL mapping field: {}", field);
                if (KOGITO_DOMAIN_ATTRIBUTE.equals(field.getName())) {
                    builder.field(newInputObjectField().name(KOGITO_DOMAIN_ATTRIBUTE).type(new GraphQLTypeReference("KogitoMetadataArgument"))).build();
                } else if ("id".equals(field.getName())) {
                    builder.field(newInputObjectField().name("id").type(new GraphQLTypeReference("IdArgument"))).build();
                } else {
                    newField(builder, field, getInputTypeByField(field));
                }
            });
        };
    }

    private GraphQLInputType getInputTypeByField(GraphQLFieldDefinition field) {
        String name = ((GraphQLNamedType) field.getType()).getName();
        switch (name) {
            case "Int":
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
                    GraphQLInputObjectType type = new GraphQLInputObjectTypeMapper(getSchema(), getAdditionalTypes(), false).apply((GraphQLObjectType) getAdditionalTypes().get(name));
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

    private void newField(GraphQLInputObjectType.Builder builder, GraphQLFieldDefinition field, GraphQLInputType type) {
        builder.field(newInputObjectField().name(field.getName()).type(type));
    }
}