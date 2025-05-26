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
package org.kie.kogito.svg;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kie.kogito.test.utils.CustomSVGDiffer;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Fail.fail;

public abstract class ProcessSvgServiceTest {

    private final static String PROCESS_ID = "travels";

    public static String readFileContent(String file) throws URISyntaxException, IOException {
        Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource(file).toURI());
        return new String(Files.readAllBytes(path));
    }

    @Test
    public void getProcessSvgWithoutSvgResourcePathTest() throws Exception {
        String fileContent = getTravelsSVGFile();
        Optional<String> svgContent = getTestedProcessSvgService().getProcessSvg(PROCESS_ID);
        assertThat(svgContent).isPresent().hasValue(fileContent);
    }

    @Test
    public void getProcessSvgFromFileSystemSuccessTest() throws Exception {
        String fileContent = getTravelsSVGFile();

        Path svgPath = Paths.get("src/test/resources/META-INF/processSVG/").toAbsolutePath();

        AbstractProcessSvgService svgService = getTestedProcessSvgService();
        svgService.setSvgResourcesPath(Optional.of(svgPath.toString()));

        Optional<String> svgContent = svgService.getProcessSvg(PROCESS_ID);
        assertThat(svgContent).isPresent().hasValue(fileContent);
    }

    @Test
    public void getProcessSvgFromFileSystemFailTest() {
        getTestedProcessSvgService().setSvgResourcesPath(Optional.of("./src/test/resources/META-INF/processSVG/"));

        assertThatExceptionOfType(SecurityException.class)
                .isThrownBy(() -> getTestedProcessSvgService().getProcessSvg("UnexistingProcessId"))
                .withMessageContaining("Path traversal attempt detected");
    }

    @Test
    public void annotateExecutedPathTest() throws Exception {
        final String content = getTestedProcessSvgService().annotateExecutedPath(
                getTravelsSVGFile(),
                Arrays.asList("_1A708F87-11C0-42A0-A464-0B7E259C426F"),
                Collections.emptyList()).get();

        Diff myDiff = new CustomSVGDiffer(content).withTest(Input.fromString(readFileContent("travels-expected.svg")));

        assertThat(myDiff.hasDifferences()).isFalse();

        assertThat(getTestedProcessSvgService().annotateExecutedPath(
                null,
                Arrays.asList("_1A708F87-11C0-42A0-A464-0B7E259C426F"),
                Collections.emptyList())).isEmpty();
        assertThat(getTestedProcessSvgService().annotateExecutedPath(
                getTravelsSVGFile(),
                Collections.emptyList(),
                Collections.emptyList())).hasValue(getTravelsSVGFile());
    }

    @Test
    public void readFileFromClassPathTest() throws Exception {
        assertThat(getTestedProcessSvgService().readFileContentFromClassPath("undefined")).isEmpty();
        assertThat(getTravelsSVGFile()).isEqualTo(getTestedProcessSvgService().readFileContentFromClassPath("travels.svg").get());
    }

    @Test
    public void testWrongSVGContentThrowsException() {
        AbstractProcessSvgService testedProcessSvgService = getTestedProcessSvgService();
        List completedNodes = Arrays.asList("_1A708F87-11C0-42A0-A464-0B7E259C426F");
        List activeNodes = Collections.emptyList();
        try {
            testedProcessSvgService.annotateExecutedPath("wrongSVGContent", completedNodes, activeNodes);
            fail("Expected an ProcessSVGException to be thrown");
        } catch (ProcessSVGException e) {
            assertThat(e.getMessage()).isEqualTo("Failed to annotated SVG for process instance");
        }
    }

    public String getTravelsSVGFile() throws Exception {
        return readFileContent("META-INF/processSVG/travels.svg");
    }

    protected abstract AbstractProcessSvgService getTestedProcessSvgService();
}
