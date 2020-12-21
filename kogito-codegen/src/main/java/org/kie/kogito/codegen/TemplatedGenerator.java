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
import org.kie.kogito.codegen.context.JavaKogitoBuildContext;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.context.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.context.SpringBootKogitoBuildContext;

import static com.github.javaparser.StaticJavaParser.parse;

/**
 * Utility class to handle multi platform template generation
 */
public final class TemplatedGenerator {

    private final String packageName;
    private final String sourceFilePath;

    private final String resourceCdi;
    private final String resourceSpring;
    private final String resourceDefault;

    private final String targetTypeName;
    private final KogitoBuildContext buildContext;

    public TemplatedGenerator(
            KogitoBuildContext buildContext,
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

        this.buildContext = buildContext;
        this.packageName = packageName;
        this.targetTypeName = targetTypeName;
        String targetCanonicalName = this.packageName + "." + this.targetTypeName;
        this.sourceFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.resourceCdi = resourceCdi;
        this.resourceSpring = resourceSpring;
        this.resourceDefault = resourceDefault;
    }

    public TemplatedGenerator(
            KogitoBuildContext buildContext,
            String packageName,
            String targetTypeName,
            String resourceCdi,
            String resourceSpring) {
        this(buildContext,
             packageName,
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

    public CompilationUnit compilationUnitOrThrow(String errorMessage) {
        return compilationUnit().orElseThrow(() -> new InvalidTemplateException(
                typeName(),
                templatePath(),
                errorMessage));
    }

    public CompilationUnit compilationUnitOrThrow() {
        return compilationUnitOrThrow("Missing template");
    }

    private String selectResource() {
        if (buildContext == null || buildContext instanceof JavaKogitoBuildContext) {
            return resourceDefault;
        } else if (buildContext instanceof QuarkusKogitoBuildContext) {
            return resourceCdi;
        } else if (buildContext instanceof SpringBootKogitoBuildContext) {
            return resourceSpring;
        } else {
            throw new IllegalArgumentException("Unknown buildContext " + buildContext);
        }
    }
}
