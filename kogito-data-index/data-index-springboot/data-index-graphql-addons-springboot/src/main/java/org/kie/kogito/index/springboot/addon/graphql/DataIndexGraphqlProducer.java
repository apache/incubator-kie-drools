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

package org.kie.kogito.index.springboot.addon.graphql;

import java.util.List;
import java.util.Optional;

import org.kie.kogito.index.addon.graphql.GraphQLAddonSchemaManagerImpl;
import org.kie.kogito.index.api.DateTimeCoercing;
import org.kie.kogito.index.api.DefaultDateTimeCoercing;
import org.kie.kogito.index.api.KogitoRuntimeClient;
import org.kie.kogito.index.graphql.GraphQLInstrumentation;
import org.kie.kogito.index.graphql.GraphQLScalarTypeProducer;
import org.kie.kogito.index.graphql.GraphQLSchemaManager;
import org.kie.kogito.index.storage.DataIndexStorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.GraphQlSource;

import graphql.execution.instrumentation.Instrumentation;
import graphql.schema.GraphQLScalarType;

@Configuration
public class DataIndexGraphqlProducer {

    @Bean
    public GraphQlSource graphQLSource(GraphQLSchemaManager graphQLSchemaManager, List<Instrumentation> instrumentations) {
        return GraphQlSource.builder(graphQLSchemaManager.getGraphQLSchema())
                .instrumentation(instrumentations)
                .build();
    }

    @Bean
    public GraphQLScalarType createGraphQLScalarType(Optional<DateTimeCoercing> dateTimeCoercing) {
        return new GraphQLScalarTypeProducer(dateTimeCoercing.orElse(new DefaultDateTimeCoercing())).dateTimeScalar();
    }

    @Bean
    public GraphQLSchemaManager graphQLSchemaManager(DataIndexStorageService storageService, GraphQLScalarType dateTimeScalarType,
            KogitoRuntimeClient dataIndexApiExecutor) {
        return new GraphQLAddonSchemaManagerImpl(storageService, dateTimeScalarType, dataIndexApiExecutor);
    }

    @Bean
    public GraphQLInstrumentation createGraphQLInstrumentation(GraphQLSchemaManager schemaManager) {
        return new GraphQLInstrumentation(schemaManager);
    }

}
