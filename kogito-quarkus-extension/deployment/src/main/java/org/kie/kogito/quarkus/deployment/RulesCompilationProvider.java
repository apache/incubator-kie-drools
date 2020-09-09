/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.quarkus.deployment;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.Generator;
import org.kie.kogito.codegen.io.CollectedResource;
import org.kie.kogito.codegen.rules.IncrementalRuleCodegen;

public class RulesCompilationProvider extends KogitoCompilationProvider {

    @Override
    public Set<String> handledExtensions() {
        return Collections.singleton(".drl");
    }

    @Override
    protected Generator addGenerator(ApplicationGenerator appGen, Set<File> filesToCompile, Context context, ClassLoader cl) {
        Path resources = context.getProjectDirectory().toPath().resolve("src").resolve("main").resolve("resources");
        Collection<File> files = PackageWalker.getAllSiblings(filesToCompile);
        return appGen.withGenerator(
                IncrementalRuleCodegen.ofCollectedResources(
                        CollectedResource.fromFiles(resources, files.toArray(new File[0]))))
                .withClassLoader(cl)
                .withHotReloadMode();
    }

}
