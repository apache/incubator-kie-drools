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
package org.kie.kogito.codegen.prediction;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.pmml.commons.model.KiePMMLModel;

import com.github.javaparser.ast.CompilationUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.pmml.CommonTestUtility.getKiePMMLModelInternal;

class PredictionContainerGeneratorTest {

    private final static String APP_CANONICAL_NAME = "APP_CANONICAL_NAME";
    private final static List<PMMLResource> PMML_RESOURCES = getPMMLResources();
    private static PredictionModelsGenerator predictionContainerGenerator;

    @BeforeAll
    public static void setup() {
        predictionContainerGenerator = new PredictionModelsGenerator(
                JavaKogitoBuildContext.builder().build(),
                APP_CANONICAL_NAME,
                PMML_RESOURCES);
        assertNotNull(predictionContainerGenerator);
    }

    @Test
    void constructor() {
        assertEquals(APP_CANONICAL_NAME, predictionContainerGenerator.applicationCanonicalName);
        assertEquals(PMML_RESOURCES, predictionContainerGenerator.resources);
    }

    @Test
    void classDeclaration() {
        CompilationUnit retrieved = predictionContainerGenerator.compilationUnit();
        assertNotNull(retrieved);
        String retrievedString = retrieved.toString();
        String expected = PMML_RESOURCES
                .stream()
                .map(pmmlResource -> "\"" + pmmlResource.getModelPath() + "\"")
                .collect(Collectors.joining(", "));
        assertTrue(retrievedString.contains(expected));

    }

    private static List<PMMLResource> getPMMLResources() {
        return IntStream.range(0, 3)
                .mapToObj(i -> getPMMLResource("Resource-" + i)).collect(Collectors.toList());
    }

    private static PMMLResource getPMMLResource(String resourceName) {
        Path path = getPath();
        String modelPath = "path/to/" + resourceName;
        List<KiePMMLModel> kiePmmlModels =
                IntStream.range(0, 3).mapToObj(i -> getKiePMMLModelInternal(resourceName, resourceName + "_Model-" + i)).collect(Collectors.toList());
        return new PMMLResource(kiePmmlModels, path, modelPath, Collections.emptyMap(), Collections.emptyMap());
    }

    private static Path getPath() {
        return new Path() {
            @Override
            public FileSystem getFileSystem() {
                return null;
            }

            @Override
            public boolean isAbsolute() {
                return false;
            }

            @Override
            public Path getRoot() {
                return null;
            }

            @Override
            public Path getFileName() {
                return null;
            }

            @Override
            public Path getParent() {
                return null;
            }

            @Override
            public int getNameCount() {
                return 0;
            }

            @Override
            public Path getName(int index) {
                return null;
            }

            @Override
            public Path subpath(int beginIndex, int endIndex) {
                return null;
            }

            @Override
            public boolean startsWith(Path other) {
                return false;
            }

            @Override
            public boolean endsWith(Path other) {
                return false;
            }

            @Override
            public Path normalize() {
                return null;
            }

            @Override
            public Path resolve(Path other) {
                return null;
            }

            @Override
            public Path relativize(Path other) {
                return null;
            }

            @Override
            public URI toUri() {
                return null;
            }

            @Override
            public Path toAbsolutePath() {
                return null;
            }

            @Override
            public Path toRealPath(LinkOption... options) throws IOException {
                return null;
            }

            @Override
            public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events,
                    WatchEvent.Modifier... modifiers) throws IOException {
                return null;
            }

            @Override
            public int compareTo(Path other) {
                return 0;
            }

            @Override
            public Iterator<Path> iterator() {
                return null;
            }

            @Override
            public boolean startsWith(String other) {
                return false;
            }

            @Override
            public boolean endsWith(String other) {
                return false;
            }

            @Override
            public Path resolve(String other) {
                return null;
            }

            @Override
            public Path resolveSibling(Path other) {
                return null;
            }

            @Override
            public Path resolveSibling(String other) {
                return null;
            }

            @Override
            public File toFile() {
                return null;
            }

            @Override
            public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws IOException {
                return null;
            }
        };
    }
}
