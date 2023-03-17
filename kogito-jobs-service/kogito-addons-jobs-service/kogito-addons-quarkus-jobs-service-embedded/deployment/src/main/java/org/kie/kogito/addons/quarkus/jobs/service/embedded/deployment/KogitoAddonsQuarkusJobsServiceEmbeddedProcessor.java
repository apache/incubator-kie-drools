/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addons.quarkus.jobs.service.embedded.deployment;

import org.kie.kogito.quarkus.addons.common.deployment.KogitoCapability;
import org.kie.kogito.quarkus.addons.common.deployment.OneOfCapabilityKogitoAddOnProcessor;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.SystemPropertyBuildItem;

class KogitoAddonsQuarkusJobsServiceEmbeddedProcessor extends OneOfCapabilityKogitoAddOnProcessor {

    private static final String FEATURE = "kogito-addons-quarkus-jobs-service-embedded";
    private static final String JOBS_SERVICE_URL = "kogito.jobs-service.url";
    private static final String SERVICE_URL = "kogito.service.url";

    KogitoAddonsQuarkusJobsServiceEmbeddedProcessor() {
        super(KogitoCapability.SERVERLESS_WORKFLOW, KogitoCapability.PROCESSES);
    }

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void buildConfiguration(BuildProducer<SystemPropertyBuildItem> systemProperties) {
        systemProperties.produce(new SystemPropertyBuildItem(SERVICE_URL, "http://${quarkus.http.host}:${quarkus.http.port}"));
        systemProperties.produce(new SystemPropertyBuildItem(JOBS_SERVICE_URL, "${" + SERVICE_URL + "}"));
    }
}
