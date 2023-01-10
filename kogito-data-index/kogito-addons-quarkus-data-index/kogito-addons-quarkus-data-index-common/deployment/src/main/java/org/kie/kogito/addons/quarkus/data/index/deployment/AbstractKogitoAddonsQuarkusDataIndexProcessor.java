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

package org.kie.kogito.addons.quarkus.data.index.deployment;

import org.kie.kogito.index.vertx.VertxGraphiQLSetup;
import org.kie.kogito.quarkus.addons.common.deployment.KogitoCapability;
import org.kie.kogito.quarkus.addons.common.deployment.OneOfCapabilityKogitoAddOnProcessor;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;

public abstract class AbstractKogitoAddonsQuarkusDataIndexProcessor extends OneOfCapabilityKogitoAddOnProcessor {

    AbstractKogitoAddonsQuarkusDataIndexProcessor() {
        super(KogitoCapability.SERVERLESS_WORKFLOW, KogitoCapability.PROCESSES);
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    public void processGraphiql(BuildProducer<AdditionalBeanBuildItem> additionalBean) {
        additionalBean.produce(AdditionalBeanBuildItem.builder().addBeanClass(VertxGraphiQLSetup.class).setUnremovable().setDefaultScope(DotNames.APPLICATION_SCOPED).build());
    }

}
