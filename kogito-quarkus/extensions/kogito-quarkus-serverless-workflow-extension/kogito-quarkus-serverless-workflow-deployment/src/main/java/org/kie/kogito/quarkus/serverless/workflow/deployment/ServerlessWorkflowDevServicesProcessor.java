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

package org.kie.kogito.quarkus.serverless.workflow.deployment;

import org.kie.kogito.quarkus.processes.deployment.KogitoBuildTimeConfig;
import org.kie.kogito.serverless.workflow.devservices.DevModeServerlessWorkflowLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;

public class ServerlessWorkflowDevServicesProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerlessWorkflowDevServicesProcessor.class);

    @BuildStep
    public void logger(BuildProducer<AdditionalBeanBuildItem> additionalBean, LaunchModeBuildItem launchMode, KogitoBuildTimeConfig config) {
        if (shouldInclude(launchMode, config)) {
            additionalBean.produce(AdditionalBeanBuildItem.builder().addBeanClass(DevModeServerlessWorkflowLogger.class).setUnremovable().setDefaultScope(DotNames.APPLICATION_SCOPED).build());
        }
    }

    private static boolean shouldInclude(LaunchModeBuildItem launchMode, KogitoBuildTimeConfig config) {
        return launchMode.getLaunchMode().isDevOrTest() || config.alwaysInclude;
    }

}
