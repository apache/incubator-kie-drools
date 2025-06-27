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
package org.kie.kogito.index.service.vertx;

import java.util.function.Function;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.FaviconHandler;
import io.vertx.ext.web.handler.FileSystemAccess;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.graphql.GraphiQLHandler;
import io.vertx.ext.web.handler.graphql.GraphiQLHandlerOptions;

public class VertxRouterSetupHelper {

    public static final String UI_PATH_PROPERTY = "kogito.data-index.ui.path";
    public static final String GRAPH_UI_PATH_PROPERTY = "kogito.data-index.vertx-graphql.ui.path";

    private VertxRouterSetupHelper() {
    }

    public static void setupRouter(Vertx vertx, Router router, String graphUIPath, String indexUIPath, boolean authEnabled, Function<RoutingContext, String> accessToken) {
        router.route().handler(LoggerHandler.create());
        GraphiQLHandler graphiQLHandler = GraphiQLHandler.create(vertx, new GraphiQLHandlerOptions().setEnabled(true));
        if (authEnabled) {
            graphiQLHandler.graphiQLRequestHeaders(rc -> MultiMap.caseInsensitiveMultiMap().add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.apply(rc)));
        }
        router.route().handler(BodyHandler.create());
        router.route(graphUIPath + "/*").handler(graphiQLHandler);
        if (indexUIPath.isEmpty()) {
            router.route("/").handler(ctx -> ctx.response().putHeader("location", graphUIPath + "/").setStatusCode(302).end());
        } else {
            router.route("/")
                    .handler(ctx -> ctx.response()
                            .putHeader("Location", "/ui/index.html")
                            .setStatusCode(302)
                            .end());

            final String normalized = indexUIPath.endsWith("/") ? indexUIPath.substring(0, indexUIPath.length() - 1) : indexUIPath;
            final FileSystemAccess fsa = normalized.startsWith("/") ? FileSystemAccess.ROOT : FileSystemAccess.RELATIVE;
            final StaticHandler handler = StaticHandler.create(fsa, normalized)
                    .setDefaultContentEncoding("utf-8")
                    .setDirectoryListing(false)
                    .setAlwaysAsyncFS(true)
                    .setIndexPage("index.html")
                    .setCacheEntryTimeout(86400) // cache for one day
                    .setEnableFSTuning(true);

            router.route("/index.html").handler(handler);
            router.route("/ui/*").handler(handler);
        }
        router.route().handler(FaviconHandler.create(vertx));
    }

}
