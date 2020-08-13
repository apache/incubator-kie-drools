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
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.mining.MiningModel;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.commons.model.enums.RESULT_FEATURE;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionFactory.getKiePMMLExtensions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLOutputFieldFactory.getOutputFields;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;
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
        final String segmentationName = String.format(SEGMENTATIONNAME_TEMPLATE, model.getModelName());
        final Map<String, String> toReturn = getSegmentationSourcesMap(parentPackageName,
                                                                       dataDictionary,
                                                                       transformationDictionary,
                                                                       model.getSegmentation(),
                                                                       segmentationName,
                                                                       kBuilder);
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
        final ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format("Missing default constructor in ClassOrInterfaceDeclaration %s ", modelTemplate.getName())));
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
            final String assignExprName = assignExpr.getTarget().asNameExpr().getNameAsString();
            switch (assignExprName) {
                case "targetField":
                    assignExpr.setValue(new StringLiteralExpr(targetField));
                    break;
                case "miningFunction":
                    assignExpr.setValue(new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
                break;
                case "pmmlMODEL":
                    assignExpr.setValue(new NameExpr(PMML_MODEL.MINING_MODEL.getClass().getName() + "." + PMML_MODEL.MINING_MODEL.name()));
                    break;
                case "segmentation":
                    ClassOrInterfaceType kiePMMLSegmentationClass = parseClassOrInterfaceType(segmentationClass);
                    ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
                    objectCreationExpr.setType(kiePMMLSegmentationClass);
                    assignExpr.setValue(objectCreationExpr);
                    break;
                default:
                    // NOOP
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
            String targetField = outputField.getTargetField().orElse(null);
            if (targetField != null) {
                expressions = NodeList.nodeList(new StringLiteralExpr(targetField));
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
