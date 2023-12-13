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
package org.drools.quarkus.deployment;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.drools.io.ByteArrayResource;
import org.drools.io.FileSystemResource;
import org.drools.io.InternalResource;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.util.IoUtils.readBytesFromInputStream;
import static org.kie.api.io.ResourceType.determineResourceType;

public class ResourceCollector {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceCollector.class);

    private ResourceCollector() {
        // utility class
    }

    /**
     * Returns a collection of CollectedResource from the given paths.
     * If a path is a jar, then walks inside the jar.
     */
    public static Collection<Resource> fromPaths(Path... paths) {
        Collection<Resource> resources = new ArrayList<>();

        for (Path path : paths) {
            if (path.toFile().isDirectory()) {
                Collection<Resource> res = fromDirectory(path);
                resources.addAll(res);
            } else if (path.getFileName().toString().endsWith(".jar") || path.getFileName().toString().endsWith(".jar.original")) {
                Collection<Resource> res = fromJarFile(path);
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
    public static Collection<Resource> fromJarFile(Path jarPath) {
        Collection<Resource> resources = new ArrayList<>();
        try (ZipFile zipFile = new ZipFile(jarPath.toFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                ResourceType resourceType = determineResourceType(entry.getName());
                if (resourceType == null) {
                    continue;
                }
                InternalResource resource = new ByteArrayResource(readBytesFromInputStream(zipFile.getInputStream(entry)), StandardCharsets.UTF_8.name());
                resource.setSourcePath(entry.getName());
                resource.setResourceType(resourceType);
                resources.add(resource);
            }
            return resources;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Returns a collection of CollectedResource from the given directory.
     */
    public static Collection<Resource> fromDirectory(Path path) {
        try (Stream<Path> paths = Files.walk(path)) {
            return paths.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .map(f -> toResource(f))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Resource toResource(File file) {
        ResourceType resourceType = determineResourceType(file.getName());
        if (resourceType == null) {
            return null;
        }
        Resource resource = new FileSystemResource(file, StandardCharsets.UTF_8.name());
        resource.setResourceType(resourceType);
        return resource;
    }
}
