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

package org.kie.kogito.serverless.workflow;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.kie.api.definition.process.Process;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;
import org.kie.kogito.serverless.workflow.utils.WorkflowFormat;

public abstract class AbstractServerlessWorkflowParsingTest {

    @BeforeAll
    public static void init() {
        System.setProperty("jbpm.enable.multi.con", "true");
    }

    @AfterAll
    public static void cleanup() {
        System.clearProperty("jbpm.enable.multi.con");
    }

    protected Process getWorkflowParser(String workflowLocation) throws IOException {
        WorkflowFormat format = workflowLocation.endsWith(".sw.json") ? WorkflowFormat.JSON : WorkflowFormat.YAML;
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream(workflowLocation),
                "Required resource was no found: " + workflowLocation))) {
            ServerlessWorkflowParser parser = ServerlessWorkflowParser.of(
                    reader,
                    format,
                    JavaKogitoBuildContext.builder().build()).withBaseURI("classpath:" + workflowLocation);
            return parser.getProcessInfo().info();
        }
    }
}
