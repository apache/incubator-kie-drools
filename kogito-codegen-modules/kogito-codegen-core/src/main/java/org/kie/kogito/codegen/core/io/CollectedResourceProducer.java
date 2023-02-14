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
package org.kie.kogito.codegen.core.io;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.drools.io.ByteArrayResource;
import org.drools.io.FileSystemResource;
import org.drools.io.InternalResource;
import org.kie.api.io.Resource;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.util.IoUtils.readBytesFromInputStream;
import static org.kie.api.io.ResourceType.determineResourceType;
import static org.kie.kogito.codegen.api.utils.KogitoCodeGenConstants.IGNORE_HIDDEN_FILES_PROP;

public class CollectedResourceProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectedResourceProducer.class);

    private CollectedResourceProducer() {
        // utility class
    }

    /**
     * @see #fromPaths(boolean, Path...)
     *      <p>
     *      Ignores hidden files by default
     */
    public static Collection<CollectedResource> fromPaths(Path... paths) {
        return fromPaths(true, paths);
    }

    /**
     * Returns a collection of CollectedResource from the given paths.
     * If a path is a jar, then walks inside the jar.
     *
     * @param paths the paths to where to collect resources
     * @param ignoreHiddenFiles whether to ignore hidden files and directories
     * @see KogitoBuildContext#ignoreHiddenFiles()
     */
    public static Collection<CollectedResource> fromPaths(boolean ignoreHiddenFiles, Path... paths) {
        Collection<CollectedResource> resources = new ArrayList<>();

        for (Path path : paths) {
            if (path.toFile().isDirectory()) {
                Collection<CollectedResource> res = fromDirectory(path, ignoreHiddenFiles);
                resources.addAll(res);
            } else if (path.getFileName().toString().endsWith(".jar") || path.getFileName().toString().endsWith(".jar.original")) {
                Collection<CollectedResource> res = fromJarFile(path);
                resources.addAll(res);
            } else if (!path.toFile().exists()) {
                LOGGER.debug("Skipping '{}' because doesn't exist", path);
            } else {
                throw new IllegalArgumentException("Expected directory or archive, file given: " + path);
            }
        }

        return resources;
    }

    /**
     * Returns a collection of CollectedResource from the given jar file.
     */
    public static Collection<CollectedResource> fromJarFile(Path jarPath) {
        Collection<CollectedResource> resources = new ArrayList<>();
        try (ZipFile zipFile = new ZipFile(jarPath.toFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                InternalResource resource = new ByteArrayResource(readBytesFromInputStream(zipFile.getInputStream(entry)));
                resource.setSourcePath(entry.getName());
                resources.add(toCollectedResource(jarPath, entry.getName(), resource));
            }
            return resources;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * @see #fromDirectory(Path, boolean)
     *      <p>
     *      Ignores hidden files by default
     */
    public static Collection<CollectedResource> fromDirectory(Path path) {
        return fromDirectory(path, true);
    }

    /**
     * Returns a collection of CollectedResource from the given directory.
     *
     * @param path the path to where to start to collect resources
     * @param ignoreHiddenFiles whether to ignore hidden files and directories
     * @see KogitoBuildContext#ignoreHiddenFiles()
     */
    public static Collection<CollectedResource> fromDirectory(Path path, boolean ignoreHiddenFiles) {
        Collection<CollectedResource> resources = new ArrayList<>();
        try {
            Files.walkFileTree(path, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new CollectResourcesVisitor(path, ignoreHiddenFiles, resources));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return resources;
    }

    /**
     * @see #fromFiles(Path, boolean, File...)
     *      <p>
     *      Ignores hidden files by default
     */
    public static Collection<CollectedResource> fromFiles(Path basePath, File... files) {
        return fromFiles(basePath, true, files);
    }

    /**
     * Returns a collection of CollectedResource from the given files
     *
     * @param basePath the base path to where to start to collect resources
     * @param ignoreHiddenFiles whether to ignore hidden files and directories
     * @param files the files to read from the given base path
     * @see KogitoBuildContext#ignoreHiddenFiles()
     */
    public static Collection<CollectedResource> fromFiles(Path basePath, boolean ignoreHiddenFiles, File... files) {
        Collection<CollectedResource> resources = new ArrayList<>();
        if (ignoreHiddenFiles && basePath.toFile().isHidden()) {
            LOGGER.debug("Skipping directory because it's hidden: {}. You can disable this option by setting {} property to 'false'.", basePath, IGNORE_HIDDEN_FILES_PROP);
            return resources;
        }
        try (Stream<File> paths = Arrays.stream(files)) {
            paths.filter(f -> f.isFile() && !(ignoreHiddenFiles && f.isHidden()))
                    .map(f -> toCollectedResource(basePath, f))
                    .forEach(resources::add);
        }
        return resources;
    }

    private static CollectedResource toCollectedResource(Path basePath, File file) {
        Resource resource = new FileSystemResource(file);
        return toCollectedResource(basePath, file.getName(), resource);
    }

    private static CollectedResource toCollectedResource(Path basePath, String resourceName, Resource resource) {
        resource.setResourceType(determineResourceType(resourceName));
        return new CollectedResource(basePath, resource);
    }

    private static class CollectResourcesVisitor extends SimpleFileVisitor<Path> {
        private final Collection<CollectedResource> resources;
        private final Path initialPath;
        private final boolean ignoreHiddenFiles;

        public CollectResourcesVisitor(Path initialPath, boolean ignoreHiddenFiles, Collection<CollectedResource> resources) {
            this.resources = resources;
            this.initialPath = initialPath;
            this.ignoreHiddenFiles = ignoreHiddenFiles;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if (ignoreHiddenFiles && Files.isHidden(dir)) {
                LOGGER.debug("Skipping directory because it's hidden: {}. You can disable this option by setting {} property to 'false'.", dir, IGNORE_HIDDEN_FILES_PROP);
                return FileVisitResult.SKIP_SUBTREE;
            }
            return super.preVisitDirectory(dir, attrs);
        }

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
            if (ignoreHiddenFiles && Files.isHidden(path)) {
                LOGGER.debug("Skipping file because it's hidden: {}. You can disable this option by setting {} property to 'false'.", path, IGNORE_HIDDEN_FILES_PROP);
            } else {
                resources.add(toCollectedResource(initialPath, path.toFile()));
            }

            return super.visitFile(path, attrs);
        }
    }
}
