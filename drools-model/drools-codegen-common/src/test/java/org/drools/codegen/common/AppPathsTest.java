/**
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

package org.drools.codegen.common;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.drools.codegen.common.AppPaths.MAIN_DIR;
import static org.drools.codegen.common.AppPaths.RESOURCES_DIR;
import static org.drools.codegen.common.AppPaths.SRC_DIR;
import static org.drools.codegen.common.AppPaths.TEST_DIR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
class AppPathsTest {

    @ParameterizedTest
    @EnumSource(value = AppPaths.BuildTool.class, names = {"GRADLE", "MAVEN"})
    void fromProjectDir(AppPaths.BuildTool bt) {
        String projectDirPath = "projectDir";
        String outputTargetPath;
        String generatedResourceDir;
        Path projectDir = Path.of(projectDirPath);
        boolean withGradle;
        generatedResourceDir = switch (bt) {
            case GRADLE -> {
                withGradle = true;
                yield "generated/resources";
            }
            default -> {
                withGradle = false;
                yield "generated-resources";
            }
        };
        outputTargetPath = bt.OUTPUT_DIRECTORY;
        AppPaths retrieved = AppPaths.fromProjectDir(projectDir, bt);
        getPathsTest(retrieved, projectDirPath, withGradle, false);
        getFirstProjectPathTest(retrieved, projectDirPath);
        getResourceFilesTest(retrieved, projectDirPath, outputTargetPath, generatedResourceDir, false);
        getResourcePathsTest(retrieved, projectDirPath, withGradle, false);
        getSourcePathsTest(retrieved, projectDirPath);
        getClassesPathsTest(retrieved);
        getOutputTargetTest(retrieved, outputTargetPath);
    }

    @ParameterizedTest
    @EnumSource(value = AppPaths.BuildTool.class, names = {"GRADLE", "MAVEN"})
    void fromTestDir(AppPaths.BuildTool bt) {
        String projectDirPath = "projectDir";
        String outputTargetPath;
        String generatedResourceDir;
        Path projectDir = Path.of(projectDirPath);
        boolean withGradle;
        generatedResourceDir = switch (bt) {
            case GRADLE -> {
                withGradle = true;
                yield "generated/test/resources";
            }
            default -> {
                withGradle = false;
                yield "generated-test-resources";
            }
        };
        outputTargetPath = bt.OUTPUT_DIRECTORY;
        AppPaths retrieved = AppPaths.fromTestDir(projectDir, bt);
        getPathsTest(retrieved, projectDirPath, withGradle, true);
        getFirstProjectPathTest(retrieved, projectDirPath);
        getResourceFilesTest(retrieved, projectDirPath, outputTargetPath, generatedResourceDir, true);
        getResourcePathsTest(retrieved, projectDirPath, withGradle, true);
        getSourcePathsTest(retrieved, projectDirPath);
        getClassesPathsTest(retrieved);
        getOutputTargetTest(retrieved, outputTargetPath);
    }

    private void getPathsTest(AppPaths toCheck, String projectDirPath, boolean isGradle, boolean isTestDir) {
        Path[] retrieved = toCheck.getPaths();
        commonCheckResourcePaths(retrieved, projectDirPath, isGradle, isTestDir,"getPathsTest");
    }

    private void getFirstProjectPathTest(AppPaths toCheck, String expectedPath) {
        Path retrieved = toCheck.getFirstProjectPath();
        assertEquals(Path.of(expectedPath), retrieved, "AppPathsTest.getFirstProjectPathTest");
    }

    private void getResourceFilesTest(AppPaths toCheck, String projectDirPath, String outputDir, String generatedResourceDir, boolean isTestDir) {
        File[] retrieved = toCheck.getResourceFiles();
        int expected = 2;
        assertEquals(expected, retrieved.length, "AppPathsTest.getResourceFilesTest");
        String expectedPath;
        String sourceDir =  isTestDir ? TEST_DIR : MAIN_DIR;
        expectedPath = String.format("%s/%s/%s/%s", projectDirPath, SRC_DIR, sourceDir, RESOURCES_DIR).replace("/", File.separator);
        assertEquals(new File(expectedPath), retrieved[0], "AppPathsTest.getResourceFilesTest");
        expectedPath = String.format("%s/%s/%s", projectDirPath, outputDir, generatedResourceDir).replace("/", File.separator);
        assertEquals(new File(expectedPath), retrieved[1], "AppPathsTest.getResourceFilesTest");
    }

    private void getResourcePathsTest(AppPaths toCheck, String projectDirPath, boolean isGradle, boolean isTestDir) {
        Path[] retrieved = toCheck.getResourcePaths();
        commonCheckResourcePaths(retrieved, projectDirPath, isGradle, isTestDir, "getResourcePathsTest");
    }

    private void getSourcePathsTest(AppPaths toCheck, String projectPath) {
        Path[] retrieved = toCheck.getSourcePaths();
        assertEquals(1, retrieved.length, "AppPathsTest.getSourcePathsTest");
        String expectedPath = String.format("%s/src", projectPath).replace("/", File.separator);
        assertEquals(Path.of(expectedPath), retrieved[0], "AppPathsTest.getSourcePathsTest");
    }

    private void getClassesPathsTest(AppPaths toCheck) {
        Collection<Path> retrieved = toCheck.getClassesPaths();
        assertTrue(retrieved.isEmpty(), "AppPathsTest.getClassesPathsTest");
    }

    private void getOutputTargetTest(AppPaths toCheck, String outputPath) {
        Path retrieved = toCheck.getOutputTarget();
        outputPath = String.format(".%s%s", File.separator, outputPath);
        assertEquals(Path.of(outputPath), retrieved, "AppPathsTest.getOutputTargetTest");
    }

    private void commonCheckResourcePaths(Path[] toCheck, String projectDirPath, boolean isGradle, boolean isTestDir, String methodName) {
        int expected = 2;
        assertEquals(expected, toCheck.length, String.format("AppPathsTest.%s", methodName));
        String expectedPath;
        String sourceDir =  isTestDir ? "test" : "main";
        expectedPath = String.format("%s/src/%s/resources", projectDirPath, sourceDir).replace("/", File.separator);
        assertEquals(Path.of(expectedPath), toCheck[0], String.format("AppPathsTest.%s", methodName));
        if (isGradle) {
            String toFormat = isTestDir ? "%s/build/generated/test/resources" : "%s/build/generated/resources";
            expectedPath = String.format(toFormat, projectDirPath).replace("/", File.separator);
            assertEquals(Path.of(expectedPath), toCheck[1], String.format("AppPathsTest.%s", methodName));
        } else {
            String toFormat = isTestDir ? "%s/target/generated-test-resources" : "%s/target/generated-resources";
            expectedPath = String.format(toFormat, projectDirPath).replace("/", File.separator);
            assertEquals(Path.of(expectedPath), toCheck[1], String.format("AppPathsTest.%s", methodName));
        }
    }
}