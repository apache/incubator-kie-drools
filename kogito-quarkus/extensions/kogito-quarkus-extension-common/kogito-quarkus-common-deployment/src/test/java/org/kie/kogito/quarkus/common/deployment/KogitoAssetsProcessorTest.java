package org.kie.kogito.quarkus.common.deployment;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

import io.quarkus.bootstrap.model.PathsCollection;
import io.quarkus.paths.PathCollection;
import org.junit.jupiter.api.Test;

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