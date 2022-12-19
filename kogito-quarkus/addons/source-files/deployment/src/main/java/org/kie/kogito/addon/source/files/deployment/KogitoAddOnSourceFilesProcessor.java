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
package org.kie.kogito.addon.source.files.deployment;

import org.kie.kogito.addon.source.files.SourceFilesProviderProducer;
import org.kie.kogito.addon.source.files.SourceFilesRecorder;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.quarkus.addons.common.deployment.KogitoCapability;
import org.kie.kogito.quarkus.addons.common.deployment.OneOfCapabilityKogitoAddOnProcessor;
import org.kie.kogito.quarkus.common.deployment.KogitoBuildContextBuildItem;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class KogitoAddOnSourceFilesProcessor extends OneOfCapabilityKogitoAddOnProcessor {

    private static final String FEATURE = "kogito-addon-source-files-extension";

    KogitoAddOnSourceFilesProcessor() {
        super(KogitoCapability.PROCESSES, KogitoCapability.SERVERLESS_WORKFLOW);
    }

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem sourceFilesProviderProducer() {
        return new AdditionalBeanBuildItem(SourceFilesProviderProducer.class);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    void addSourceFileProcessBindListener(KogitoBuildContextBuildItem ctxBuildItem,
            SourceFilesRecorder sourceFilesRecorder) {
        KogitoBuildContext kogitoBuildContext = ctxBuildItem.getKogitoBuildContext();

        SourceFileProcessBindListenerImpl processListener = new SourceFileProcessBindListenerImpl(
                kogitoBuildContext.getAppPaths().getResourceFiles(),
                sourceFilesRecorder);

        SourceFileServerlessWorkflowBindListenerImpl serverlessWorkflowListener = new SourceFileServerlessWorkflowBindListenerImpl(
                kogitoBuildContext.getAppPaths().getResourceFiles(),
                sourceFilesRecorder);

        kogitoBuildContext.getSourceFileCodegenBindNotifier()
                .ifPresent(notifier -> notifier.addListeners(processListener, serverlessWorkflowListener));
    }
}
