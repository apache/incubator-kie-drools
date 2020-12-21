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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.kie.kogito.Addons;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.decision.config.DecisionConfigGenerator;
import org.kie.kogito.codegen.prediction.config.PredictionConfigGenerator;
import org.kie.kogito.codegen.process.config.ProcessConfigGenerator;
import org.kie.kogito.codegen.rules.config.RuleConfigGenerator;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.kie.kogito.codegen.CodegenUtils.newObject;

public class ApplicationConfigGenerator {

    private static final String CLASS_NAME = "ApplicationConfig";
    private static final String RESOURCE_DEFAULT = "/class-templates/config/ApplicationConfigTemplate.java";
    private static final String RESOURCE_CDI = "/class-templates/config/CdiApplicationConfigTemplate.java";
    private static final String RESOURCE_SPRING = "/class-templates/config/SpringApplicationConfigTemplate.java";

    private final TemplatedGenerator templatedGenerator;

    private Collection<String> addons = Collections.emptyList();

    private ProcessConfigGenerator processConfig;
    private RuleConfigGenerator ruleConfig;
    private DecisionConfigGenerator decisionConfig;
    private PredictionConfigGenerator predictionConfig;
    private ConfigBeanGenerator configBean;

    public ApplicationConfigGenerator(KogitoBuildContext buildContext, String packageName) {
        this.templatedGenerator = new TemplatedGenerator(
                buildContext,
                packageName,
                CLASS_NAME,
                RESOURCE_CDI,
                RESOURCE_SPRING,
                RESOURCE_DEFAULT);

        this.configBean = new ConfigBeanGenerator(buildContext, packageName);
    }

    public ApplicationConfigGenerator withProcessConfig(ProcessConfigGenerator cfg) {
        this.processConfig = cfg;
        return this;
    }

    public ApplicationConfigGenerator withRuleConfig(RuleConfigGenerator cfg) {
        this.ruleConfig = cfg;
        return this;
    }

    public ApplicationConfigGenerator withDecisionConfig(DecisionConfigGenerator cfg) {
        this.decisionConfig = cfg;
        return this;
    }

    public ApplicationConfigGenerator withPredictionConfig(PredictionConfigGenerator cfg) {
        this.predictionConfig = cfg;
        return this;
    }

    public Collection<GeneratedFile> generate() {
        ArrayList<GeneratedFile> generatedFiles = new ArrayList<>();
        generatedFiles.add(generateApplicationConfigDescriptor());

        asList(processConfig, ruleConfig, predictionConfig, decisionConfig, configBean)
                .forEach(configGenerator -> ofNullable(configGenerator)
                        .flatMap(AbstractConfigGenerator::generate)
                        .ifPresent(generatedFiles::add));

        return generatedFiles;
    }

    private GeneratedFile generateApplicationConfigDescriptor() {
        CompilationUnit compilationUnit = templatedGenerator.compilationUnitOrThrow();

        compilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                .ifPresent(this::replaceAddonPlaceHolder);

        return new GeneratedFile(GeneratedFile.Type.APPLICATION_CONFIG,
                                 templatedGenerator.generatedFilePath(),
                                 compilationUnit.toString());
    }

    private void replaceAddonPlaceHolder(ClassOrInterfaceDeclaration cls) {
        // get the place holder and replace it with a list of the addons that have been found
        NameExpr addonsPlaceHolder =
                cls.findFirst(NameExpr.class, e -> e.getNameAsString().equals("$Addons$")).
                        orElseThrow(() -> new InvalidTemplateException(
                                templatedGenerator.typeName(),
                                templatedGenerator.templatePath(),
                                "Missing $Addons$ placeholder"));

        ObjectCreationExpr addonsList = generateAddonsList();
        addonsPlaceHolder.getParentNode()
                .orElseThrow(() -> new InvalidTemplateException(
                        templatedGenerator.typeName(),
                        templatedGenerator.templatePath(),
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
}
