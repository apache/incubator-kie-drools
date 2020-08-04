/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.process;

import org.drools.core.util.StringUtils;
import org.jbpm.compiler.canonical.ModelMetaData;
import org.jbpm.compiler.canonical.ProcessToExecModelGenerator;
import org.jbpm.compiler.canonical.VariableDeclarations;
import org.jbpm.process.core.context.variable.VariableScope;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.GeneratorContext;

public class InputModelClassGenerator {
    
    private final GeneratorContext context;
    private final WorkflowProcess workFlowProcess;
    private String className;
    private String modelFileName;
    private ModelMetaData modelMetaData;
    private String modelClassName;

    public InputModelClassGenerator(GeneratorContext context, WorkflowProcess workFlowProcess) {
        String pid = workFlowProcess.getId();
        className = StringUtils.ucFirst(ProcessToExecModelGenerator.extractProcessId(pid) + "ModelInput");
        this.modelClassName = workFlowProcess.getPackageName() + "." + className;

        this.context = context;
        this.workFlowProcess = workFlowProcess;
    }

    public ModelMetaData generate() {
        // create model class for all variables
        String packageName = workFlowProcess.getPackageName();        

        modelMetaData = new ModelMetaData(workFlowProcess.getId(), packageName, className, workFlowProcess.getVisibility(),
                                 VariableDeclarations.ofInput((VariableScope) ((org.jbpm.process.core.Process) workFlowProcess).getDefaultContext(VariableScope.VARIABLE_SCOPE)),
                                 true, "/class-templates/ModelNoIDTemplate.java");
        modelMetaData.setSupportsValidation(context.getBuildContext().isValidationSupported());
                
        modelFileName = modelMetaData.getModelClassName().replace('.', '/') + ".java";
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
