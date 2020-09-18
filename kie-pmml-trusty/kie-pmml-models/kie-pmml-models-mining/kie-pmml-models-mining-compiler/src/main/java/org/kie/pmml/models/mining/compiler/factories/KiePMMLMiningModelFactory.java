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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.mining.MiningModel;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionFactory.getKiePMMLExtensions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLOutputFieldFactory.getOutputFields;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.addOutputFieldsPopulation;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.addTransformationsInClassOrInterfaceDeclaration;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.setConstructorSuperNameInvocation;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFieldName;
import static org.kie.pmml.models.mining.compiler.factories.KiePMMLSegmentationFactory.getSegmentation;
import static org.kie.pmml.models.mining.compiler.factories.KiePMMLSegmentationFactory.getSegmentationSourcesMap;

public class KiePMMLMiningModelFactory {

    static final String SEGMENTATIONNAME_TEMPLATE = "%s_Segmentation";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLMiningModelFactory.class.getName());
    static final String KIE_PMML_MINING_MODEL_TEMPLATE_JAVA = "KiePMMLMiningModelTemplate.tmpl";
    static final String KIE_PMML_MINING_MODEL_TEMPLATE = "KiePMMLMiningModelTemplate";


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
                                                                      final KnowledgeBuilder kBuilder,
                                                                      final List<KiePMMLModel> nestedModels) {
        logger.trace("getKiePMMLMiningModelSourcesMap {} {} {}", dataDictionary, model, parentPackageName);
        final String segmentationName = String.format(SEGMENTATIONNAME_TEMPLATE, model.getModelName());
        final Map<String, String> toReturn = getSegmentationSourcesMap(parentPackageName,
                                                                       dataDictionary,
                                                                       transformationDictionary,
                                                                       model.getSegmentation(),
                                                                       segmentationName,
                                                                       kBuilder,
                                                                       nestedModels);
        String segmentationClass = getSanitizedPackageName(parentPackageName + "." + segmentationName) + "." + getSanitizedClassName(segmentationName);
        if (!toReturn.containsKey(segmentationClass)) {
            throw new KiePMMLException("Expected generated class " + segmentationClass + " not found");
        }
        String className = getSanitizedClassName(model.getModelName());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, parentPackageName, KIE_PMML_MINING_MODEL_TEMPLATE_JAVA, KIE_PMML_MINING_MODEL_TEMPLATE);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        String modelName = model.getModelName();
        String targetFieldName = getTargetFieldName(dataDictionary, model).orElse(null);
        List<KiePMMLOutputField> outputFields = getOutputFields(model);
        final ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())));
        setConstructor(className,
                       constructorDeclaration,
                       targetFieldName,
                       MINING_FUNCTION.byName(model.getMiningFunction().value()),
                       modelName,
                       segmentationClass);
        addOutputFieldsPopulation(constructorDeclaration.getBody(), outputFields);
        addTransformationsInClassOrInterfaceDeclaration(modelTemplate, transformationDictionary, model.getLocalTransformations());
        toReturn.put(getFullClassName(cloneCU), cloneCU.toString());
        return toReturn;
    }

    static void setConstructor(final String generatedClassName,
                               final ConstructorDeclaration constructorDeclaration,
                               final String targetField,
                               final MINING_FUNCTION miningFunction,
                               final String modelName,
                               final String segmentationClass) {
        setConstructorSuperNameInvocation(generatedClassName, constructorDeclaration, modelName);
        final BlockStmt body = constructorDeclaration.getBody();
        CommonCodegenUtils.setAssignExpressionValue(body, "targetField", new StringLiteralExpr(targetField));
        CommonCodegenUtils.setAssignExpressionValue(body, "miningFunction", new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
        CommonCodegenUtils.setAssignExpressionValue(body, "pmmlMODEL", new NameExpr(PMML_MODEL.MINING_MODEL.getClass().getName() + "." + PMML_MODEL.MINING_MODEL.name()));
        ClassOrInterfaceType kiePMMLSegmentationClass = parseClassOrInterfaceType(segmentationClass);
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(kiePMMLSegmentationClass);
        CommonCodegenUtils.setAssignExpressionValue(body, "segmentation", objectCreationExpr);
    }

}
