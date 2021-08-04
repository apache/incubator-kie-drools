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
package org.kie.kogito.codegen.prediction;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.GeneratedFileType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.core.AbstractGenerator;
import org.kie.kogito.codegen.prediction.config.PredictionConfigGenerator;
import org.kie.kogito.codegen.rules.IncrementalRuleCodegen;
import org.kie.kogito.pmml.openapi.api.PMMLOASResult;
import org.kie.kogito.pmml.openapi.factories.PMMLOASResultFactory;
import org.kie.pmml.commons.model.HasNestedModels;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLFactoryModel;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.stream.Collectors.toList;
import static org.kie.kogito.codegen.rules.KogitoPackageSources.getReflectConfigFile;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.evaluator.assembler.service.PMMLCompilerService.getKiePMMLModelsFromResourceWithSources;

public class PredictionCodegen extends AbstractGenerator {

    public static final String DMN_JPMML_CLASS = "org.kie.dmn.jpmml.DMNjPMMLInvocationEvaluator";
    public static final String GENERATOR_NAME = "predictions";
    private static final Logger LOGGER = LoggerFactory.getLogger(PredictionCodegen.class);
    private static final GeneratedFileType PMML_TYPE = GeneratedFileType.of("PMML", GeneratedFileType.Category.SOURCE);
    private static final String DECLARED_TYPE_IDENTIFIER = "org.drools.core.factmodel.GeneratedFact";
    private final List<PMMLResource> resources;

    public PredictionCodegen(KogitoBuildContext context, List<PMMLResource> resources) {
        super(context, GENERATOR_NAME, new PredictionConfigGenerator(context));
        this.resources = resources;
    }

    public static PredictionCodegen ofCollectedResources(KogitoBuildContext context,
            Collection<CollectedResource> resources) {
        if (context.hasClassAvailable(DMN_JPMML_CLASS)) {
            LOGGER.info("jpmml libraries available on classpath, skipping kogito-pmml parsing and compilation");
            return ofPredictions(context, Collections.emptyList());
        }
        List<PMMLResource> pmmlResources = resources.stream()
                .filter(r -> r.resource().getResourceType() == ResourceType.PMML)
                .flatMap(r -> parsePredictions(r.basePath(), Collections.singletonList(r.resource())).stream())
                .collect(toList());
        return ofPredictions(context, pmmlResources);
    }

    private static PredictionCodegen ofPredictions(KogitoBuildContext context, List<PMMLResource> resources) {
        return new PredictionCodegen(context, resources);
    }

    private static List<PMMLResource> parsePredictions(Path path, List<Resource> resources) {
        final InternalKnowledgeBase knowledgeBase = new KnowledgeBaseImpl("PMML", null);
        KnowledgeBuilderImpl kbuilderImpl = new KnowledgeBuilderImpl(knowledgeBase);
        List<PMMLResource> toReturn = new ArrayList<>();
        resources.forEach(resource -> {
            List<KiePMMLModel> kiePMMLModels = getKiePMMLModelsFromResourceWithSources(kbuilderImpl, resource);
            String modelPath = resource.getSourcePath();
            PMMLResource toAdd = new PMMLResource(kiePMMLModels, path, modelPath);
            toReturn.add(toAdd);
        });
        return toReturn;
    }

    @Override
    public Optional<ApplicationSection> section() {
        return Optional.of(new PredictionModelsGenerator(context(), applicationCanonicalName(), resources));
    }

    @Override
    protected Collection<GeneratedFile> internalGenerate() {
        List<GeneratedFile> files = new ArrayList<>();
        for (PMMLResource resource : resources) {
            generateModelsFromResource(files, resource);
        }
        return files;
    }

    @Override
    public boolean isEmpty() {
        return resources.isEmpty();
    }

    @Override
    public int priority() {
        return 40;
    }

    private void generateModelsFromResource(List<GeneratedFile> files, PMMLResource resource) {
        for (KiePMMLModel model : resource.getKiePmmlModels()) {
            generateModel(files, model, resource.getModelPath());
        }
    }

    private void generateModel(List<GeneratedFile> files, KiePMMLModel model, String modelPath) {
        generateModelBaseFiles(files, model, modelPath);
        generateModelRESTFiles(files, model);
    }

    private void generateModelBaseFiles(List<GeneratedFile> files, KiePMMLModel model, String modelPath) {
        if (model.getName() == null || model.getName().isEmpty()) {
            String errorMessage = String.format("Model name should not be empty inside %s", modelPath);
            throw new IllegalArgumentException(errorMessage);
        }
        if (!(model instanceof HasSourcesMap)) {
            String errorMessage = String.format("Expecting HasSourcesMap instance, retrieved %s inside %s", model.getClass().getName(), modelPath);
            throw new IllegalStateException(errorMessage);
        }

        Map<String, String> sourceMap = ((HasSourcesMap) model).getSourcesMap();
        for (Map.Entry<String, String> sourceMapEntry : sourceMap.entrySet()) {
            String path = sourceMapEntry.getKey().replace('.', File.separatorChar) + ".java";
            files.add(new GeneratedFile(PMML_TYPE, path, sourceMapEntry.getValue()));
        }

        Map<String, String> rulesSourceMap = ((HasSourcesMap) model).getRulesSourcesMap();
        if (rulesSourceMap != null) {
            List<String> pojoClasses = new ArrayList<>();

            for (Map.Entry<String, String> rulesSourceMapEntry : rulesSourceMap.entrySet()) {
                String path = rulesSourceMapEntry.getKey().replace('.', File.separatorChar) + ".java";
                files.add(new GeneratedFile(IncrementalRuleCodegen.RULE_TYPE, path, rulesSourceMapEntry.getValue()));

                if (rulesSourceMapEntry.getValue().contains(DECLARED_TYPE_IDENTIFIER)) {
                    pojoClasses.add(rulesSourceMapEntry.getKey());
                }
            }

            if (!pojoClasses.isEmpty()) {
                org.drools.modelcompiler.builder.GeneratedFile reflectConfigFile =
                        getReflectConfigFile(model.getKModulePackageName(), pojoClasses);
                files.add(new GeneratedFile(GeneratedFileType.RESOURCE, reflectConfigFile.getPath(), new String(reflectConfigFile.getData())));
            }
        }

        if (model instanceof HasNestedModels) {
            for (KiePMMLModel nestedModel : ((HasNestedModels) model).getNestedModels()) {
                generateModelBaseFiles(files, nestedModel, modelPath);
            }
        }
    }

    private void generateModelRESTFiles(List<GeneratedFile> files, KiePMMLModel model) {
        if (!context().hasRESTForGenerator(this) || (model instanceof KiePMMLFactoryModel)) {
            return;
        }

        PMMLRestResourceGenerator resourceGenerator = new PMMLRestResourceGenerator(context(), model, applicationCanonicalName());
        files.add(new GeneratedFile(REST_TYPE, resourceGenerator.generatedFilePath(), resourceGenerator.generate()));

        PMMLOASResult oasResult = PMMLOASResultFactory.getPMMLOASResult(model);
        try {
            String jsonContent = new ObjectMapper().writeValueAsString(oasResult.jsonSchemaNode());
            String jsonFile = String.format("%s.json", getSanitizedClassName(model.getName()));
            String jsonFilePath = String.format("META-INF/resources/%s", jsonFile);
            files.add(new GeneratedFile(GeneratedFileType.RESOURCE, jsonFilePath, jsonContent));
        } catch (Exception e) {
            LOGGER.warn("Failed to write OAS schema");
        }
    }
}
