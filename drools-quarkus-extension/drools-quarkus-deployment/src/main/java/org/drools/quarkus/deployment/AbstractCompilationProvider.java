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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

import io.quarkus.deployment.dev.JavaCompilationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.quarkus.util.deployment.DroolsQuarkusResourceUtils.HOT_RELOAD_SUPPORT_PATH;
import static org.drools.quarkus.util.deployment.DroolsQuarkusResourceUtils.getHotReloadSupportSource;

public abstract class AbstractCompilationProvider extends JavaCompilationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCompilationProvider.class);

    @Override
    public Set<String> handledSourcePaths() {
        return Collections.singleton("src" + File.separator + "main" + File.separator + "resources");
    }

    @Override
    public final void compile(Set<File> filesToCompile, Context quarkusContext) {
        Path path = pathOf(quarkusContext.getOutputDirectory().getPath(), HOT_RELOAD_SUPPORT_PATH + ".java");

        try {
            Files.write(path, getHotReloadSupportSource().getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        super.compile(Collections.singleton(path.toFile()), quarkusContext);
    }

    private static Path pathOf(String path, String relativePath) {
        Path p = Paths.get(path, relativePath);
        p.getParent().toFile().mkdirs();
        return p;
    }
}
