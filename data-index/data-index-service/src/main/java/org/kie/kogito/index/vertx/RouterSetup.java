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

import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.FaviconHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class RouterSetup {

    @Inject
    GraphQLHandler graphQLHandler;
    
    @Inject
    @ConfigProperty(name = "kogito.allowedOriginPattern", defaultValue = "*")
    String allowedOriginPattern;
    
    private AtomicBoolean routesAdded = new AtomicBoolean(false);

    void setupRouter(@Observes Router router) {
        //Avoid setting up routes twice
        if (!routesAdded.get()) {
            router.route("/graphql").handler(CorsHandler.create(allowedOriginPattern).allowedMethod(HttpMethod.POST).allowedHeader("content-type"));
            router.route("/graphql").handler(graphQLHandler);
            router.route("/").handler(ctx -> ctx.reroute("/graphql"));
            router.route().handler(LoggerHandler.create());
            router.route().handler(StaticHandler.create());
            router.route().handler(FaviconHandler.create());
            routesAdded.set(true);
        }
    }
}
