/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.kie.kogito.codegen.decision.config.DecisionConfigGenerator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.prediction.config.PredictionConfigGenerator;
import org.kie.kogito.codegen.process.config.ProcessConfigGenerator;
import org.kie.kogito.codegen.rules.config.RuleConfigGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.codegen.ApplicationGenerator.log;

public class ConfigGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigGenerator.class);
    private final ApplicationConfigGenerator applicationConfigGenerator;

    private DependencyInjectionAnnotator annotator;
    private ProcessConfigGenerator processConfig;
    private RuleConfigGenerator ruleConfig;
    private DecisionConfigGenerator decisionConfig;
    private PredictionConfigGenerator predictionConfig;

    private String packageName;
    private final String sourceFilePath;
    private final String targetTypeName;
    private final String targetCanonicalName;
    private Collection<String> addons;

    public ConfigGenerator(String packageName) {
        this.packageName = packageName;
        this.targetTypeName = "ApplicationConfig";
        this.targetCanonicalName = this.packageName + "." + targetTypeName;
        this.sourceFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.applicationConfigGenerator = new ApplicationConfigGenerator(packageName);
    }

    public ConfigGenerator withProcessConfig(ProcessConfigGenerator cfg) {
        this.processConfig = cfg;
        if (this.processConfig != null) {
            this.processConfig.withDependencyInjection(annotator);
        }
        return this;
    }

    public ConfigGenerator withRuleConfig(RuleConfigGenerator cfg) {
        this.ruleConfig = cfg;
        if (this.ruleConfig != null) {
            this.ruleConfig.withDependencyInjection(annotator);
        }
        return this;
    }

    public ConfigGenerator withDecisionConfig(DecisionConfigGenerator cfg) {
        this.decisionConfig = cfg;
        if (this.decisionConfig != null) {
            this.decisionConfig.withDependencyInjection(annotator);
        }
        return this;
    }

    public ConfigGenerator withPredictionConfig(PredictionConfigGenerator cfg) {
        this.predictionConfig = cfg;
        if (this.predictionConfig != null) {
            this.predictionConfig.withDependencyInjection(annotator);
        }
        return this;
    }

    public ConfigGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        this.applicationConfigGenerator.withDependencyInjection(annotator);
        return this;
    }

    public ObjectCreationExpr newInstance() {
        return new ObjectCreationExpr()
                .setType(targetCanonicalName);
    }

    public Collection<GeneratedFile> generate() {
        ArrayList<GeneratedFile> generatedFiles = new ArrayList<>();
        generatedFiles.add(generateApplicationConfigDescriptor());

        generateProcessConfigDescriptor().ifPresent(generatedFiles::add);
        generateRuleConfigDescriptor().ifPresent(generatedFiles::add);
        generatePredictionConfigDescriptor().ifPresent(generatedFiles::add);
        generateDecisionConfigDescriptor().ifPresent(generatedFiles::add);
        generateBeanConfig().ifPresent(generatedFiles::add);

        return generatedFiles;
    }

    private GeneratedFile generateApplicationConfigDescriptor() {
        CompilationUnit compilationUnit = applicationConfigGenerator.compilationUnit()
                .orElseThrow(() -> new InvalidTemplateException(
                        applicationConfigGenerator.typeName(),
                        applicationConfigGenerator.templatePath(),
                        "Missing template"));
        return new GeneratedFile(GeneratedFile.Type.APPLICATION_CONFIG,
                                 applicationConfigGenerator.generatedFilePath(),
                                 log(compilationUnit.toString()).getBytes(StandardCharsets.UTF_8));
    }

    private Optional<GeneratedFile> generateProcessConfigDescriptor() {
        if (processConfig == null) {
            return Optional.empty();
        }
        Optional<CompilationUnit> compilationUnit = processConfig.compilationUnit();
        return compilationUnit.map(c -> new GeneratedFile(GeneratedFile.Type.APPLICATION_CONFIG,
                                                          processConfig.generatedFilePath(),
                                                          log(c.toString()).getBytes(StandardCharsets.UTF_8)));
    }

    private Optional<GeneratedFile> generateRuleConfigDescriptor() {
        if (ruleConfig == null) {
            return Optional.empty();
        }
        Optional<CompilationUnit> compilationUnit = ruleConfig.compilationUnit();
        return compilationUnit.map(c -> new GeneratedFile(GeneratedFile.Type.APPLICATION_CONFIG,
                                                          ruleConfig.generatedFilePath(),
                                                          log(c.toString()).getBytes(StandardCharsets.UTF_8)));
    }

    private Optional<GeneratedFile> generateDecisionConfigDescriptor() {
        if (decisionConfig == null) {
            return Optional.empty();
        }
        Optional<CompilationUnit> compilationUnit = decisionConfig.compilationUnit();
        return compilationUnit.map(c -> new GeneratedFile(GeneratedFile.Type.APPLICATION_CONFIG,
                                                          decisionConfig.generatedFilePath(),
                                                          log(c.toString()).getBytes(StandardCharsets.UTF_8)));
    }

    private Optional<GeneratedFile> generatePredictionConfigDescriptor() {
        if (predictionConfig == null) {
            return Optional.empty();
        }
        Optional<CompilationUnit> compilationUnit = predictionConfig.compilationUnit();
        return compilationUnit.map(c -> new GeneratedFile(GeneratedFile.Type.APPLICATION_CONFIG,
                                                          predictionConfig.generatedFilePath(),
                                                          log(c.toString()).getBytes(StandardCharsets.UTF_8)));
    }

    private Optional<GeneratedFile> generateBeanConfig() {
        ConfigBeanGenerator configBeanGenerator = new ConfigBeanGenerator(packageName);
        configBeanGenerator.withDependencyInjection(annotator);

        Optional<CompilationUnit> compilationUnit = configBeanGenerator.compilationUnit();
        return compilationUnit.map(c -> new GeneratedFile(GeneratedFile.Type.APPLICATION_CONFIG,
                                                          configBeanGenerator.generatedFilePath(),
                                                          log(c.toString()).getBytes(StandardCharsets.UTF_8)));
    }

    public void withAddons(Collection<String> addons) {
        this.applicationConfigGenerator.withAddons(addons);
    }
}
