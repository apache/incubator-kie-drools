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
package org.drools.quarkus.util.deployment;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.drools.codegen.common.AppPaths;

import io.quarkus.deployment.pkg.steps.JarResultBuildStep;

/**
 * {@link AppPaths}'s extension in Quarkus context.
 */
public class QuarkusAppPaths extends AppPaths {

    private static final Path MUTABLE_JAR_PATH = Paths.get("dev", "app");
    private static final Path CLASSES_PATH = Paths.get(TARGET_DIR, "classes");
    private static final Path TEST_CLASSES_PATH = Paths.get(TARGET_DIR, "test-classes");

    private enum PathType {
        CLASSES,
        TEST_CLASSES,
        JAR,
        /**
         * Augmentation Mode from Quarkus. The app is deployed at "target/quarkus-app/dev/app" and behaves as an "exploded" jar.
         * Every resource is in this root path, so all the generated code must also be generated in this very same path.
         *
         * @see <a href=""https://quarkus.io/guides/maven-tooling#remote-development-mode>Remote Development Mode</a>
         */
        MUTABLE_JAR,
        UNKNOWN
    }

    protected QuarkusAppPaths(Set<Path> projectPaths, Collection<Path> classesPaths, boolean isJar, BuildTool bt, Path outputTarget) {
        super(projectPaths, classesPaths, isJar, bt, JarResultBuildStep.MAIN, outputTarget);
    }

    public static AppPaths from(Path outputTarget, Iterable<Path> paths, AppPaths.BuildTool bt) {
        final Set<Path> projectPaths = new LinkedHashSet<>();
        final Collection<Path> classesPaths = new ArrayList<>();
        boolean isJar = false;
        for (Path path : paths) {
            PathType pathType = getPathType(path);
            switch (pathType) {
                case CLASSES:
                    classesPaths.add(path);
                    projectPaths.add(path.getParent().getParent());
                    break;
                case TEST_CLASSES:
                    projectPaths.add(path.getParent().getParent());
                    break;
                case JAR:
                    isJar = true;
                    classesPaths.add(path);
                    projectPaths.add(path.getParent().getParent());
                    break;
                case MUTABLE_JAR:
                    // project, class, and target are all the same.
                    // also, we don't need any prefix (see constructor), hence passing GRADLE as the build tool
                    return new QuarkusAppPaths(Collections.singleton(path), Collections.singleton(path), false, BuildTool.GRADLE, path);
                case UNKNOWN:
                    classesPaths.add(path);
                    projectPaths.add(path);
                    break;
            }
        }
        return new QuarkusAppPaths(projectPaths, classesPaths, isJar, bt, outputTarget);
    }

    private static PathType getPathType(Path archiveLocation) {
        if (archiveLocation.endsWith(MUTABLE_JAR_PATH)) {
            return PathType.MUTABLE_JAR;
        }
        if (archiveLocation.endsWith(CLASSES_PATH)) {
            return PathType.CLASSES;
        }
        if (archiveLocation.endsWith(TEST_CLASSES_PATH)) {
            return PathType.TEST_CLASSES;
        }
        // Quarkus generates a file with extension .jar.original when doing a native compilation of a uberjar
        // TODO replace ".jar.original" with constant JarResultBuildStep.RENAMED_JAR_EXTENSION when it will be avialable in Quakrus 1.7
        if (archiveLocation.toString().toLowerCase().endsWith(".jar") || archiveLocation.toString().toLowerCase().endsWith(".jar.original")) {
            return PathType.JAR;
        }
        return PathType.UNKNOWN;
    }

}
