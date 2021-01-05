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

import java.nio.file.Path;
import java.util.Optional;

import org.jbpm.compiler.canonical.ProcessMetaData;
import org.jbpm.compiler.canonical.ProcessToExecModelGenerator;
import org.kie.api.definition.process.WorkflowProcess;

public class ProcessExecutableModelGenerator {

    private final WorkflowProcess workFlowProcess;
    private final ProcessToExecModelGenerator execModelGenerator;
    private String processFilePath;
    private ProcessMetaData processMetaData;

    public ProcessExecutableModelGenerator(WorkflowProcess workFlowProcess, ProcessToExecModelGenerator execModelGenerator) {
        this.workFlowProcess = workFlowProcess;
        this.execModelGenerator = execModelGenerator;
    }

    public boolean isPublic() {
        return WorkflowProcess.PUBLIC_VISIBILITY.equalsIgnoreCase(workFlowProcess.getVisibility());
    }

    public ProcessMetaData generate() {
        if (processMetaData != null) return processMetaData;
        processMetaData = execModelGenerator.generate(workFlowProcess);

        // this is ugly, but this class will be refactored
        String processClazzName = processMetaData.getProcessClassName();
        processFilePath = processClazzName.replace('.', '/') + ".java";
        return processMetaData;
    }

    public String description() {
        return Optional.ofNullable(workFlowProcess.getMetaData().get("Description"))
                .map(Object::toString).orElse("Executes " + workFlowProcess.getName());
    }

    public String className() {
        if (processMetaData == null) generate();
        return processMetaData.getProcessClassName();
    }

    public String generatedFilePath() {
        return processFilePath;
    }

    private String getCompiledClassName(Path fileNameRelative) {
        return fileNameRelative.toString()
                .replace("/", ".")
                .replace(".java", "");
    }

    public String extractedProcessId() {
        return execModelGenerator.extractProcessId(workFlowProcess.getId());
    }

    public String getProcessId() {
        return workFlowProcess.getId();
    }

    public WorkflowProcess process() {
        return workFlowProcess;
    }
}
