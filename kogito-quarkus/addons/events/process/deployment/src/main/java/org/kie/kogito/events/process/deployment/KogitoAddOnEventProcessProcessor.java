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
package org.kie.kogito.events.process.deployment;

import org.kie.kogito.addon.quarkus.common.reactive.messaging.http.CloudEventHttpOutgoingDecorator;
import org.kie.kogito.quarkus.addons.common.deployment.KogitoCapability;
import org.kie.kogito.quarkus.addons.common.deployment.OneOfCapabilityKogitoAddOnProcessor;
import org.kie.kogito.quarkus.common.deployment.KogitoBuildContextBuildItem;
import org.kie.kogito.quarkus.config.KogitoBuildTimeConfig;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class KogitoAddOnEventProcessProcessor extends OneOfCapabilityKogitoAddOnProcessor {

    private static final String FEATURE = "kie-addon-events-process-extension";

    KogitoAddOnEventProcessProcessor() {
        super(KogitoCapability.PROCESSES, KogitoCapability.SERVERLESS_WORKFLOW);
    }

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void httpMessageDecorator(BuildProducer<AdditionalBeanBuildItem> beanBuildItem, KogitoBuildTimeConfig buildTimeConfig, KogitoBuildContextBuildItem kogitoContext) {
        if (buildTimeConfig.useCloudEvents() && kogitoContext.getKogitoBuildContext().hasClassAvailable("io.quarkus.reactivemessaging.http.runtime.OutgoingHttpMetadata")) {
            beanBuildItem.produce(AdditionalBeanBuildItem.builder().addBeanClass(CloudEventHttpOutgoingDecorator.class).setDefaultScope(DotNames.APPLICATION_SCOPED).build());
        }
    }

}
