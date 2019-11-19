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
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import graphql.GraphQL;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.FaviconHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.graphql.ApolloWSHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandlerOptions;
import io.vertx.ext.web.handler.graphql.GraphiQLHandler;
import io.vertx.ext.web.handler.graphql.GraphiQLHandlerOptions;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class VertxRouterSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxRouterSetup.class);

    @Inject
    @ConfigProperty(name = "quarkus.oidc.enabled", defaultValue = "false")
    Boolean authEnabled;

    @Inject
    @ConfigProperty(name = "kogito.dataindex.auth.requiredrole", defaultValue = "none")
    String requiredRole;

    @Inject
    GraphQL graphQL;

    void setupRouter(@Observes Router router) {
        if (authEnabled) {
            router.route().handler(routeContext -> authCheck(routeContext));
        }
        router.route("/graphql").handler(ApolloWSHandler.create(graphQL));
        router.route("/graphql").handler(GraphQLHandler.create(graphQL, new GraphQLHandlerOptions()));
        router.route("/graphiql/*").handler(GraphiQLHandler.create(new GraphiQLHandlerOptions().setEnabled(true)));
        router.route("/").handler(ctx -> ctx.response().putHeader("location", "/graphiql/").setStatusCode(302).end());
        router.route().handler(LoggerHandler.create());
        router.route().handler(StaticHandler.create());
        router.route().handler(FaviconHandler.create());
    }

    public void authCheck(RoutingContext context) {

        if (context.user() != null) {
            context.user().isAuthorized(requiredRole,
                                        res -> {
                                            if (res.succeeded()) {
                                                boolean hasAuthority = res.result();
                                                if (!hasAuthority) {
                                                    LOGGER.error("User does not have the authority");
                                                    context.fail(403);
                                                } else {
                                                    context.next();
                                                }
                                            } else {
                                                LOGGER.error("It was not able to retrieve user authorization ", res.cause());
                                                context.fail(500);
                                            }
                                        });
        } else {
            LOGGER.error("Auth enabled but No user context found");
            context.fail(401);
        }
    }
}
