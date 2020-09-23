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

import java.text.MessageFormat;
import java.util.Optional;

import javax.lang.model.SourceVersion;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.di.SpringDependencyInjectionAnnotator;

import static com.github.javaparser.StaticJavaParser.parse;

public class TemplatedGenerator {

    private final String packageName;
    private final String sourceFilePath;

    private final String resourceCdi;
    private final String resourceSpring;
    private final String resourceDefault;

    private DependencyInjectionAnnotator annotator;
    private final String targetTypeName;

    public TemplatedGenerator(
            String packageName,
            String targetTypeName,
            String resourceCdi,
            String resourceSpring,
            String resourceDefault) {
        if (packageName == null) {
            throw new IllegalArgumentException("Package name cannot be undefined (null), please specify a package name!");
        }
        if (!SourceVersion.isName(packageName)) {
            throw new IllegalArgumentException(
                    MessageFormat.format(
                            "Package name \"{0}\" is not valid. It should be a valid Java package name.", packageName));
        }

        this.packageName = packageName;
        this.targetTypeName = targetTypeName;
        String targetCanonicalName = this.packageName + "." + this.targetTypeName;
        this.sourceFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.resourceCdi = resourceCdi;
        this.resourceSpring = resourceSpring;
        this.resourceDefault = resourceDefault;
    }

    public TemplatedGenerator(
            String packageName,
            String targetTypeName,
            String resourceCdi,
            String resourceSpring) {
        this(packageName,
             targetTypeName,
             resourceCdi,
             resourceSpring,
             null);
    }

    public String generatedFilePath() {
        return sourceFilePath;
    }

    public String templatePath() {
        return selectResource();
    }

    public String typeName() {
        return targetTypeName;
    }

    protected String packageName() {
        return this.packageName;
    }

    public TemplatedGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }

    public Optional<CompilationUnit> compilationUnit() {
        String selectedResource = selectResource();
        if (selectedResource == null) {
            return Optional.empty();
        }

        try {
            CompilationUnit compilationUnit =
                    parse(this.getClass().getResourceAsStream(selectedResource))
                            .setPackageDeclaration(packageName);

            return Optional.of(compilationUnit);
        } catch (ParseProblemException | AssertionError e) {
            throw new TemplateInstantiationException(targetTypeName, selectedResource, e);
        }
    }

    private String selectResource() {
        if (annotator == null) {
            if (resourceDefault == null) {
                return null;
            } else {
                return resourceDefault;
            }
        } else if (annotator instanceof CDIDependencyInjectionAnnotator) {
            return resourceCdi;
        } else if (annotator instanceof SpringDependencyInjectionAnnotator) {
            return resourceSpring;
        } else {
            throw new IllegalArgumentException("Unknown annotator " + annotator);
        }
    }
}
