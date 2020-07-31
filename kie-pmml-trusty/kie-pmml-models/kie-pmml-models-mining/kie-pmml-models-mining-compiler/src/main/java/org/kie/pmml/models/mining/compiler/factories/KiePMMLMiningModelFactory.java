/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.models.mining.compiler.factories;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.mining.MiningModel;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionFactory.getKiePMMLExtensions;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFieldName;
import static org.kie.pmml.models.mining.compiler.factories.KiePMMLSegmentationFactory.getSegmentation;
import static org.kie.pmml.models.mining.compiler.factories.KiePMMLSegmentationFactory.getSegmentationSourcesMap;

public class KiePMMLMiningModelFactory {

    static final String SEGMENTATIONNAME_TEMPLATE = "%s_Segmentation";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLMiningModelFactory.class.getName());

    private KiePMMLMiningModelFactory() {
        // Avoid instantiation
    }

    public static KiePMMLMiningModel getKiePMMLMiningModel(final DataDictionary dataDictionary,
                                                           final TransformationDictionary transformationDictionary,
                                                           final MiningModel model,
                                                           final KnowledgeBuilder kBuilder) {
        logger.debug("getKiePMMLMiningModel {}", model);
        String name = model.getModelName();
        Optional<String> targetFieldName = getTargetFieldName(dataDictionary, model);
        List<KiePMMLExtension> extensions = getKiePMMLExtensions(model.getExtensions());
        return KiePMMLMiningModel.builder(name, extensions, MINING_FUNCTION.byName(model.getMiningFunction().value()))
                .withAlgorithmName(model.getAlgorithmName())
                .withScorable(model.isScorable())
                .withSegmentation(getSegmentation(dataDictionary,
                                                  transformationDictionary,
                                                  model.getSegmentation(),
                                                  String.format(SEGMENTATIONNAME_TEMPLATE, model.getModelName()),
                                                  kBuilder))
                .withTargetField(targetFieldName.orElse(null))
                .build();
    }

    public static Map<String, String> getKiePMMLMiningModelSourcesMap(final DataDictionary dataDictionary,
                                                                      final TransformationDictionary transformationDictionary,
                                                                      final MiningModel model,
                                                                      final String parentPackageName,
                                                                      final KnowledgeBuilder kBuilder) throws IOException {
        logger.trace("getKiePMMLMiningModelSourcesMap {} {} {}", dataDictionary, model, parentPackageName);
        return getSegmentationSourcesMap(parentPackageName,
                                         dataDictionary,
                                         transformationDictionary,
                                         model.getSegmentation(),
                                         String.format(SEGMENTATIONNAME_TEMPLATE, model.getModelName()),
                                         kBuilder);
        //        String className = getSanitizedClassName(model.getModelName());
//        String modelName = model.getModelName();
//        String targetFieldName = getTargetFieldName(dataDictionary, model).orElse(null);
//        List<KiePMMLOutputField> outputFields = getOutputFields(model);
//        Map<String, KiePMMLTableSourceCategory> tablesSourceMap = getRegressionTablesMap(dataDictionary, model,
//        targetFieldName, outputFields, packageName);
//        CompilationUnit templateCU = getFromFileName(KIE_PMML_REGRESSION_MODEL_TEMPLATE_JAVA);
//        CompilationUnit cloneCU = templateCU.clone();
//        cloneCU.setPackageDeclaration(packageName);
//        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(KIE_PMML_REGRESSION_MODEL_TEMPLATE)
//                .orElseThrow(() -> new RuntimeException(MAIN_CLASS_NOT_FOUND));
//        modelTemplate.setName(className);
//        String nestedTable = tablesSourceMap.size() == 1 ? tablesSourceMap.keySet().iterator().next() :
//                tablesSourceMap.keySet().stream().filter(tableName -> tableName.startsWith
//                ("KiePMMLRegressionTableClassification"))
//                        .findFirst().orElseThrow(() -> new RuntimeException("Failed to find expected
//                        KiePMMLRegressionTableClassification"));
//        final ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().orElseThrow(()
//        -> new KiePMMLInternalException(String.format("Missing default constructor in ClassOrInterfaceDeclaration
//        %s ", modelTemplate.getName())));
//        populateConstructor(className, nestedTable, constructorDeclaration, targetFieldName, MINING_FUNCTION.byName
//        (model.getMiningFunction().value()), modelName);
//        addTransformationsInClassOrInterfaceDeclaration(modelTemplate, transformationDictionary, model
//        .getLocalTransformations());
//        Map<String, String> toReturn = tablesSourceMap.entrySet().stream().collect(Collectors.toMap(entry ->
//        packageName + "." + entry.getKey(), entry -> entry.getValue().getSource()));
//        String fullClassName = packageName + "." + className;
//        toReturn.put(fullClassName, cloneCU.toString());
//        return toReturn;
    }
}
