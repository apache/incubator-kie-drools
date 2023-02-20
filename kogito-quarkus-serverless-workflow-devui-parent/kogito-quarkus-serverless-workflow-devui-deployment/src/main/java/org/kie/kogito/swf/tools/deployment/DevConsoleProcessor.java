/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.swf.tools.deployment;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.kie.kogito.quarkus.extensions.spi.deployment.KogitoDataIndexServiceAvailableBuildItem;

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

import static org.kie.kogito.swf.tools.dataindex.DataIndexClient.DATA_INDEX_CONFIG_KEY;

public class DevConsoleProcessor {

    private static final String DATA_INDEX_CLIENT_KEY = "quarkus.rest-client.\"" + DATA_INDEX_CONFIG_KEY + "\".url";
    private static final String STATIC_RESOURCES_PATH = "dev-static/";
    private static final String BASE_RELATIVE_URL = "/q/dev/org.kie.kogito.kogito-quarkus-serverless-workflow-devui";
    private static final String DATA_INDEX_CAPABILITY = "org.kie.kogito.data-index";

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
                "kogito-quarkus-serverless-workflow-devui-deployment");

        Path devConsoleStaticResourcesDeploymentPath = WebJarUtil.copyResourcesForDevOrTest(
                liveReloadBuildItem,
                curateOutcomeBuildItem,
                launchMode,
                devConsoleResourcesArtifact,
                STATIC_RESOURCES_PATH,
                true);

        routeBuildItemBuildProducer.produce(new RouteBuildItem.Builder()
                .route(BASE_RELATIVE_URL + "/resources/*")
                .handler(recorder.devConsoleHandler(devConsoleStaticResourcesDeploymentPath.toString(),
                        shutdownContext))
                .build());

        routeBuildItemBuildProducer.produce(new RouteBuildItem.Builder()
                .route(BASE_RELATIVE_URL + "/*")
                .handler(recorder.devConsoleHandler(devConsoleStaticResourcesDeploymentPath.toString(),
                        shutdownContext))
                .build());
    }

    @SuppressWarnings("unused")
    @BuildStep(onlyIf = IsDevelopment.class)
    public void isDataIndexAvailable(BuildProducer<DevConsoleTemplateInfoBuildItem> devConsoleTemplateInfoBuildItemBuildProducer,
            Optional<KogitoDataIndexServiceAvailableBuildItem> dataIndexServiceAvailableBuildItem,
            Capabilities capabilities) {
        devConsoleTemplateInfoBuildItemBuildProducer.produce(new DevConsoleTemplateInfoBuildItem("isDataIndexAvailable",
                dataIndexServiceAvailableBuildItem.isPresent() || capabilities.isPresent(DATA_INDEX_CAPABILITY)));
    }
}
