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

import java.io.File;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.addon.source.files.SourceFile;
import org.kie.kogito.codegen.api.SourceFileCodegenBindEvent;

import static org.assertj.core.api.Assertions.assertThat;

class SourceFileCodegenBindListenerImplTest {

    public static Stream<Arguments> testOnSourceFileProcessBindEventSources() {
        return Stream.of(
                Arguments.arguments("/dev/proj/other_resources/org/acme/process/a_process.bpmn", "org/acme/process/a_process.bpmn"),
                Arguments.arguments("file://restcountries.json", "file://restcountries.json"),
                Arguments.arguments("/a/random/directory/a_process.bpmn", "/a/random/directory/a_process.bpmn"));
    }

    @ParameterizedTest
    @MethodSource("testOnSourceFileProcessBindEventSources")
    void testOnSourceFileProcessBindEvent(String eventSourceFile, String expectedSourceFile) {
        File[] resourcePaths = new File[] { new File("/dev/proj/resources/"), new File("/dev/proj/other_resources/") };

        String processId = "a_process";

        SourceFileCodegenBindEvent event = new SourceFileCodegenBindEvent(processId, eventSourceFile);

        FakeSourceFilesRecorder sourceFilesRecorder = new FakeSourceFilesRecorder();

        new SourceFileProcessBindListenerImpl(resourcePaths, sourceFilesRecorder).onSourceFileCodegenBind(event);

        assertThat(sourceFilesRecorder.containsRecordFor(processId, new SourceFile(expectedSourceFile))).isTrue();
    }
}
