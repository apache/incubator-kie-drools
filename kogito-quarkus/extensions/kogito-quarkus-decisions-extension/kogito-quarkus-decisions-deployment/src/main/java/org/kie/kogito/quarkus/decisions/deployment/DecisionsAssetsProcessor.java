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
import java.util.Optional;

import org.jboss.jandex.DotName;
import org.kie.dmn.core.runtime.DMNRuntimeService;
import org.kie.kogito.codegen.decision.DecisionContainerGenerator;
import org.kie.kogito.quarkus.common.deployment.KogitoBuildContextBuildItem;
import org.kie.kogito.quarkus.common.deployment.KogitoGeneratedSourcesBuildItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveHierarchyIgnoreWarningBuildItem;

/**
 * Main class of the Kogito decisions extension
 */
public class DecisionsAssetsProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DecisionsAssetsProcessor.class);

    @BuildStep
    FeatureBuildItem featureBuildItem() {
        return new FeatureBuildItem("kogito-decisions");
    }

    @BuildStep
    public List<ReflectiveHierarchyIgnoreWarningBuildItem> reflectiveDMNREST() {
        List<ReflectiveHierarchyIgnoreWarningBuildItem> result = new ArrayList<>();
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.api.builder" +
                ".Message$Level")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.core" +
                ".DMNContext")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.core" +
                ".DMNDecisionResult")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(
                DotName.createSimple("org.kie.dmn.api.core.DMNDecisionResult$DecisionEvaluationStatus")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.core" +
                ".DMNMessage")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.core" +
                ".DMNMessage$Severity")));
        result.add(new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.core" +
                ".DMNMessageType")));
        result.add(
                new ReflectiveHierarchyIgnoreWarningBuildItem(DotName.createSimple("org.kie.dmn.api.feel.runtime" +
                        ".events.FEELEvent")));
        return result;
    }

    /**
     * Constrained:
     * 1. conflicted with having a separate BuildStep with signature: public List<ReflectiveClassBuildItem>
     * reflectiveClassBuildItems() {
     * so it includes the code from that original method.
     * 2. need to be triggered by Quarkus AFTER the Kogito Codegen, hence this BuildStep "depends" on
     * KogitoGeneratedSourcesBuildItem.
     */
    @BuildStep
    public void stronglyTypeAdditionalClassesForReflection(KogitoGeneratedSourcesBuildItem generatedKogitoClasses, //
            // Constrain 1
            BuildProducer<ReflectiveClassBuildItem> additionalClassesForReflection,
            KogitoBuildContextBuildItem kogitoBuildContextBuildItem,
            Capabilities capabilities) {
        Optional<DecisionContainerGenerator> decisionContainerOpt =
                kogitoBuildContextBuildItem.getKogitoBuildContext().getApplicationSections().stream()
                        .filter(DecisionContainerGenerator.class::isInstance).map(DecisionContainerGenerator.class::cast).findFirst();
        if (decisionContainerOpt.isPresent()) {
            DecisionContainerGenerator decisionContainerGenerator = decisionContainerOpt.get();
            for (String fqcn : decisionContainerGenerator.getClassesForManualReflection()) {
                additionalClassesForReflection.produce(new ReflectiveClassBuildItem(true, true, fqcn));
            }
        }
        // Constrain 2:
        additionalClassesForReflection.produce(new ReflectiveClassBuildItem(true, true, "org.kie.kogito.dmn.rest" +
                ".KogitoDMNDecisionResult"));
        additionalClassesForReflection.produce(new ReflectiveClassBuildItem(true, true, "org.kie.kogito.dmn.rest" +
                ".KogitoDMNMessage"));
        additionalClassesForReflection.produce(new ReflectiveClassBuildItem(true, true, "org.kie.kogito.dmn.rest" +
                ".KogitoDMNResult"));
    }

    @BuildStep
    public ReflectiveClassBuildItem dmnRuntimeServiceReflectiveClass() {
        logger.debug("dmnRuntimeServiceReflectiveClass()");
        return new ReflectiveClassBuildItem(true, true, DMNRuntimeService.class);
    }
}
