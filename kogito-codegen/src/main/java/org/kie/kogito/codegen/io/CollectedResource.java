/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.io;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.io.impl.FileSystemResource;
import org.drools.core.io.internal.InternalResource;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

import static org.drools.core.util.IoUtils.readBytesFromInputStream;
import static org.kie.api.io.ResourceType.determineResourceType;

/**
 * A (Path basePath, Resource resource) pair
 */
public class CollectedResource {

    /**
     * Returns a collection of CollectedResource from the given paths.
     * If a path is a jar, then walks inside the jar.
     */
    public static Collection<CollectedResource> fromPaths(Path... paths) {
        Collection<CollectedResource> resources = new ArrayList<>();

        for (Path path : paths) {
            if (path.toFile().isDirectory()) {
                Collection<CollectedResource> res = fromDirectory(path);
                resources.addAll(res);
            } else if (path.getFileName().toString().endsWith(".jar") || path.getFileName().toString().endsWith(".jar.original")) {
                Collection<CollectedResource> res = fromJarFile(path);
                resources.addAll(res);
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
                ResourceType resourceType = determineResourceType(entry.getName());
                if (resourceType != null) {
                    InternalResource resource = new ByteArrayResource(readBytesFromInputStream(zipFile.getInputStream(entry)));
                    resource.setResourceType(resourceType);
                    resource.setSourcePath(entry.getName());
                    CollectedResource collectedResource = new CollectedResource(jarPath, resource);
                    resources.add(collectedResource);
                }
            }
            return resources;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Returns a collection of CollectedResource from the given directory.
     */
    public static Collection<CollectedResource> fromDirectory(Path path) {
        Collection<CollectedResource> resources = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(path)) {
            paths.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .map(f -> new FileSystemResource(f)
                            .setResourceType(determineResourceType(f.getName())))
                    .map(f -> new CollectedResource(path, f))
                    .forEach(resources::add);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return resources;
    }

    /**
     * Returns a collection of CollectedResource from the given files
     */
    public static Collection<CollectedResource> fromFiles(Path basePath, File... files) {
        Collection<CollectedResource> resources = new ArrayList<>();
        try (Stream<File> paths = Arrays.stream(files)) {
            paths.filter(File::isFile)
                    .map(f -> new FileSystemResource(f)
                            .setResourceType(determineResourceType(f.getName())))
                    .map(f -> new CollectedResource(basePath, f))
                    .forEach(resources::add);
        }
        return resources;
    }


    private final Path basePath;
    private final Resource resource;

    public CollectedResource(Path basePath, Resource resource) {
        // basePath must be a prefix of sourcePath
        // unless it is a jar file, then the check is ignored
        if (! basePath.toString().endsWith(".jar") &&
                ! Paths.get(resource.getSourcePath()).toAbsolutePath().startsWith(basePath.toAbsolutePath())) {
            throw new IllegalArgumentException(
                    String.format("basePath %s is not a prefix to the resource sourcePath %s",
                                  basePath, resource.getSourcePath()));
        }
        this.basePath = basePath;
        this.resource = resource;
    }

    public Path basePath() {
        return basePath;
    }

    public Resource resource() {
        return resource;
    }
}
