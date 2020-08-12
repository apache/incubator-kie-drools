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
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.mining.MiningModel;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.commons.model.enums.RESULT_FEATURE;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionFactory.getKiePMMLExtensions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLOutputFieldFactory.getOutputFields;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.addTransformationsInClassOrInterfaceDeclaration;
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
                                                                      final KnowledgeBuilder kBuilder) {
        logger.trace("getKiePMMLMiningModelSourcesMap {} {} {}", dataDictionary, model, parentPackageName);
        String className = getSanitizedClassName(model.getModelName());
        String modelName = model.getModelName();
        String targetFieldName = getTargetFieldName(dataDictionary, model).orElse(null);
        List<KiePMMLOutputField> outputFields = getOutputFields(model);
        CompilationUnit templateCU = getFromFileName(KIE_PMML_MINING_MODEL_TEMPLATE_JAVA);
        CompilationUnit cloneCU = templateCU.clone();
        cloneCU.setPackageDeclaration(parentPackageName);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(KIE_PMML_MINING_MODEL_TEMPLATE)
                .orElseThrow(() -> new RuntimeException(MAIN_CLASS_NOT_FOUND));
        modelTemplate.setName(className);
        final ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format("Missing default constructor in ClassOrInterfaceDeclaration %s ", modelTemplate.getName())));
        setConstructor(className, constructorDeclaration, targetFieldName, MINING_FUNCTION.byName(model.getMiningFunction().value()), modelName);
        addOutputFieldsPopulation(constructorDeclaration.getBody(), outputFields);
        addTransformationsInClassOrInterfaceDeclaration(modelTemplate, transformationDictionary, model.getLocalTransformations());



        Map<String, String> toReturn = getSegmentationSourcesMap(parentPackageName,
                                                                dataDictionary,
                                                                transformationDictionary,
                                                                model.getSegmentation(),
                                                                String.format(SEGMENTATIONNAME_TEMPLATE, model.getModelName()),
                                                                kBuilder);
        String fullClassName = parentPackageName + "." + className;
        toReturn.put(fullClassName, cloneCU.toString());
        return toReturn;
    }

    static void setConstructor(final String generatedClassName,
                               final ConstructorDeclaration constructorDeclaration,
                               final String targetField,
                               final MINING_FUNCTION miningFunction,
                               final String modelName) {
        constructorDeclaration.setName(generatedClassName);
        final BlockStmt body = constructorDeclaration.getBody();
        body.getStatements().iterator().forEachRemaining(statement -> {
            if (statement instanceof ExplicitConstructorInvocationStmt) {
                ExplicitConstructorInvocationStmt superStatement = (ExplicitConstructorInvocationStmt) statement;
                NameExpr modelNameExpr = (NameExpr) superStatement.getArgument(0);
                modelNameExpr.setName(String.format("\"%s\"", modelName));
            }
        });
        final List<AssignExpr> assignExprs = body.findAll(AssignExpr.class);
        assignExprs.forEach(assignExpr -> {
            if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("targetField")) {
                assignExpr.setValue(new StringLiteralExpr(targetField));
            } else if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("miningFunction")) {
                assignExpr.setValue(new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
            } else if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("pmmlMODEL")) {
                assignExpr.setValue(new NameExpr(PMML_MODEL.REGRESSION_MODEL.getClass().getName() + "." + PMML_MODEL.REGRESSION_MODEL.name()));
            }
        });
    }

    /**
     * Populate the <b>outputFields</b> <code>List&lt;KiePMMLOutputField&gt;</code>
     * @param body
     * @param outputFields
     */
    static void addOutputFieldsPopulation(final BlockStmt body, final List<KiePMMLOutputField> outputFields) {
        for (KiePMMLOutputField outputField : outputFields) {
            NodeList<Expression> expressions = NodeList.nodeList(new StringLiteralExpr(outputField.getName()), new NameExpr("Collections.emptyList()"));
            MethodCallExpr builder = new MethodCallExpr(new NameExpr("KiePMMLOutputField"), "builder", expressions);
            if (outputField.getRank() != null) {
                expressions = NodeList.nodeList(new IntegerLiteralExpr(outputField.getRank()));
                builder = new MethodCallExpr(builder, "withRank", expressions);
            }
            if (outputField.getValue() != null) {
                expressions = NodeList.nodeList(new StringLiteralExpr(outputField.getValue().toString()));
                builder = new MethodCallExpr(builder, "withValue", expressions);
            }
            if (outputField.getTargetField().isPresent()) {
                expressions = NodeList.nodeList(new StringLiteralExpr(outputField.getTargetField().get()));
                builder = new MethodCallExpr(builder, "withTargetField", expressions);
            }
            if (outputField.getResultFeature() != null) {
                expressions = NodeList.nodeList(new NameExpr(RESULT_FEATURE.class.getName() + "." + outputField.getResultFeature().toString()));
                builder = new MethodCallExpr(builder, "withResultFeature", expressions);
            }
            Expression newOutputField = new MethodCallExpr(builder, "build");
            expressions = NodeList.nodeList(newOutputField);
            body.addStatement(new MethodCallExpr(new NameExpr("outputFields"), "add", expressions));
        }
    }
}
