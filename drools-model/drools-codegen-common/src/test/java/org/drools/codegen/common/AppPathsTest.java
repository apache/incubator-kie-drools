package org.drools.codegen.common;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.drools.codegen.common.AppPaths.GENERATED_RESOURCES_DIR;
import static org.drools.codegen.common.AppPaths.MAIN_DIR;
import static org.drools.codegen.common.AppPaths.RESOURCES_DIR;
import static org.drools.codegen.common.AppPaths.SRC_DIR;
import static org.drools.codegen.common.AppPaths.TARGET_DIR;
import static org.drools.codegen.common.AppPaths.TEST_DIR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
class AppPathsTest {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void fromProjectDir(boolean withGradle) {
        String projectDirPath = "projectDir";
        String outputTargetPath = "outputTarget";
        Path projectDir = Path.of(projectDirPath);
        Path outputTarget = Path.of(outputTargetPath);
        if (withGradle) {
            System.setProperty("org.gradle.appname", "gradle-impl");
        } else {
            System.clearProperty("org.gradle.appname");
        }
        AppPaths retrieved = AppPaths.fromProjectDir(projectDir, outputTarget);
        getPathsTest(retrieved, projectDirPath, withGradle, false);
        getFirstProjectPathTest(retrieved, projectDirPath, outputTargetPath, withGradle);
        getResourceFilesTest(retrieved, projectDirPath, withGradle, false);
        getResourcePathsTest(retrieved, projectDirPath, withGradle, false);
        getSourcePathsTest(retrieved, projectDirPath);
        getClassesPathsTest(retrieved);
        getOutputTargetTest(retrieved, outputTargetPath);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void fromTestDir(boolean withGradle) {
        String projectDirPath = "projectDir";
        String outputTargetPath = String.format("%s/%s", projectDirPath, TARGET_DIR).replace("/", File.separator);
        Path projectDir = Path.of(projectDirPath);
        if (withGradle) {
            System.setProperty("org.gradle.appname", "gradle-impl");
        } else {
            System.clearProperty("org.gradle.appname");
        }
        AppPaths retrieved = AppPaths.fromTestDir(projectDir);
        getPathsTest(retrieved, projectDirPath, withGradle, true);
        getFirstProjectPathTest(retrieved, projectDirPath, outputTargetPath, withGradle);
        getResourceFilesTest(retrieved, projectDirPath, withGradle, true);
        getResourcePathsTest(retrieved, projectDirPath, withGradle, true);
        getSourcePathsTest(retrieved, projectDirPath);
        getClassesPathsTest(retrieved);
        getOutputTargetTest(retrieved, outputTargetPath);
    }

    private void getPathsTest(AppPaths toCheck, String projectDirPath, boolean isGradle, boolean isTestDir) {
        Path[] retrieved = toCheck.getPaths();
        commonCheckResourcePaths(retrieved, projectDirPath, isGradle, isTestDir,"getPathsTest");
    }

    private void getFirstProjectPathTest(AppPaths toCheck, String projectPath, String outputPath, boolean isGradle) {
        Path retrieved = toCheck.getFirstProjectPath();
        String expectedPath;
        if (isGradle) {
            expectedPath = outputPath;
        } else {
            expectedPath = projectPath;
        }
        assertEquals(Path.of(expectedPath), retrieved, "AppPathsTest.getFirstProjectPathTest");
    }

    private void getResourceFilesTest(AppPaths toCheck, String projectDirPath, boolean isGradle, boolean isTestDir) {
        File[] retrieved = toCheck.getResourceFiles();
        int expected = isGradle ? 1 : 2;
        assertEquals(expected, retrieved.length, "AppPathsTest.getResourceFilesTest");
        String expectedPath;
        String sourceDir =  isTestDir ? TEST_DIR : MAIN_DIR;
        if (isGradle) {
            expectedPath = projectDirPath;
        } else {
            expectedPath = String.format("%s/%s/%s/%s", projectDirPath, SRC_DIR, sourceDir, RESOURCES_DIR).replace("/", File.separator);
        }
        assertEquals(new File(expectedPath), retrieved[0], "AppPathsTest.getResourceFilesTest");
        if (!isGradle) {
            expectedPath = String.format("%s/%s/%s", projectDirPath, TARGET_DIR, GENERATED_RESOURCES_DIR).replace("/", File.separator);
            assertEquals(new File(expectedPath), retrieved[1], "AppPathsTest.getResourceFilesTest");
        }
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
        assertEquals(Path.of(outputPath), retrieved, "AppPathsTest.getOutputTargetTest");
    }

    private void commonCheckResourcePaths(Path[] toCheck, String projectDirPath, boolean isGradle, boolean isTestDir, String methodName) {
        int expected = isGradle ? 1 : 2;
        assertEquals(expected, toCheck.length, String.format("AppPathsTest.%s", methodName));
        String expectedPath;
        String sourceDir =  isTestDir ? "test" : "main";
        if (isGradle) {
            expectedPath = projectDirPath;
        } else {
            expectedPath = String.format("%s/src/%s/resources", projectDirPath, sourceDir).replace("/", File.separator);
        }
        assertEquals(Path.of(expectedPath), toCheck[0], String.format("AppPathsTest.%s", methodName));
        if (!isGradle) {
            expectedPath = String.format("%s/target/generated-resources", projectDirPath).replace("/", File.separator);
            assertEquals(Path.of(expectedPath), toCheck[1], String.format("AppPathsTest.%s", methodName));
        }
    }
}