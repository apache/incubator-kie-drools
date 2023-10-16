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
package org.kie.kogito.runtime.tools.quarkus.extension.deployment;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.kie.kogito.quarkus.extensions.spi.deployment.KogitoDataIndexServiceAvailableBuildItem;
import org.kie.kogito.quarkus.extensions.spi.deployment.TrustyServiceAvailableBuildItem;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.config.DevConsoleRuntimeConfig;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.user.UserInfoSupplier;

import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.deployment.util.WebJarUtil;
import io.quarkus.devconsole.spi.DevConsoleTemplateInfoBuildItem;
import io.quarkus.maven.dependency.ResolvedDependency;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.runtime.devmode.DevConsoleRecorder;

public class DevConsoleProcessor {

    private static final String STATIC_RESOURCES_PATH = "dev-static/";
    private static final String DATA_INDEX_CAPABILITY = "org.kie.kogito.data-index";

    @SuppressWarnings("unused")
    @BuildStep(onlyIf = IsDevelopment.class)
    public void collectUsersInfo(final BuildProducer<DevConsoleTemplateInfoBuildItem> devConsoleTemplateInfoBuildItemBuildProducer,
            final DevConsoleRuntimeConfig devConsoleRuntimeConfig) {
        devConsoleTemplateInfoBuildItemBuildProducer.produce(new DevConsoleTemplateInfoBuildItem("userInfo",
                new UserInfoSupplier(devConsoleRuntimeConfig.userConfigByUser).get()));
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    @Record(ExecutionTime.RUNTIME_INIT)
    public void deployStaticResources(final DevConsoleRecorder recorder,
            final CurateOutcomeBuildItem curateOutcomeBuildItem,
            final LiveReloadBuildItem liveReloadBuildItem,
            final LaunchModeBuildItem launchMode,
            final ShutdownContextBuildItem shutdownContext,
            final BuildProducer<RouteBuildItem> routeBuildItemBuildProducer) throws IOException {
        ResolvedDependency devConsoleResourcesArtifact = WebJarUtil.getAppArtifact(curateOutcomeBuildItem,
                "org.kie.kogito",
                "runtime-tools-quarkus-extension-deployment");

        Path devConsoleStaticResourcesDeploymentPath = WebJarUtil.copyResourcesForDevOrTest(
                liveReloadBuildItem,
                curateOutcomeBuildItem,
                launchMode,
                devConsoleResourcesArtifact,
                STATIC_RESOURCES_PATH,
                true);

        routeBuildItemBuildProducer.produce(new RouteBuildItem.Builder()
                .route("/q/dev/org.kie.kogito.runtime-tools-quarkus-extension/resources/*")
                .handler(recorder.devConsoleHandler(devConsoleStaticResourcesDeploymentPath.toString(),
                        shutdownContext))
                .build());
    }

    @SuppressWarnings("unused")
    @BuildStep(onlyIf = IsDevelopment.class)
    public void isProcessEnabled(BuildProducer<DevConsoleTemplateInfoBuildItem> devConsoleTemplateInfoBuildItemBuildProducer,
            Optional<KogitoDataIndexServiceAvailableBuildItem> dataIndexServiceAvailableBuildItem,
            Capabilities capabilities) {
        devConsoleTemplateInfoBuildItemBuildProducer.produce(new DevConsoleTemplateInfoBuildItem("isProcessEnabled",
                dataIndexServiceAvailableBuildItem.isPresent() || capabilities.isPresent(DATA_INDEX_CAPABILITY)));
    }

    @SuppressWarnings("unused")
    @BuildStep(onlyIf = IsDevelopment.class)
    public void isTracingEnabled(final BuildProducer<DevConsoleTemplateInfoBuildItem> devConsoleTemplateInfoBuildItemBuildProducer,
            final Optional<TrustyServiceAvailableBuildItem> trustyServiceAvailableBuildItem) {
        devConsoleTemplateInfoBuildItemBuildProducer.produce(new DevConsoleTemplateInfoBuildItem("isTracingEnabled",
                trustyServiceAvailableBuildItem.isPresent()));
    }
}
