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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import graphql.Scalars;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import org.kie.kogito.index.domain.AttributeDescriptor;
import org.kie.kogito.index.domain.DomainDescriptor;
import org.kie.kogito.index.event.DomainModelRegisteredEvent;
import org.kie.kogito.index.model.ProcessInstanceMeta;
import org.kie.kogito.index.model.UserTaskInstanceMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static graphql.schema.FieldCoordinates.coordinates;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLNonNull.nonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.kie.kogito.index.Constants.PROCESS_INSTANCES_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.Constants.USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE;

@ApplicationScoped
public class GraphQLProtoSchemaMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLProtoSchemaMapper.class);

    @Inject
    GraphQLSchemaManager schemaManager;

    private static String getTypeName(String type) {
        return type.contains(".") ? type.substring(type.lastIndexOf(".") + 1) : type;
    }

    public void onDomainModelRegisteredEvent(@Observes DomainModelRegisteredEvent event) {
        LOGGER.debug("Received new domain event: {}", event);
        GraphQLSchema schema = schemaManager.getGraphQLSchema();
        schemaManager.transform(builder -> {
            builder.clearAdditionalTypes();
            Map<String, DomainDescriptor> map = event.getAdditionalTypes().stream().collect(toMap(desc -> getTypeName(desc.getTypeName()), desc -> desc));
            Map<String, GraphQLType> additionalTypes = new HashMap<>();
            GraphQLObjectType rootType = new GraphQLObjectTypeMapper(schema, additionalTypes, map).apply(event.getDomainDescriptor());
            additionalTypes.put(rootType.getName(), rootType);
            Set<GraphQLType> newTypes = additionalTypes.entrySet().stream().map(entry -> entry.getValue()).collect(toSet());
            newTypes.addAll(schema.getAdditionalTypes().stream().filter(type -> additionalTypes.containsKey(type.getName()) == false).collect(toSet()));
            LOGGER.debug("New GraphQL types: {}", newTypes);
            builder.additionalTypes(newTypes);

            GraphQLObjectType query = schema.getQueryType();

            //Should use extend instead?
            query = query.transform(qBuilder -> {
                if (qBuilder.hasField(rootType.getName())) {
                    qBuilder.clearFields();
                    qBuilder.fields(schema.getQueryType().getFieldDefinitions().stream().filter(field -> rootType.getName().equals(field.getName()) == false).collect(toList()));
                }
                GraphQLArgument argument = newArgument().name("query").type(Scalars.GraphQLString).build();
                qBuilder.field(newFieldDefinition().name(rootType.getName()).type(GraphQLList.list(rootType)).argument(argument));
            });
            builder.query(query);

            GraphQLObjectType subscription = schema.getSubscriptionType();
            subscription = subscription.transform(sBuilder -> {
                sBuilder.field(newFieldDefinition().name(rootType.getName() + "Added").type(nonNull(rootType)).build());
                sBuilder.field(newFieldDefinition().name(rootType.getName() + "Updated").type(nonNull(rootType)).build());
            });
            builder.subscription(subscription);

            GraphQLCodeRegistry registry = schema.getCodeRegistry().transform(codeBuilder -> {
                codeBuilder.dataFetcher(coordinates("Query", rootType.getName()), schemaManager.getDomainModelDataFetcher(event.getProcessId()));
                codeBuilder.dataFetcher(coordinates("Subscription", rootType.getName() + "Added"), schemaManager.getDomainModelAddedDataFetcher(event.getProcessId()));
                codeBuilder.dataFetcher(coordinates("Subscription", rootType.getName() + "Updated"), schemaManager.getDomainModelUpdatedDataFetcher(event.getProcessId()));
            });

            builder.codeRegistry(registry);
        });
    }

    private GraphQLOutputType getGraphQLType(AttributeDescriptor attribute, GraphQLSchema schema, Map<String, GraphQLType> additionalTypes, Map<String, DomainDescriptor> allTypes) {
        String typeName = getTypeName(attribute.getTypeName());
        GraphQLType type = schema.getType(typeName);
        if (type == null) {
            type = additionalTypes.get(typeName);
            if (type == null) {
                type = new GraphQLObjectTypeMapper(schema, additionalTypes, allTypes).apply(allTypes.get(typeName));
                additionalTypes.put(typeName, type);
            }
        }
        return (GraphQLOutputType) type;
    }

    private class GraphQLObjectTypeMapper implements Function<DomainDescriptor, GraphQLObjectType> {

        GraphQLSchema schema;
        Map<String, GraphQLType> additionalTypes;
        Map<String, DomainDescriptor> allTypes;

        public GraphQLObjectTypeMapper(GraphQLSchema schema, Map<String, GraphQLType> additionalTypes, Map<String, DomainDescriptor> allTypes) {
            this.schema = schema;
            this.additionalTypes = additionalTypes;
            this.allTypes = allTypes;
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
            return builder -> {
                domain.getAttributes().forEach(field -> {
                    LOGGER.debug("GraphQL mapping field: {}", field);
                    if (ProcessInstanceMeta.class.getName().equals(field.getTypeName())) {
                        builder.field(newFieldDefinition().name(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE).type(GraphQLList.list(schema.getObjectType("ProcessInstanceMeta")))).build();
                    } else if (UserTaskInstanceMeta.class.getName().equals(field.getTypeName())) {
                        builder.field(newFieldDefinition().name(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE).type(GraphQLList.list(schema.getObjectType("UserTaskInstanceMeta")))).build();
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
            };
        }
    }
}
