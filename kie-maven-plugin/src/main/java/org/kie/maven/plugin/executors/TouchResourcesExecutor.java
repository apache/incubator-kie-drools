/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.maven.plugin.executors;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * Compiles and serializes knowledge packages.
 */
public class TouchResourcesExecutor {

    private TouchResourcesExecutor() {
    }

    public static void touchResources(final String resDirectory,
                                      final List<String> kiebases,
                                      final Log log) throws MojoExecutionException {
        try {
            File outputFolder = new File(resDirectory);
            outputFolder.mkdirs();

            for (String kbase : kiebases) {
                log.info("Touching KBase: " + kbase);
                File file = new File(outputFolder, kbase.replace('.', '_').toLowerCase());
                file.createNewFile();
            }
        } catch (Exception e) {
            throw new MojoExecutionException("error", e);
        }
    }
}
