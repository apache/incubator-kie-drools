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

import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.di.SpringDependencyInjectionAnnotator;

import static com.github.javaparser.StaticJavaParser.parse;

public abstract class AbstractConfigGenerator {

    private final String packageName;
    private final String sourceFilePath;

    private final String resourceCdi;
    private final String resourceSpring;

    private DependencyInjectionAnnotator annotator;

    public AbstractConfigGenerator(String packageName, String targetTypeName, String resourceCdi, String resourceSpring) {
        this.packageName = packageName;
        String targetCanonicalName = this.packageName + "." + targetTypeName;
        this.sourceFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.resourceCdi = resourceCdi;
        this.resourceSpring = resourceSpring;
    }

    public String generatedFilePath() {
        return sourceFilePath;
    }

    public final AbstractConfigGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }

    public Optional<CompilationUnit> compilationUnit() {
        if (annotator == null) {
            return Optional.empty();
        }

        String resource;
        if (annotator instanceof CDIDependencyInjectionAnnotator) {
            resource = resourceCdi;
        } else if (annotator instanceof SpringDependencyInjectionAnnotator) {
            resource = resourceSpring;
        } else {
            throw new IllegalArgumentException("Unknown annotator " + annotator);
        }

        CompilationUnit compilationUnit =
                parse(this.getClass().getResourceAsStream(resource))
                        .setPackageDeclaration(packageName);

        return Optional.of(compilationUnit);
    }
}
