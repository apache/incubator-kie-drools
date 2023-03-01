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
package org.kie.kogito.codegen.process;

import org.jbpm.compiler.canonical.ModelMetaData;
import org.jbpm.compiler.canonical.ProcessToExecModelGenerator;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;

public class ModelClassGenerator {

    private final String modelFileName;
    private final ModelMetaData modelMetaData;
    private final String modelClassName;

    public ModelClassGenerator(KogitoBuildContext context, WorkflowProcess workFlowProcess) {
        modelMetaData = workFlowProcess.getType().equals(KogitoWorkflowProcess.SW_TYPE) ? ServerlessWorkflowUtils.getModelMetadata(workFlowProcess)
                : ProcessToExecModelGenerator.INSTANCE.generateModel(workFlowProcess);
        modelClassName = modelMetaData.getModelClassName();
        modelFileName = modelMetaData.getModelClassName().replace('.', '/') + ".java";
        modelMetaData.setSupportsValidation(context.isValidationSupported());
        modelMetaData.setSupportsOpenApiGeneration(context.isOpenApiSpecSupported());
    }

    public ModelMetaData generate() {
        return modelMetaData;
    }

    public String generatedFilePath() {
        return modelFileName;
    }

    public String simpleName() {
        return modelMetaData.getModelClassSimpleName();
    }

    public String className() {
        return modelClassName;
    }
}
