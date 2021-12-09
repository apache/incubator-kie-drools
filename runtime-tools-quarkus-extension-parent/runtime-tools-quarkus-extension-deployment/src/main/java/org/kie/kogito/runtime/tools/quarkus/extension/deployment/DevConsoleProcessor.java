/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.runtime.tools.quarkus.extension.deployment;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.kie.kogito.runtime.tools.quarkus.extension.runtime.user.UserInfoSupplier;

import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.deployment.builditem.SystemPropertyBuildItem;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.deployment.util.WebJarUtil;
import io.quarkus.devconsole.spi.DevConsoleRouteBuildItem;
import io.quarkus.devconsole.spi.DevConsoleRuntimeTemplateInfoBuildItem;
import io.quarkus.maven.dependency.ResolvedDependency;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.runtime.devmode.DevConsoleRecorder;

import static org.kie.kogito.runtime.tools.quarkus.extension.runtime.dataindex.DataIndexClient.DATA_INDEX_CONFIG_KEY;

public class DevConsoleProcessor {

    private static final String DATA_INDEX_CLIENT_KEY = "quarkus.rest-client.\"" + DATA_INDEX_CONFIG_KEY + "\".url";
    private static final String STATIC_RESOURCES_PATH = "dev-static/";

    @BuildStep(onlyIf = IsDevelopment.class)
    public DevConsoleRuntimeTemplateInfoBuildItem collectUsersInfo() {
        return new DevConsoleRuntimeTemplateInfoBuildItem("userInfo",
                new UserInfoSupplier());
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    public void setUpDataIndexServiceURL(
            final DevConsoleBuildTimeConfig config,
            final BuildProducer<SystemPropertyBuildItem> systemProperties) throws IOException {

        systemProperties.produce(new SystemPropertyBuildItem(DATA_INDEX_CLIENT_KEY, config.dataIndexUrl));
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    @Record(ExecutionTime.RUNTIME_INIT)
    public void deployStaticResources(final DevConsoleRecorder recorder,
            final List<DevConsoleRouteBuildItem> routes,
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
}
