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

import java.util.Objects;
import java.util.Optional;

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
    private final KogitoBuildContext context;

    public TemplatedGenerator(
            KogitoBuildContext context,
            String targetTypeName,
            String resourceCdi,
            String resourceSpring,
            String resourceDefault) {
        this(context, context.getPackageName(), targetTypeName, resourceCdi, resourceSpring, resourceDefault);
    }

    public TemplatedGenerator(
            KogitoBuildContext context,
            String packageName,
            String targetTypeName,
            String resourceCdi,
            String resourceSpring,
            String resourceDefault) {

        Objects.requireNonNull(context, "context cannot be null");
        this.context = context;
        this.packageName = packageName == null ? context.getPackageName() : packageName;
        this.targetTypeName = targetTypeName;
        String targetCanonicalName = this.packageName + "." + this.targetTypeName;
        this.sourceFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.resourceCdi = resourceCdi;
        this.resourceSpring = resourceSpring;
        this.resourceDefault = resourceDefault;
    }

    public TemplatedGenerator(
            KogitoBuildContext context,
            String targetTypeName,
            String resourceCdi,
            String resourceSpring) {
        this(context,
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
        if (context instanceof JavaKogitoBuildContext) {
            return resourceDefault;
        } else if (context instanceof QuarkusKogitoBuildContext) {
            return resourceCdi;
        } else if (context instanceof SpringBootKogitoBuildContext) {
            return resourceSpring;
        } else {
            throw new IllegalArgumentException("Unknown context " + context.getClass().getCanonicalName());
        }
    }
}
