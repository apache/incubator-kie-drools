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

package org.kie.kogito.runtime.tools.quarkus.extension.runtime.config;

import java.util.ArrayList;
import java.util.List;

import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.vertx.http.runtime.devmode.FileSystemStaticHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class DevUIStaticArtifactsRecorder {

    public Handler<RoutingContext> handler(String deploymentArtifactPath, ShutdownContext shutdownContext) {
        List<FileSystemStaticHandler.StaticWebRootConfiguration> webRootConfigurations = new ArrayList<>();
        webRootConfigurations.add(
                new FileSystemStaticHandler.StaticWebRootConfiguration(deploymentArtifactPath, ""));

        FileSystemStaticHandler fileSystemStaticHandler = new FileSystemStaticHandler(webRootConfigurations);

        shutdownContext.addShutdownTask(new ShutdownContext.CloseRunnable(fileSystemStaticHandler));

        return fileSystemStaticHandler;
    }
}
