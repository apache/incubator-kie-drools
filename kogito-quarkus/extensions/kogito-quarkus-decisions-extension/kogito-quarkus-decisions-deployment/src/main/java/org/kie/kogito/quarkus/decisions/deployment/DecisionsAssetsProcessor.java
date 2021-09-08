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
package org.kie.kogito.quarkus.decisions.deployment;

import java.util.ArrayList;
import java.util.List;

import org.jboss.jandex.DotName;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveHierarchyIgnoreWarningBuildItem;

/**
 * Main class of the Kogito decisions extension
 */
public class DecisionsAssetsProcessor {

    @BuildStep
    FeatureBuildItem featureBuildItem() {
        return new FeatureBuildItem("kogito-decisions");
    }

    @BuildStep
    public List<ReflectiveHierarchyIgnoreWarningBuildItem> reflectiveDMNREST() {
        List<ReflectiveHierarchyIgnoreWarningBuildItem> result = new ArrayList<>();
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.api.builder.Message$Level")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.core.DMNContext")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.core.DMNDecisionResult")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(
                DotName.createSimple("org.kie.dmn.api.core.DMNDecisionResult$DecisionEvaluationStatus")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.core.DMNMessage")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.core.DMNMessage$Severity")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.core.DMNMessageType")));
        result.add(
                new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.feel.runtime.events.FEELEvent")));
        return result;
    }

    @BuildStep
    public List<ReflectiveClassBuildItem> reflectiveClassBuildItems() {
        List<ReflectiveClassBuildItem> result = new ArrayList<>();
        result.add(new ReflectiveClassBuildItem(true, true, "org.kie.kogito.dmn.rest.KogitoDMNDecisionResult"));
        result.add(new ReflectiveClassBuildItem(true, true, "org.kie.kogito.dmn.rest.KogitoDMNMessage"));
        result.add(new ReflectiveClassBuildItem(true, true, "org.kie.kogito.dmn.rest.KogitoDMNResult"));
        return result;
    }
}
