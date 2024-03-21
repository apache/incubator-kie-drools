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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.kie.kogito.quarkus.extensions.spi.deployment.KogitoDataIndexServiceAvailableBuildItem;
import org.kie.kogito.quarkus.extensions.spi.deployment.TrustyServiceAvailableBuildItem;
import org.kie.kogito.runtime.tools.quarkus.extension.deployment.data.UserInfo;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.config.DevConsoleRuntimeConfig;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.config.DevUIStaticArtifactsRecorder;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.rpc.JBPMDevuiJsonRPCService;

import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.*;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.deployment.util.WebJarUtil;
import io.quarkus.devui.spi.JsonRPCProvidersBuildItem;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;
import io.quarkus.maven.dependency.ResolvedDependency;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.runtime.management.ManagementInterfaceBuildTimeConfig;

public class DevConsoleProcessor {

    private static final String STATIC_RESOURCES_PATH = "dev-static/";
    private static final String BASE_RELATIVE_URL = "/q/dev-ui/org.jbpm.jbpm-quarkus-devui";
    private static final String DATA_INDEX_CAPABILITY = "org.kie.kogito.data-index";

    @SuppressWarnings("unused")
    @BuildStep(onlyIf = IsDevelopment.class)
    @Record(ExecutionTime.RUNTIME_INIT)
    public void deployStaticResources(final DevUIStaticArtifactsRecorder devUIStaticArtifactsRecorder,
            final CurateOutcomeBuildItem curateOutcomeBuildItem,
            final LiveReloadBuildItem liveReloadBuildItem,
            final LaunchModeBuildItem launchMode,
            final ShutdownContextBuildItem shutdownContext,
            final BuildProducer<RouteBuildItem> routeBuildItemBuildProducer) throws IOException {
        ResolvedDependency devConsoleResourcesArtifact = WebJarUtil.getAppArtifact(curateOutcomeBuildItem,
                "org.jbpm",
                "jbpm-quarkus-devui-deployment");

        Path devConsoleStaticResourcesDeploymentPath = WebJarUtil.copyResourcesForDevOrTest(
                liveReloadBuildItem,
                curateOutcomeBuildItem,
                launchMode,
                devConsoleResourcesArtifact,
                STATIC_RESOURCES_PATH,
                true);

        routeBuildItemBuildProducer.produce(new RouteBuildItem.Builder()
                .route(BASE_RELATIVE_URL + "/resources/*")
                .handler(devUIStaticArtifactsRecorder.handler(devConsoleStaticResourcesDeploymentPath.toString(),
                        shutdownContext))
                .build());

        routeBuildItemBuildProducer.produce(new RouteBuildItem.Builder()
                .route(BASE_RELATIVE_URL + "/*")
                .handler(devUIStaticArtifactsRecorder.handler(devConsoleStaticResourcesDeploymentPath.toString(),
                        shutdownContext))
                .build());
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    public JsonRPCProvidersBuildItem createJsonRPCServiceForJBPMDevUi() {
        return new JsonRPCProvidersBuildItem(JBPMDevuiJsonRPCService.class);
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    public CardPageBuildItem pages(
            final NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
            final DevConsoleRuntimeConfig devConsoleRuntimeConfig,
            final ManagementInterfaceBuildTimeConfig managementInterfaceBuildTimeConfig,
            final LaunchModeBuildItem launchModeBuildItem,
            final ConfigurationBuildItem configurationBuildItem,
            final List<SystemPropertyBuildItem> systemPropertyBuildItems,
            final Optional<KogitoDataIndexServiceAvailableBuildItem> dataIndexServiceAvailableBuildItem,
            final Optional<TrustyServiceAvailableBuildItem> trustyServiceAvailableBuildItem,
            final Capabilities capabilities) {

        CardPageBuildItem cardPageBuildItem = new CardPageBuildItem();

        String uiPath = nonApplicationRootPathBuildItem.resolveManagementPath(BASE_RELATIVE_URL,
                managementInterfaceBuildTimeConfig, launchModeBuildItem, true);

        String openapiPath = getProperty(configurationBuildItem, systemPropertyBuildItems, "quarkus.smallrye-openapi.path");
        String devUIUrl = getProperty(configurationBuildItem, systemPropertyBuildItems, "kogito.dev-ui.url");
        String dataIndexUrl = getProperty(configurationBuildItem, systemPropertyBuildItems, "kogito.data-index.url");
        String trustyServiceUrl = getProperty(configurationBuildItem, systemPropertyBuildItems, "kogito.trusty.http.url");

        cardPageBuildItem.addBuildTimeData("extensionBasePath", uiPath);
        cardPageBuildItem.addBuildTimeData("openapiPath", openapiPath);
        cardPageBuildItem.addBuildTimeData("devUIUrl", devUIUrl);
        cardPageBuildItem.addBuildTimeData("dataIndexUrl", dataIndexUrl);
        cardPageBuildItem.addBuildTimeData("isTracingEnabled", trustyServiceAvailableBuildItem.isPresent());
        cardPageBuildItem.addBuildTimeData("trustyServiceUrl", trustyServiceUrl);
        cardPageBuildItem.addBuildTimeData("userData", readUsersInfo(devConsoleRuntimeConfig));

        if (dataIndexServiceAvailableBuildItem.isPresent() || capabilities.isPresent(DATA_INDEX_CAPABILITY)) {
            cardPageBuildItem.addPage(Page.webComponentPageBuilder()
                    .componentLink("qwc-jbpm-quarkus-devui.js")
                    .metadata("page", "Processes")
                    .title("Process Instances")
                    .icon("font-awesome-solid:diagram-project")
                    .dynamicLabelJsonRPCMethodName("queryProcessInstancesCount"));

            cardPageBuildItem.addPage(Page.webComponentPageBuilder()
                    .componentLink("qwc-jbpm-quarkus-devui.js")
                    .metadata("page", "TaskInbox")
                    .title("Tasks")
                    .icon("font-awesome-solid:bars-progress")
                    .dynamicLabelJsonRPCMethodName("queryTasksCount"));

            cardPageBuildItem.addPage(Page.webComponentPageBuilder()
                    .componentLink("qwc-jbpm-quarkus-devui.js")
                    .metadata("page", "JobsManagement")
                    .title("Jobs")
                    .icon("font-awesome-solid:clock")
                    .dynamicLabelJsonRPCMethodName("queryJobsCount"));

            cardPageBuildItem.addPage(Page.webComponentPageBuilder()
                    .componentLink("qwc-jbpm-quarkus-devui.js")
                    .metadata("page", "Forms")
                    .title("Forms")
                    .icon("font-awesome-solid:table-cells")
                    .dynamicLabelJsonRPCMethodName("getFormsCount"));
        }

        if (trustyServiceAvailableBuildItem.isPresent()) {
            cardPageBuildItem.addPage(Page.webComponentPageBuilder()
                    .componentLink("qwc-jbpm-quarkus-devui.js")
                    .metadata("page", "Audit")
                    .title("Audit investigation")
                    .icon("font-awesome-solid:gauge-high"));
        }

        return cardPageBuildItem;
    }

    private Collection<UserInfo> readUsersInfo(DevConsoleRuntimeConfig devConsoleRuntimeConfig) {
        if (devConsoleRuntimeConfig.userConfigByUser.isEmpty()) {
            return Collections.emptyList();
        }

        return devConsoleRuntimeConfig.userConfigByUser.entrySet().stream()
                .map(entry -> new UserInfo(entry.getKey(), entry.getValue().groups))
                .collect(Collectors.toList());
    }

    private static String getProperty(ConfigurationBuildItem configurationBuildItem,
            List<SystemPropertyBuildItem> systemPropertyBuildItems, String propertyKey) {

        String propertyValue = configurationBuildItem
                .getReadResult()
                .getAllBuildTimeValues()
                .get(propertyKey);

        if (propertyValue == null) {
            propertyValue = configurationBuildItem
                    .getReadResult()
                    .getBuildTimeRunTimeValues()
                    .get(propertyKey);
        } else {
            return propertyValue;
        }

        if (propertyValue == null) {
            propertyValue = configurationBuildItem
                    .getReadResult()
                    .getRunTimeDefaultValues()
                    .get(propertyKey);
        }

        if (propertyValue != null) {
            return propertyValue;
        }

        return systemPropertyBuildItems.stream().filter(property -> property.getKey().equals(propertyKey))
                .findAny()
                .map(SystemPropertyBuildItem::getValue).orElse(null);
    }
}
