/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import org.assertj.core.api.Assertions;
import org.jbpm.compiler.canonical.ProcessToExecModelGenerator;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utilities for unit Process generation tests
 */
public class ProcessGenerationUtils {

    /**
     * Creates a list of {@link ProcessExecutableModelGenerator} for process generators
     *
     * @param processFilePath from the test/resources classpath folder
     * @return a list of {@link ProcessExecutableModelGenerator} from the given file
     */
    public static List<ProcessExecutableModelGenerator> execModelFromProcessFile(final String processFilePath) {
        final File processFile = new File(ProcessGenerationUtils.class.getResource(processFilePath).getFile());
        final List<Process> processes = ProcessCodegen.parseProcesses(Collections.singleton(processFile));
        Assertions.assertThat(processes).isNotEmpty();

        final ProcessToExecModelGenerator execModelGenerator = new ProcessToExecModelGenerator(ProcessGenerationUtils.class.getClassLoader());
        final List<ProcessExecutableModelGenerator> processExecutableModelGenerators = new ArrayList<>();
        processes.forEach(p -> {
            processExecutableModelGenerators.add(new ProcessExecutableModelGenerator((WorkflowProcess) p, execModelGenerator));
        });
        return processExecutableModelGenerators;
    }

}
