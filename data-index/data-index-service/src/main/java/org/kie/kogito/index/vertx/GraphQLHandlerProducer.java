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

package org.kie.kogito.index.vertx;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import graphql.GraphQL;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandlerOptions;
import io.vertx.ext.web.handler.graphql.GraphiQLOptions;
import org.kie.kogito.index.graphql.GraphQLSchemaManager;
import org.kie.kogito.index.graphql.GraphQLInstrumentation;

@ApplicationScoped
public class GraphQLHandlerProducer {

    @Inject
    GraphQLInstrumentation instrumentation;

    @Inject
    GraphQLSchemaManager manager;

    @Produces
    public GraphQLHandler createHandler() {
        GraphQL graphQL = GraphQL.newGraphQL(manager.getGraphQLSchema()).instrumentation(instrumentation).build();
        GraphQLHandlerOptions options = new GraphQLHandlerOptions().setGraphiQLOptions(new GraphiQLOptions().setEnabled(true));
        return GraphQLHandler.create(graphQL, options);
    }
}
