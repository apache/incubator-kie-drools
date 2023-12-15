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
package org.kie.kogito.mgmt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class VertxRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxRouter.class);

    @Location("index")
    Template indexTemplate;

    private String index;

    @PostConstruct
    public void init() {
        index = indexTemplate.render();
    }

    void setupRouter(@Observes Router router) {
        router.route("/").handler(ctx -> ctx.response().putHeader("location", "/ProcessInstances/").setStatusCode(302).end());
        router.route("/Process*").handler(this::handle);
        router.route("/DomainExplorer*").handler(this::handle);
        router.route("/JobsManagement*").handler(this::handle);
        router.route().handler(StaticHandler.create());
    }

    public void handle(RoutingContext context) {
        try {
            context.response()
                    .putHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf8")
                    .end(index);
        } catch (Exception ex) {
            LOGGER.error("Error handling index.html", ex);
            context.fail(500, ex);
        }
    }
}
