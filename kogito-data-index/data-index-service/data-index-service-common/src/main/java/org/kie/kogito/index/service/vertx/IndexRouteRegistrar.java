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

import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.runtime.StartupEvent;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.FileSystemAccess;
import io.vertx.ext.web.handler.StaticHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class IndexRouteRegistrar {

    public static final String CONFIG_KEY_UI_PATH = "kogito.data-index.ui.path";

    @ConfigProperty(name = CONFIG_KEY_UI_PATH, defaultValue = "")
    Optional<String> directory;

    /**
     * Configures the UI Page in case we find a directory to serve.
     * This path MUST have an index.html file to serve as the static index page for the Data Index service.
     * / -> points to the custom /ui/index.html
     * /index.html -> points to the custom /ui/index.html
     * /ui/* -> points to our directory and serves everything
     */
    void onStart(@Observes StartupEvent ev, Router router) {
        directory.ifPresent(dir -> {
            router.route("/")
                    .handler(ctx -> ctx.response()
                            .putHeader("Location", "/ui/index.html")
                            .setStatusCode(302)
                            .end());

            final String normalized = dir.endsWith("/") ? dir.substring(0, dir.length() - 1) : dir;
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
        });
    }
}
