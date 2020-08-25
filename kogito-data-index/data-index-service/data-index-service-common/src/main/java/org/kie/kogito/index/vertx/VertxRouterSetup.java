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

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import graphql.GraphQL;
import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.vertx.http.runtime.security.QuarkusHttpUser;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpHeaders;
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

@ApplicationScoped
public class VertxRouterSetup {

    @Inject
    @ConfigProperty(name = "quarkus.oidc.enabled", defaultValue = "false")
    Boolean authEnabled;

    @Inject
    @ConfigProperty(name = "kogito.data-index.vertx-graphql.ui.path", defaultValue = "/graphiql")
    String graphUIPath;

    @Inject
    GraphQL graphQL;

    void setupRouter(@Observes Router router) {
        GraphiQLHandler graphiQLHandler = GraphiQLHandler.create(new GraphiQLHandlerOptions().setEnabled(true));
        if (Boolean.TRUE.equals(authEnabled)) {
            addGraphiqlRequestHeader(graphiQLHandler);
        }
        router.route(graphUIPath + "/*").handler(graphiQLHandler);
        router.route().handler(LoggerHandler.create());
        router.route().handler(StaticHandler.create());
        router.route().handler(FaviconHandler.create());
        router.route("/").handler(ctx -> ctx.response().putHeader("location", graphUIPath + "/").setStatusCode(302).end());
        router.route("/graphql").handler(ApolloWSHandler.create(graphQL));
        router.route("/graphql").handler(GraphQLHandler.create(graphQL, new GraphQLHandlerOptions()));
    }

    protected void addGraphiqlRequestHeader(GraphiQLHandler graphiQLHandler) {
        graphiQLHandler.graphiQLRequestHeaders(rc -> MultiMap.caseInsensitiveMultiMap().add(HttpHeaders.AUTHORIZATION, "Bearer " + getCurrentAccessToken(rc))
        );
    }

    private String getCurrentAccessToken(RoutingContext routingContext) {
        return Optional.ofNullable(routingContext.user())
                .map(user -> ((QuarkusHttpUser) user).getSecurityIdentity())
                .map(identity -> identity.getCredential(AccessTokenCredential.class))
                .map(AccessTokenCredential::getToken)
                .orElse("");
    }
}

