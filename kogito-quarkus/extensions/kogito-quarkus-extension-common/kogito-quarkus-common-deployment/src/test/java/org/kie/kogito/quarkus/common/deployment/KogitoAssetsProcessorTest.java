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
package org.kie.kogito.quarkus.common.deployment;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import io.quarkus.bootstrap.model.PathsCollection;
import io.quarkus.paths.PathCollection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KogitoAssetsProcessorTest {

    @Test
    void getRootPathsWithoutClasses() {
        String projectDirPath = "projectDir";
        String outputTargetPath = "outputTarget";
        Path projectDir = Path.of(projectDirPath);
        Path outputTarget = Path.of(outputTargetPath);
        Iterable<Path> paths = Arrays.asList(projectDir, outputTarget);

        PathCollection resolvedPaths = PathsCollection.from(paths);
        PathCollection retrieved = KogitoAssetsProcessor.getRootPaths(resolvedPaths);
        assertEquals(resolvedPaths.size(), retrieved.size());
        paths.forEach(expected -> assertTrue(retrieved.contains(expected)));
    }

    @Test
    void getRootPathsWithClasses() {
        String projectDirPath = "projectDir";
        String outputTargetPath = "outputTarget";
        String outputTargetPathClasses = String.format("%s/%s/classes", projectDirPath, outputTargetPath).replace("/", File.separator);
        Path projectDir = Path.of(projectDirPath);
        Path outputTarget = Path.of(outputTargetPathClasses);
        Iterable<Path> paths = Arrays.asList(projectDir, outputTarget);

        PathCollection resolvedPaths = PathsCollection.from(paths);
        PathCollection retrieved = KogitoAssetsProcessor.getRootPaths(resolvedPaths);
        assertEquals(resolvedPaths.size() + 1, retrieved.size());
        paths.forEach(expected -> assertTrue(retrieved.contains(expected)));
        String expectedPath = String.format("%s/%s/generated-resources", projectDirPath, outputTargetPath).replace("/", File.separator);
        assertTrue(retrieved.contains(Path.of(expectedPath)));
    }
}
