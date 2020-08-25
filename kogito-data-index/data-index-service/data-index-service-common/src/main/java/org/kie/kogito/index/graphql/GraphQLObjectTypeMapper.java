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

package org.kie.kogito.index.graphql;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import graphql.Scalars;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import org.kie.kogito.index.model.KogitoMetadata;
import org.kie.kogito.persistence.api.proto.AttributeDescriptor;
import org.kie.kogito.persistence.api.proto.DomainDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static org.kie.kogito.index.Constants.KOGITO_DOMAIN_ATTRIBUTE;

public class GraphQLObjectTypeMapper implements Function<DomainDescriptor, GraphQLObjectType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLObjectTypeMapper.class);

    private GraphQLSchema schema;
    private Map<String, GraphQLType> additionalTypes;
    private Map<String, DomainDescriptor> allTypes;

    public GraphQLObjectTypeMapper(GraphQLSchema schema, Map<String, GraphQLType> additionalTypes, Map<String, DomainDescriptor> allTypes) {
        this.schema = schema;
        this.additionalTypes = additionalTypes;
        this.allTypes = allTypes;
    }

    public static String getTypeName(String type) {
        return type.contains(".") ? type.substring(type.lastIndexOf('.') + 1) : type;
    }

    @Override
    public GraphQLObjectType apply(DomainDescriptor domain) {
        LOGGER.debug("GraphQL mapping domain: {}", domain);
        String typeName = getTypeName(domain.getTypeName());
        GraphQLObjectType existingType = (GraphQLObjectType) schema.getType(typeName);
        if (existingType == null) {
            GraphQLObjectType.Builder builder = GraphQLObjectType.newObject().name(typeName);
            build(domain).accept(builder);
            return builder.build();
        } else {
            return existingType.transform(builder -> {
                builder.clearFields();
                build(domain).accept(builder);
            });
        }
    }

    private Consumer<GraphQLObjectType.Builder> build(DomainDescriptor domain) {
        return builder ->
                domain.getAttributes().forEach(field -> {
                    LOGGER.debug("GraphQL mapping field: {}", field);
                    if (KogitoMetadata.class.getName().equals(field.getTypeName())) {
                        builder.field(newFieldDefinition().name(KOGITO_DOMAIN_ATTRIBUTE).type(schema.getObjectType("KogitoMetadata"))).build();
                    } else {
                        GraphQLOutputType type;
                        switch (field.getTypeName()) {
                            case "java.lang.Integer":
                                type = Scalars.GraphQLInt;
                                break;
                            case "java.lang.Long":
                                type = Scalars.GraphQLLong;
                                break;
                            case "java.lang.String":
                            case "java.util.Date":
                                type = Scalars.GraphQLString;
                                break;
                            case "java.lang.Boolean":
                                type = Scalars.GraphQLBoolean;
                                break;
                            default:
                                type = getGraphQLType(field, schema, additionalTypes, allTypes);
                        }
                        builder.field(newFieldDefinition().name(field.getName()).type(type));
                    }
                });
    }

    private GraphQLOutputType getGraphQLType(AttributeDescriptor attribute, GraphQLSchema schema, Map<String, GraphQLType> additionalTypes, Map<String, DomainDescriptor> allTypes) {
        String typeName = getTypeName(attribute.getTypeName());
        GraphQLType type = schema.getType(typeName);
        if (type == null) {
            type = additionalTypes.computeIfAbsent(typeName, k -> new GraphQLObjectTypeMapper(schema, additionalTypes, allTypes).apply(allTypes.get(typeName)));
        }
        return (GraphQLOutputType) type;
    }
}