/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.internal;

import java.nio.file.Path;
import java.util.Set;

public class SupportedExtensions {

    private static final Set<String> BPMN_EXTENSIONS = Set.of(".bpmn", ".bpmn2");
    private static final Set<String> SWF_EXTENSIONS = Set.of(".sw.yml", ".sw.yaml", ".sw.json");

    public static boolean isSourceFile(Path file) {
        return isSourceFile(file.toString());
    }

    public static boolean isSourceFile(String file) {
        return BPMN_EXTENSIONS.stream().anyMatch(file::endsWith)
                || SWF_EXTENSIONS.stream().anyMatch(file::endsWith);
    }

    public static Set<String> getBPMNExtensions() {
        return BPMN_EXTENSIONS;
    }

    public static Set<String> getSWFExtensions() {
        return SWF_EXTENSIONS;
    }

    private SupportedExtensions() {
    }
}
