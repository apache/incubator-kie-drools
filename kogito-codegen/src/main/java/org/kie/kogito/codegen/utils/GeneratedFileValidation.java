/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.utils;

import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.GeneratedFileType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class GeneratedFileValidation {

    private GeneratedFileValidation() {
        // utility class
    }

    public static void validateGeneratedFileTypes(Collection<GeneratedFile> generatedFiles, Collection<GeneratedFileType.Category> expectedTypes) {
        Collection<GeneratedFile> unexpectedGeneratedFiles = generatedFiles.stream()
                .filter(generatedFile -> !expectedTypes.contains(generatedFile.category()))
                .collect(Collectors.toCollection(ArrayList::new));

        if(!unexpectedGeneratedFiles.isEmpty()) {
            throw new IllegalStateException("Found unexpected files:\n" +
                    unexpectedGeneratedFiles.stream()
                            .map(x -> x.category().name() + " " +
                                    x.type().name() + ": "
                                    + x.relativePath())
                            .collect(Collectors.joining("\n")));
        }
    }
}
