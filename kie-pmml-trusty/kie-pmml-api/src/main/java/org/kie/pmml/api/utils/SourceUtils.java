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
package org.kie.pmml.api.utils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.kie.pmml.api.enums.PMML_MODEL;

public class SourceUtils {

    /**
     * System property to dump generated PMML sources <b>yes</b>
     */
    public static final String DUMP_KIE_PMML_SOURCES = "dumpKiePmmlSources";
    /**
     * System property to override default output directory for generated PMML sources (inside <b>target</b>)
     */
    public static final String DUMP_KIE_PMML_DIRECTORY = "dumpKiePmmlDirectory";
    /**
     * Default output directory for generated PMML sources (<b>target/generated-pmml-sources</b>)
     */
    public static final String DEFAULT_DUMP_KIE_PMML_DIRECTORY = "generated-pmml-sources";

    private SourceUtils() {
    }

    public static void dumpSources(final Map<String, String> classNameSourceMap, final PMML_MODEL pmmlModel) throws IOException {
        final String dumpKiePmmlSources = System.getProperty(DUMP_KIE_PMML_SOURCES);
        if ("true".equalsIgnoreCase(dumpKiePmmlSources)) {
            String outputDir = System.getProperty(DUMP_KIE_PMML_DIRECTORY, DEFAULT_DUMP_KIE_PMML_DIRECTORY);
            String targetPath = String.format("%1$s%2$starget%2$s%3$s", System.getProperty("user.dir"),
                                              File.separator, outputDir);
            final File targetDirectory = new File(targetPath);
            if (!targetDirectory.exists()) {
                Files.createDirectories(targetDirectory.toPath().getParent());
            }
            dumpGeneratedSources(targetDirectory, classNameSourceMap, pmmlModel.getName().toLowerCase());
        }
    }

    private static void dumpGeneratedSources(File targetDirectory, Map<String, String> classNameSourceMap,
                                             String dumpKieSourcesFolder) {
        for (Map.Entry<String, String> entry : classNameSourceMap.entrySet()) {
            Path sourceDestinationPath = Paths.get(targetDirectory.getPath(), dumpKieSourcesFolder,
                                                   entry.getKey().replace('.', '/') + ".java");
            writeFile(sourceDestinationPath, entry.getValue().getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void writeFile(Path packagesDestinationPath, byte[] value) {
        try {
            if (!packagesDestinationPath.toFile().exists()) {
                Files.createDirectories(packagesDestinationPath.getParent());
            }
            Files.write(packagesDestinationPath, value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
