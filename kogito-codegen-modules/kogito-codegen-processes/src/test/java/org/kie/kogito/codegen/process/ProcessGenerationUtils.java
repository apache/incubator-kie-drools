/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.codegen.process;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.io.FileSystemResource;
import org.jbpm.compiler.canonical.ProcessToExecModelGenerator;
import org.kie.api.definition.process.Process;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.internal.SupportedExtensions;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;

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
        final List<Process> processes = parseProcesses(Collections.singleton(processFile));
        Assertions.assertThat(processes).isNotEmpty();

        final ProcessToExecModelGenerator execModelGenerator = new ProcessToExecModelGenerator(ProcessGenerationUtils.class.getClassLoader());
        final List<ProcessExecutableModelGenerator> processExecutableModelGenerators = new ArrayList<>();
        processes.forEach(p -> {
            processExecutableModelGenerators.add(new ProcessExecutableModelGenerator((KogitoWorkflowProcess) p, execModelGenerator));
        });
        return processExecutableModelGenerators;
    }

    public static List<Process> parseProcesses(Collection<File> processFiles) {
        List<Process> processes = new ArrayList<>();
        for (File processSourceFile : processFiles) {
            try {
                FileSystemResource r = new FileSystemResource(processSourceFile);
                if (SupportedExtensions.getBPMNExtensions().stream().anyMatch(processSourceFile.getPath()::endsWith)) {
                    ProcessCodegen.parseProcessFile(r)
                            .forEach(process -> {
                                process.setResource(r);
                                processes.add(process);
                            });
                } else if (SupportedExtensions.getSWFExtensions().stream().anyMatch(processSourceFile.getPath()::endsWith)) {
                    KogitoWorkflowProcess swfWorkflow = ProcessCodegen.parseWorkflowFile(r, JavaKogitoBuildContext.builder().build()).info();
                    swfWorkflow.setResource(r);
                    processes.add(swfWorkflow);
                }
                if (processes.isEmpty()) {
                    throw new IllegalArgumentException("Unable to process file with unsupported extension: " + processSourceFile);
                }
            } catch (RuntimeException e) {
                throw new ProcessCodegenException(processSourceFile.getAbsolutePath(), e);
            }
        }
        return processes;
    }

}
