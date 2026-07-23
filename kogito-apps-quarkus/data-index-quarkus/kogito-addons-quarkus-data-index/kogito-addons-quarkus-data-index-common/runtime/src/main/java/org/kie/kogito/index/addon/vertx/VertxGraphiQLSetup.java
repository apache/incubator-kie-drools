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
package org.kie.kogito.index.addon.vertx;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.arc.properties.IfBuildProperty;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.graphql.GraphiQLHandler;
import io.vertx.ext.web.handler.graphql.GraphiQLHandlerOptions;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
@IfBuildProperty(name = "quarkus.kogito.data-index.graphql.ui.always-include", stringValue = "true")
public class VertxGraphiQLSetup {

    @Inject
    @ConfigProperty(name = "quarkus.http.non-application-root-path")
    String path;

    @Inject
    @ConfigProperty(name = "quarkus.http.root-path")
    String rootPath;

    void setupRouter(@Observes Router router) {
        GraphiQLHandler graphiQLHandler = GraphiQLHandler.create(new GraphiQLHandlerOptions().setEnabled(true));
        router.route(rootPath + path + "/graphql-ui/*").handler(graphiQLHandler);
    }

}
