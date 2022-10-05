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
package org.kie.kogito.quarkus.addons.common.deployment;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Data Holder to use with {@link RequireCapabilityKogitoAddOnProcessor} to describe the Kogito Capability required by a given add-on.
 */
public final class KogitoCapability {

    public static final String KOGITO_GROUP_ID = "org.kie.kogito";
    public static final KogitoCapability DECISIONS = new KogitoCapability("org.kie.kogito.decisions", "kogito-quarkus-decisions");
    public static final KogitoCapability PROCESSES = new KogitoCapability("org.kie.kogito.processes", "kogito-quarkus-processes");
    public static final KogitoCapability PREDICTIONS = new KogitoCapability("org.kie.kogito.predictions", "kogito-quarkus-predictions");
    public static final KogitoCapability RULES = new KogitoCapability("org.kie.kogito.rules", "kogito-quarkus-rules");
    public static final KogitoCapability SERVERLESS_WORKFLOW = new KogitoCapability("org.kie.kogito.serverless-workflow", "kogito-quarkus-serverless-workflow");

    public static final List<KogitoCapability> ENGINES = asList(
            KogitoCapability.DECISIONS,
            KogitoCapability.PROCESSES,
            KogitoCapability.PREDICTIONS,
            KogitoCapability.RULES,
            KogitoCapability.SERVERLESS_WORKFLOW);

    private final String capability;
    private final String offeredBy;

    public KogitoCapability(final String capability, final String offeredBy) {
        this.capability = capability;
        this.offeredBy = offeredBy;
    }

    public String getCapability() {
        return capability;
    }

    public String getOfferedBy() {
        return offeredBy;
    }
}
