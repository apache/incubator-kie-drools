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
package org.jbpm.flow.migration.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jbpm.flow.migration.MigrationPlanFile;
import org.jbpm.flow.migration.MigrationPlanFileProvider;
import org.jbpm.util.JbpmClassLoaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.emptyMap;

public class FileSystemMigrationPlanFileProvider implements MigrationPlanFileProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemMigrationPlanFileProvider.class);

    public static final String EXPLODED_MIGRATION_PLAN_FOLDER = "META-INF/migration-plan/";
    public static final String MIGRATION_PLAN_FOLDER = "/META-INF/migration-plan/";

    private List<URI> rootPaths;

    public FileSystemMigrationPlanFileProvider() {
        try {
            List<URL> url = Collections.list(JbpmClassLoaderUtil.findClassLoader().getResources(MIGRATION_PLAN_FOLDER));
            if (url.isEmpty()) {
                url = Collections.list(JbpmClassLoaderUtil.findClassLoader().getResources(EXPLODED_MIGRATION_PLAN_FOLDER));
            }
            this.rootPaths = url.stream().map(this::toURI).filter(Optional::isPresent).map(Optional::get).toList();
        } catch (IOException e) {
            throw new IllegalArgumentException("error trying to get Migration Plan folder");
        }
    }

    private Optional<URI> toURI(URL e) {
        try {
            return Optional.of(e.toURI());
        } catch (URISyntaxException ex) {
            return Optional.<URI> empty();
        }
    }

    public FileSystemMigrationPlanFileProvider(URI rootPath) {
        this.rootPaths = List.of(rootPath);
    }

    @Override
    public List<MigrationPlanFile> listMigrationPlanFiles(String... extensions) {
        List<String> allowedExtensions = Arrays.asList(extensions);
        List<MigrationPlanFile> migrationPlanFiles = new ArrayList<>();
        for (URI rootPath : rootPaths) {
            if ("jar".equals(rootPath.getScheme())) {
                migrationPlanFiles.addAll(walkInJarPaths(rootPath, allowedExtensions));
            } else {
                migrationPlanFiles.addAll(walkingPaths(Path.of(rootPath), allowedExtensions));
            }
        }
        return migrationPlanFiles;
    }

    private List<MigrationPlanFile> walkInJarPaths(URI baseURI, List<String> extensions) {
        LOGGER.debug("Searching Migration Plans in rootPath {}", baseURI);
        try (FileSystem fileSystem = FileSystems.newFileSystem(baseURI, emptyMap(), JbpmClassLoaderUtil.findClassLoader())) {
            Path myPath = fileSystem.getPath(MIGRATION_PLAN_FOLDER);
            if (!Files.exists(myPath)) {
                LOGGER.debug("There was not any migration plan found within jar {}", myPath);
                return Collections.emptyList();
            }
            return walkingPaths(myPath, extensions);
        } catch (Exception e) {
            LOGGER.error("There was a problem during migration plan execution", e);
            return Collections.emptyList();
        }
    }

    public List<MigrationPlanFile> walkingPaths(Path basePath, List<String> allowedExtensions) {
        List<MigrationPlanFile> migrationPlans = new ArrayList<>();
        if (!Files.exists(basePath)) {
            LOGGER.debug("There was not any migration plan found in path {}", basePath);
            return Collections.emptyList();
        }
        try (Stream<Path> walk = Files.walk(basePath)) {
            migrationPlans = walk
                    .filter(Files::isRegularFile)
                    .filter(e -> isMigrationPlanFileExtension(e, allowedExtensions))
                    .map(this::toMigrationPlanFile)
                    .toList();
        } catch (IOException e) {
            LOGGER.error("There was an error trying to search for migration plan in basePath {}", basePath, e);
        }
        return migrationPlans;
    }

    private boolean isMigrationPlanFileExtension(Path path, List<String> extensions) {
        Predicate<String> predicate = new Predicate<String>() {
            @Override
            public boolean test(String extension) {
                return path.toString().endsWith(extension);
            }
        };
        return extensions.stream().anyMatch(predicate);
    }

    private MigrationPlanFile toMigrationPlanFile(Path path) {
        try {
            return new MigrationPlanFile(path, Files.readAllBytes(path));
        } catch (IOException e) {
            LOGGER.error("There was an error trying to search for migration plan in basePath {}", e);
            return new MigrationPlanFile(path, new byte[0]);
        }
    }

}
