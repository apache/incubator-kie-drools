/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.builder;

import java.util.function.Predicate;

import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieModule;

public interface InternalKieBuilder extends KieBuilder {

    KieBuilderSet createFileSet(String... files);

    KieModule getKieModuleIgnoringErrors();

    IncrementalResults incrementalBuild();

    /**
     * Builds all the KieBases contained in the KieModule for which this KieBuilder has been created
     * @param classFilter Used to prevent compilation of Java source files.
     *          This filter will be tested on all source file names before they are compiled.
     *          Only source files for which the filter returns true will be compiled.
     */
    KieBuilder buildAll(Predicate<String> classFilter);

}
