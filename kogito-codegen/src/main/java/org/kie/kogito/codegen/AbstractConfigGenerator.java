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

package org.kie.kogito.codegen;

import com.github.javaparser.ast.CompilationUnit;
import org.kie.kogito.codegen.context.KogitoBuildContext;

import java.util.Optional;

public class AbstractConfigGenerator {

    private final TemplatedGenerator templatedGenerator;

    public AbstractConfigGenerator(KogitoBuildContext buildContext, String packageName, String targetTypeName, String resourceCdi, String resourceSpring) {
        this.templatedGenerator = new TemplatedGenerator(
                buildContext,
                packageName,
                targetTypeName,
                resourceCdi,
                resourceSpring);
    }

    public Optional<GeneratedFile> generate() {
        Optional<CompilationUnit> compilationUnit = templatedGenerator.compilationUnit();
        return compilationUnit.map(cu ->
                new GeneratedFile(GeneratedFile.Type.APPLICATION_CONFIG,
                        templatedGenerator.generatedFilePath(),
                        cu.toString()));
    }
}
