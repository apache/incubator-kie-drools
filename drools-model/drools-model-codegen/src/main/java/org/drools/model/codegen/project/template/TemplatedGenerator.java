/**
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
package org.drools.model.codegen.project.template;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import org.drools.codegen.common.DroolsModelBuildContext;

import static com.github.javaparser.StaticJavaParser.parse;

/**
 * Utility class to handle multi platform template generation.
 * Template naming convention is the following:
 * templateName + context.name() + "Template.java"
 *
 * e.g.:
 * ApplicationConfigQuarkusTemplate.java
 * PredictionModelsSpringTemplate.java
 * ApplicationJavaTemplate.java
 *
 * By default targetTypeName value is ''templateName''
 * By default templateBasePath value is ''/class-templates/''
 * It is possible to specify a fallback context with fallbackContext
 */
public final class TemplatedGenerator {

    public static final String DEFAULT_TEMPLATE_BASE_PATH = "/class-templates/";
    protected static final String TEMPLATE_FORMAT = "{0}{1}{2}{3}";
    protected static final String TEMPLATE_SUFFIX = "Template.java";

    protected final String packageName;
    protected final String sourceFilePath;

    protected final String templateBasePath;
    protected final String templateName;

    protected final String targetTypeName;
    protected final String fallbackContext;
    protected final String contextName;

    private TemplatedGenerator(
            String contextName,
            String packageName,
            String targetTypeName,
            String templateBasePath,
            String templateName,
            String fallbackContext) {
        this.contextName = contextName;
        this.packageName = packageName;
        this.targetTypeName = targetTypeName;
        this.fallbackContext = fallbackContext;
        String targetCanonicalName = this.packageName + "." + this.targetTypeName;
        this.sourceFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.templateBasePath = templateBasePath;
        this.templateName = templateName;
    }

    public String generatedFilePath() {
        return sourceFilePath;
    }

    public String templateName() {
        return templateName;
    }

    public String targetTypeName() {
        return targetTypeName;
    }

    public Optional<CompilationUnit> compilationUnit() {
        String selectedResource = templatePath();
        if (selectedResource == null) {
            return Optional.empty();
        }

        try {
            CompilationUnit compilationUnit =
                    parse(getResource(selectedResource))
                            .setPackageDeclaration(packageName);

            return Optional.of(compilationUnit);
        } catch (ParseProblemException | AssertionError e) {
            throw new TemplateInstantiationException(targetTypeName, selectedResource, e);
        }
    }

    public CompilationUnit compilationUnitOrThrow(String errorMessage) {
        return compilationUnit().orElseThrow(() -> new InvalidTemplateException(
                this,
                errorMessage));
    }

    public CompilationUnit compilationUnitOrThrow() {
        return compilationUnitOrThrow("Missing template");
    }

    /**
     * Returns the valid template path if exists or null
     * 
     * @return
     */
    public String templatePath() {
        String resourcePath = uncheckedTemplatePath();
        if (getResource(resourcePath) != null) {
            return resourcePath;
        }

        String fallbackPath = createTemplatePath(templateBasePath, templateName, fallbackContext);
        if (fallbackContext != null && getResource(fallbackPath) != null) {
            return fallbackPath;
        }

        return null;
    }

    /**
     * Returns template path applying naming convention without verifying if exist
     * 
     * @return
     */
    public String uncheckedTemplatePath() {
        return createTemplatePath(templateBasePath, templateName, contextName);
    }

    private InputStream getResource(String path) {
        return this.getClass().getResourceAsStream(path);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static String createTemplatePath(String basePath, String templateName, String context) {
        return MessageFormat.format(TEMPLATE_FORMAT, basePath, templateName, context, TEMPLATE_SUFFIX);
    }

    public static class Builder {
        protected String packageName;
        protected String templateBasePath = DEFAULT_TEMPLATE_BASE_PATH;
        protected String targetTypeName;
        protected String fallbackContext;

        public Builder withTemplateBasePath(String templateBasePath) {
            Objects.requireNonNull(templateBasePath, "templateBasePath cannot be null");
            String prefix = !templateBasePath.startsWith("/") ? "/" : "";
            String postfix = !templateBasePath.endsWith("/") ? "/" : "";
            this.templateBasePath = prefix + templateBasePath + postfix;
            return this;
        }

        public Builder withPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder withTargetTypeName(String targetTypeName) {
            this.targetTypeName = targetTypeName;
            return this;
        }

        public Builder withFallbackContext(String fallbackContext) {
            this.fallbackContext = fallbackContext;
            return this;
        }

        public TemplatedGenerator build(DroolsModelBuildContext context, String templateName) {
            Objects.requireNonNull(templateName, "templateName cannot be null");
            String aPackageName = packageName == null ? context.getPackageName() : packageName;
            String aTargetTypeName = targetTypeName == null ? templateName : targetTypeName;
            return new TemplatedGenerator(context != null ? context.name() : null, aPackageName, aTargetTypeName, templateBasePath, templateName, fallbackContext);
        }
    }
}
