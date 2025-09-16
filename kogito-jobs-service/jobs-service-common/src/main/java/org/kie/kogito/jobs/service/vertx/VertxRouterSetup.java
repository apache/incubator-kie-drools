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
package org.kie.kogito.jobs.service.vertx;

import java.io.File;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.runtime.StartupEvent;
import io.vertx.ext.web.handler.FileSystemAccess;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.handler.StaticHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class VertxRouterSetup {

    public static final String CONFIG_KEY_UI_PATH = "kogito.jobs-service.ui.path";

    @ConfigProperty(name = CONFIG_KEY_UI_PATH, defaultValue = "")
    Optional<String> directory;

    /**
     * Configures the UI Page in case we find a directory to serve.
     * This path MUST have an index.html file to serve as the static index page for the Jobs service.
     * / -> redirects to the to the custom /ui/index.html.
     * /index.html -> redirects to the to the custom /ui/index.html.
     * /index.html/ -> redirects to the /ui/index.html.
     * /ui/* -> points to the ui directory and serves everything.
     */
    void onStart(@Observes StartupEvent ev, Router router) {
        directory.ifPresent(dir -> {
            router.routeWithRegex("^/(index\\.html/?)?$")
                    .handler(ctx -> {
                        ctx.response()
                                .putHeader("Location", "/ui/index.html")
                                .setStatusCode(302)
                                .endAndForget();
                    });

            final String normalized = dir.endsWith("/") ? dir.substring(0, dir.length() - 1) : dir;
            final FileSystemAccess fsa = new File(normalized).isAbsolute() ? FileSystemAccess.ROOT : FileSystemAccess.RELATIVE;
            final StaticHandler handler = StaticHandler.create(fsa, normalized)
                    .setDefaultContentEncoding("utf-8")
                    .setDirectoryListing(false)
                    .setAlwaysAsyncFS(true)
                    .setIndexPage("index.html")
                    .setCacheEntryTimeout(86400) // cache for one day
                    .setEnableFSTuning(true);
            router.route("/ui/*").handler(handler);
        });
    }
}
