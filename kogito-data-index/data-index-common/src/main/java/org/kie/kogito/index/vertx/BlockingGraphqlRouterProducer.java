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
package org.kie.kogito.index.vertx;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.arc.properties.IfBuildProperty;
import io.quarkus.vertx.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.graphql.ApolloWSHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandlerOptions;

import graphql.GraphQL;

@ApplicationScoped
@IfBuildProperty(name = "kogito.data-index.blocking", stringValue = "true")
public class BlockingGraphqlRouterProducer {

    @Inject
    GraphQL graphQL;

    GraphQLHandler graphQLHandler;

    ApolloWSHandler apolloWSHandler;

    @PostConstruct
    public void init() {
        graphQLHandler = GraphQLHandler.create(graphQL, new GraphQLHandlerOptions());
        apolloWSHandler = ApolloWSHandler.create(graphQL);
    }

    @Route(path = "/graphql", type = Route.HandlerType.BLOCKING, order = 1)
    public void apolloWSHandler(RoutingContext rc) {
        apolloWSHandler.handle(rc);
    }

    @Route(path = "/graphql", type = Route.HandlerType.BLOCKING, order = 2)
    public void graphQLHandler(RoutingContext rc) {
        graphQLHandler.handle(rc);
    }

}
