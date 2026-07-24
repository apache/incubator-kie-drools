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
package org.kie.kogito.index.vertx;

import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.index.service.vertx.VertxRouterSetupHelper;

import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.vertx.http.runtime.security.QuarkusHttpUser;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class QuarkusVertxRouterSetup {

    @ConfigProperty(name = "quarkus.oidc.enabled", defaultValue = "false")
    Boolean authEnabled;

    @ConfigProperty(name = VertxRouterSetupHelper.GRAPH_UI_PATH_PROPERTY, defaultValue = "/graphiql")
    String graphUIPath;

    @ConfigProperty(name = VertxRouterSetupHelper.UI_PATH_PROPERTY)
    Optional<String> indexUIPath;

    @Inject
    Vertx vertx;

    void setupRouter(@Observes Router router) {
        VertxRouterSetupHelper.setupRouter(vertx, router, graphUIPath, indexUIPath.orElse(""), authEnabled, this::getCurrentAccessToken);
    }

    private String getCurrentAccessToken(RoutingContext routingContext) {
        return Optional.ofNullable(routingContext.user())
                .map(user -> ((QuarkusHttpUser) user).getSecurityIdentity())
                .map(identity -> identity.getCredential(AccessTokenCredential.class))
                .map(AccessTokenCredential::getToken)
                .orElse("");
    }
}
