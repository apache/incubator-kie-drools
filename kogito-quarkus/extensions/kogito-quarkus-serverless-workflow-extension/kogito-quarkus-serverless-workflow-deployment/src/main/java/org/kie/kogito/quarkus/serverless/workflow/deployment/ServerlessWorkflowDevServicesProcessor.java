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
package org.kie.kogito.quarkus.serverless.workflow.deployment;

import java.util.List;
import java.util.Optional;

import org.kie.kogito.quarkus.workflow.deployment.AbstractDevServicesProcessor;
import org.kie.kogito.quarkus.workflow.deployment.config.KogitoWorkflowBuildTimeConfig;
import org.kie.kogito.serverless.workflow.devservices.DevModeServerlessWorkflowLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.builditem.SystemPropertyBuildItem;
import io.quarkus.deployment.dev.devservices.DevServicesConfig;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;

import static org.kie.kogito.quarkus.workflow.devservices.DataIndexEventPublisher.KOGITO_DATA_INDEX;

public class ServerlessWorkflowDevServicesProcessor extends AbstractDevServicesProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerlessWorkflowDevServicesProcessor.class);

    @BuildStep
    public void logger(BuildProducer<AdditionalBeanBuildItem> additionalBean, LaunchModeBuildItem launchMode, KogitoWorkflowBuildTimeConfig config) {
        if (shouldInclude(launchMode, config)) {
            additionalBean.produce(AdditionalBeanBuildItem.builder().addBeanClass(DevModeServerlessWorkflowLogger.class).setUnremovable().setDefaultScope(DotNames.APPLICATION_SCOPED).build());
        }
    }

    @BuildStep(onlyIf = { DevServicesConfig.Enabled.class, IsDevelopment.class })
    void createDataIndexDevUILink(BuildProducer<CardPageBuildItem> cardsProducer,
            Capabilities capabilities,
            KogitoWorkflowBuildTimeConfig kogitoBuildTimeConfig,
            List<SystemPropertyBuildItem> systemPropertyBuildItems) {

        if (!kogitoBuildTimeConfig.devServicesConfig().enabled()) {
            LOGGER.info("Kogito DevServices are disabled. Skipping Dev UI Card initialization");
            return;
        }

        Optional<String> dataIndexUrlProp = getProperty(systemPropertyBuildItems, KOGITO_DATA_INDEX);

        if (capabilities.isPresent(DATA_INDEX_CAPABILITY) || dataIndexUrlProp.isEmpty()) {
            return;
        }

        CardPageBuildItem cardPageBuildItem = new CardPageBuildItem();
        cardPageBuildItem.addPage(Page.externalPageBuilder("Data Index GraphQL UI")
                .url(dataIndexUrlProp.get() + "/q/graphql-ui/")
                .isHtmlContent()
                .icon("font-awesome-solid:signs-post"));

        cardsProducer.produce(cardPageBuildItem);
    }
}
