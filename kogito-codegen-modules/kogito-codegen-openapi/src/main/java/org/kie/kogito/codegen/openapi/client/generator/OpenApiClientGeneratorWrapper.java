/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.openapi.client.generator;

import java.io.File;
import java.util.List;

import org.kie.kogito.codegen.openapi.client.OpenApiSpecDescriptor;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;
import org.openapitools.codegen.config.GlobalSettings;

/**
 * Wrapper for the OpenAPIGen tool.
 * This is the same as calling the Maven plugin or the CLI.
 * We are wrapping into a class to generate code that meet our requirements.
 * In the future we can consider exposing some of this properties for fine tune configuration.
 *
 * @see <a href="https://openapi-generator.tech/docs/generators/java">OpenAPI Generator Client for Java</a>
 */
public class OpenApiClientGeneratorWrapper {

    private static final String FALSE = "false";
    private static final String TRUE = "true";

    private static final String MODEL_PACKAGE = "model";
    private static final String GENERATOR_NAME = "java";
    private static final String VERBOSE = "verbose";
    private static final String ONCE_LOGGER = "org.openapitools.codegen.utils.oncelogger.enabled";

    private final KogitoJavaClientCodegen kogitoCodegen;
    private final CodegenConfigurator configurator;
    private final DefaultGenerator generator;

    private OpenApiClientGeneratorWrapper(final String specFilePath, final String outputDir) {
        // do not generate docs nor tests
        GlobalSettings.setProperty(CodegenConstants.API_DOCS, FALSE);
        GlobalSettings.setProperty(CodegenConstants.API_TESTS, FALSE);
        GlobalSettings.setProperty(CodegenConstants.MODEL_TESTS, FALSE);
        GlobalSettings.setProperty(CodegenConstants.MODEL_DOCS, FALSE);
        // generates every Api and Supporting files
        GlobalSettings.setProperty(CodegenConstants.APIS, "");
        GlobalSettings.setProperty(CodegenConstants.SUPPORTING_FILES, "");
        // logging
        GlobalSettings.setProperty(VERBOSE, FALSE);
        GlobalSettings.setProperty(ONCE_LOGGER, TRUE);

        this.configurator = new CodegenConfigurator();
        this.configurator.setInputSpec(specFilePath);
        this.configurator.setGeneratorName(GENERATOR_NAME);
        this.generator = new DefaultGenerator();
        this.kogitoCodegen = new KogitoJavaClientCodegen(this.generator);
        this.kogitoCodegen.setOutputDir(outputDir);
    }

    /**
     * Generates the OpenAPI project based on the given OpenAPI spec file
     *
     * @param specFilePath a valid path in the local system to the spec file
     * @param outputDir a valid path in the local system where the files will be generated
     * @return a new instance of {@link OpenApiClientGeneratorWrapper}
     */
    public static OpenApiClientGeneratorWrapper newInstance(final String specFilePath, final String outputDir) {
        return new OpenApiClientGeneratorWrapper(specFilePath, outputDir);
    }

    public OpenApiClientGeneratorWrapper withPackage(final String pkg) {
        this.kogitoCodegen.setApiPackage(pkg);
        this.kogitoCodegen.setInvokerPackage(pkg);
        this.kogitoCodegen.setModelPackage(pkg + "." + MODEL_PACKAGE);
        return this;
    }

    public List<File> generate(final OpenApiSpecDescriptor descriptor) {
        final List<File> generatedFiles = this.generator.opts(
                this.configurator
                        .toClientOptInput()
                        .config(this.kogitoCodegen))
                .generate();
        this.kogitoCodegen.processGeneratedOperations(descriptor);
        return generatedFiles;
    }
}
