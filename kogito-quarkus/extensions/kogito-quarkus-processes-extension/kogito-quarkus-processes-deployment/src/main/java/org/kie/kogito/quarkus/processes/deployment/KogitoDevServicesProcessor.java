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

package org.kie.kogito.quarkus.processes.deployment;

import org.kie.kogito.quarkus.processes.devservices.DevModeWorkflowLogger;
import org.kie.kogito.quarkus.workflow.deployment.AbstractDevServicesProcessor;
import org.kie.kogito.quarkus.workflow.deployment.config.KogitoWorkflowBuildTimeConfig;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;

/**
 * Starts a Data Index as dev service if needed.
 */
public class KogitoDevServicesProcessor extends AbstractDevServicesProcessor {

    @BuildStep
    public void logger(BuildProducer<AdditionalBeanBuildItem> additionalBean, LaunchModeBuildItem launchMode, KogitoWorkflowBuildTimeConfig config) {
        if (shouldInclude(launchMode, config)) {
            additionalBean.produce(AdditionalBeanBuildItem.builder().addBeanClass(DevModeWorkflowLogger.class).setUnremovable().setDefaultScope(DotNames.APPLICATION_SCOPED).build());
        }
    }

}
