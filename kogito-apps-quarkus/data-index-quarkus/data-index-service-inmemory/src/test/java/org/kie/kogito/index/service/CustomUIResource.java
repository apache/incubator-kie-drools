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
package org.kie.kogito.index.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class CustomUIResource implements QuarkusTestResourceLifecycleManager {
    private static final Logger LOG = LoggerFactory.getLogger(CustomUIResource.class);
    private Path tempDir;

    @Override
    public Map<String, String> start() {
        try {
            // Create a temp directory
            tempDir = Files.createTempDirectory("custom-ui");
            // Copy 'ui' resources from classpath to tempDir
            Path uiSource = Paths.get(getClass().getClassLoader().getResource("ui").toURI());
            Files.walk(uiSource).forEach(source -> {
                try {
                    Path dest = tempDir.resolve(uiSource.relativize(source).toString());
                    if (Files.isDirectory(source)) {
                        Files.createDirectories(dest);
                    } else {
                        Files.copy(source, dest);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to copy UI resource: " + source, e);
                }
            });
            LOG.info("Copied UI files to {}", tempDir);

            // Override the config property to point to the temp directory
            return Collections.singletonMap(
                    "kogito.data-index.ui.path", tempDir.toString());
        } catch (Exception e) {
            throw new RuntimeException("Unable to set up CustomUIResource", e);
        }
    }

    @Override
    public void stop() {
        if (tempDir != null) {
            try {
                Files.walk(tempDir)
                        .sorted((a, b) -> b.compareTo(a))
                        .forEach(path -> path.toFile().delete());
            } catch (Exception e) {
                LOG.warn("Failed to delete temp UI directory {}", tempDir, e);
            }
        }
    }
}
