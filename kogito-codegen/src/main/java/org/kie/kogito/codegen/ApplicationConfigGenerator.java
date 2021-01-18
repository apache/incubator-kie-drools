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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import org.kie.kogito.Addons;
import org.kie.kogito.codegen.context.KogitoBuildContext;

import static org.kie.kogito.codegen.CodegenUtils.newObject;

public class ApplicationConfigGenerator {

    public static final GeneratedFileType APPLICATION_CONFIG_TYPE = GeneratedFileType.of("APPLICATION_CONFIG", GeneratedFileType.Category.SOURCE);
    public static final String TEMPLATE_CONFIG_FOLDER = "/class-templates/config/";
    private static final String CLASS_NAME = "ApplicationConfig";

    private final TemplatedGenerator templatedGenerator;
    private KogitoBuildContext context;

    private Collection<String> addons = Collections.emptyList();

    private final Collection<ConfigGenerator> configGenerators = new ArrayList<>();

    public ApplicationConfigGenerator(KogitoBuildContext context) {
        this.templatedGenerator = TemplatedGenerator.builder()
                .withTemplateBasePath(TEMPLATE_CONFIG_FOLDER)
                .build(context, CLASS_NAME);
        this.context = context;

        this.configGenerators.add(new ConfigBeanGenerator(context));
    }

    public ApplicationConfigGenerator withConfigGenerator(ConfigGenerator configGenerator) {
        this.configGenerators.add(configGenerator);
        return this;
    }

    public Collection<GeneratedFile> generate() {
        ArrayList<GeneratedFile> generatedFiles = new ArrayList<>();

        configGenerators.forEach(configGenerator ->
                generatedFiles.add(configGenerator.generate()));

        Collection<String> configClassNames = configGenerators.stream()
                .map(ConfigGenerator::configClassName)
                .collect(Collectors.toList());

        generatedFiles.add(generateApplicationConfigDescriptor(configClassNames));

        return generatedFiles;
    }

    private GeneratedFile generateApplicationConfigDescriptor(Collection<String> configClassNames) {
        CompilationUnit compilationUnit = templatedGenerator.compilationUnitOrThrow();

        compilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                .ifPresent(this::replaceAddonPlaceHolder);

        // Add explicit initialization when no DI
        if(!context.hasDI()) {
            ClassOrInterfaceDeclaration cls = compilationUnit
                    .findFirst(ClassOrInterfaceDeclaration.class)
                    .orElseThrow(() -> new InvalidTemplateException(
                            templatedGenerator,
                            "Compilation unit doesn't contain a class or interface declaration!"));

            initConfigs(getSuperStatement(cls), configClassNames);
        }

        return new GeneratedFile(APPLICATION_CONFIG_TYPE,
                                 templatedGenerator.generatedFilePath(),
                                 compilationUnit.toString());
    }

    private void replaceAddonPlaceHolder(ClassOrInterfaceDeclaration cls) {
        // get the place holder and replace it with a list of the addons that have been found
        NameExpr addonsPlaceHolder =
                cls.findFirst(NameExpr.class, e -> e.getNameAsString().equals("$Addons$")).
                        orElseThrow(() -> new InvalidTemplateException(
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
        MethodCallExpr asListOfAddons = new MethodCallExpr(new NameExpr("java.util.Arrays"), "asList");
        for (String addon : addons) {
            asListOfAddons.addArgument(new StringLiteralExpr(addon));
        }

        return newObject(Addons.class, asListOfAddons);
    }

    public void withAddons(Collection<String> addons) {
        this.addons = addons;
    }

    private ExplicitConstructorInvocationStmt getSuperStatement(ClassOrInterfaceDeclaration cls) {
        return cls.findFirst(ExplicitConstructorInvocationStmt.class)
                .orElseThrow(() -> new InvalidTemplateException(
                        templatedGenerator,
                        "Impossible to find super invocation"));
    }

    /**
     * For each config it produces a new instance follow naming convention and add it to superInvocation
     *       e.g. section: ProcessConfig
     * produce:
     *       e.g.: new ProcessConfig()
     * @param superInvocation
     * @param configClassNames
     */
    private void initConfigs(ExplicitConstructorInvocationStmt superInvocation, Collection<String> configClassNames) {
        configClassNames.stream()
                .map(config -> new ObjectCreationExpr()
                        .setType(config))
                .forEach(superInvocation::addArgument);
    }
}
