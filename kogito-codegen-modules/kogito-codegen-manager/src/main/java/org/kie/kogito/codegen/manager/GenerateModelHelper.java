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
package org.kie.kogito.codegen.manager;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.drools.codegen.common.GeneratedFile;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.core.ApplicationGenerator;
import org.kie.kogito.codegen.core.utils.ApplicationGeneratorDiscovery;

import static org.drools.codegen.common.GeneratedFileType.COMPILED_CLASS;
import static org.kie.kogito.codegen.manager.CompilerHelper.RESOURCES;
import static org.kie.kogito.codegen.manager.CompilerHelper.SOURCES;

public class GenerateModelHelper {

    private GenerateModelHelper() {
    }

    public static Map<String, Collection<GeneratedFile>> generateModelFiles(KogitoBuildContext kogitoBuildContext, boolean generatePartial) {
        ApplicationGenerator appGen = ApplicationGeneratorDiscovery.discover(kogitoBuildContext);

        Collection<GeneratedFile> generatedFiles;
        if (generatePartial) {
            generatedFiles = appGen.generateComponents();
        } else {
            generatedFiles = appGen.generate();
        }
        Collection<GeneratedFile> generatedClasses = new HashSet<>();
        Collection<GeneratedFile> generatedResources = new HashSet<>();
        generatedFiles.forEach(generatedFile -> {
            switch (generatedFile.category()) {
                case SOURCE -> generatedClasses.add(generatedFile);
                case INTERNAL_RESOURCE, STATIC_HTTP_RESOURCE -> generatedResources.add(generatedFile);
                case COMPILED_CLASS -> generatedResources.add(new GeneratedFile(COMPILED_CLASS, convertPath(generatedFile.path().toString()), generatedFile.contents()));
                default -> throw new IllegalStateException("Unexpected file with category: " + generatedFile.category());
            }
        });
        return Map.of(SOURCES, generatedClasses, RESOURCES, generatedResources);
    }

    private static String convertPath(String toConvert) {
        return toConvert.replace('.', File.separatorChar) + ".class";
    }
}
