/*
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
package org.kie.kogito.codegen.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.codegen.common.GeneratedFile;
import org.kie.kogito.Addons;
import org.kie.kogito.codegen.api.ConfigGenerator;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

import static org.kie.kogito.codegen.core.CodegenUtils.newObject;

public class ApplicationConfigGenerator {

    public static final String TEMPLATE_CONFIG_FOLDER = "/class-templates/config/";
    private static final String CLASS_NAME = "ApplicationConfig";

    private final TemplatedGenerator templatedGenerator;
    private KogitoBuildContext context;

    private Set<String> addons = Collections.emptySet();

    private final Collection<ConfigGenerator> configGenerators = new ArrayList<>();

    public ApplicationConfigGenerator(KogitoBuildContext context) {
        this.templatedGenerator = TemplatedGenerator.builder()
                .withTemplateBasePath(TEMPLATE_CONFIG_FOLDER)
                .build(context, CLASS_NAME);
        this.context = context;

        if (!QuarkusKogitoBuildContext.CONTEXT_NAME.equals(context.name())) {
            this.configGenerators.add(new ConfigBeanGenerator(context));
        }
    }

    public ApplicationConfigGenerator addConfigGenerator(ConfigGenerator configGenerator) {
        this.configGenerators.add(configGenerator);
        return this;
    }

    public Collection<GeneratedFile> generate() {
        ArrayList<GeneratedFile> generatedFiles = new ArrayList<>();

        configGenerators.forEach(configGenerator -> generatedFiles.add(configGenerator.generate()));

        Collection<String> configClassNames = configGenerators.stream()
                .map(ConfigGenerator::configClassName)
                .collect(Collectors.toList());

        generatedFiles.add(generateApplicationConfigDescriptor(configClassNames));

        if (context.hasDI() && context.hasRESTGloballyAvailable()) {
            generatedFiles.add(ObjectMapperGenerator.generate(context));
        }

        return generatedFiles;
    }

    private GeneratedFile generateApplicationConfigDescriptor(Collection<String> configClassNames) {
        CompilationUnit compilationUnit = templatedGenerator.compilationUnitOrThrow();

        compilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                .ifPresent(this::replaceAddonPlaceHolder);

        // Add explicit initialization when no DI
        if (!context.hasDI()) {
            ClassOrInterfaceDeclaration cls = compilationUnit
                    .findFirst(ClassOrInterfaceDeclaration.class)
                    .orElseThrow(() -> new InvalidTemplateException(
                            templatedGenerator,
                            "Compilation unit doesn't contain a class or interface declaration!"));

            initConfigs(getInitStatement(cls), configClassNames);
        }

        return new GeneratedFile(ConfigGenerator.APPLICATION_CONFIG_TYPE,
                templatedGenerator.generatedFilePath(),
                compilationUnit.toString());
    }

    private void replaceAddonPlaceHolder(ClassOrInterfaceDeclaration cls) {
        // get the place holder and replace it with a list of the addons that have been found
        NameExpr addonsPlaceHolder =
                cls.findFirst(NameExpr.class, e -> e.getNameAsString().equals("$Addons$")).orElseThrow(() -> new InvalidTemplateException(
                        templatedGenerator,
                        "Missing $Addons$ placeholder"));

        ObjectCreationExpr addonsList = generateAddonsList();
        addonsPlaceHolder.getParentNode()
                .orElseThrow(() -> new InvalidTemplateException(
                        templatedGenerator,
                        "Cannot replace $Addons$ placeholder"))
                .replace(addonsPlaceHolder, addonsList);
    }

    private ObjectCreationExpr generateAddonsList() {
        MethodCallExpr asListOfAddons = new MethodCallExpr(new NameExpr("java.util.Set"), "of");
        for (String addon : addons) {
            asListOfAddons.addArgument(new StringLiteralExpr(addon));
        }

        return newObject(Addons.class, asListOfAddons);
    }

    public void withAddons(Set<String> addons) {
        this.addons = addons;
    }

    private BlockStmt getInitStatement(ClassOrInterfaceDeclaration cls) {
        return cls.findFirst(ConstructorDeclaration.class).map(ConstructorDeclaration::getBody)
                .orElseThrow(() -> new InvalidTemplateException(
                        templatedGenerator,
                        "Impossible to find super invocation"));
    }

    /**
     * For each config it produces a new instance follow naming convention and add it to superInvocation
     * e.g. section: ProcessConfig
     * produce:
     * e.g.: new ProcessConfig()
     * 
     * @param superInvocation
     * @param configClassNames
     */
    private void initConfigs(BlockStmt initInvocation, Collection<String> configClassNames) {
        initInvocation.findFirst(MethodCallExpr.class).ifPresent(call -> {
            configClassNames
                    .stream()
                    .map(config -> new ObjectCreationExpr().setType(config))
                    .forEach(call::addArgument);
        });

    }
}
