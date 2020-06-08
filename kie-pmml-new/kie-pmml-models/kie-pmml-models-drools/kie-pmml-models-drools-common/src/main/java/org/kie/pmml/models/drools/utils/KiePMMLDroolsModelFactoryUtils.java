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
package org.kie.pmml.models.drools.utils;

import java.util.List;
import java.util.Map;

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
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.Model;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.commons.model.enums.RESULT_FEATURE;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.factories.KiePMMLOutputFieldFactory.getOutputFields;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFieldName;

/**
 * Utility class to provide common methods for KiePMMLDroolsModel-specific <b>factories</b>
 */
public class KiePMMLDroolsModelFactoryUtils {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLDroolsModelFactoryUtils.class.getName());

    private KiePMMLDroolsModelFactoryUtils() {
        // Avoid instantiation
    }

    /**
     * @param dataDictionary
     * @param model
     * @param fieldTypeMap
     * @param packageName
     * @param javaTemplate the name of the <b>file</b> to be used as template source
     * @param modelClassName the name of the class used in the provided template
     * @return
     */
    public static CompilationUnit getKiePMMLModelCompilationUnit(final DataDictionary dataDictionary,
                                                                 final Model model,
                                                                 final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                                 final String packageName,
                                                                 final String javaTemplate,
                                                                 final String modelClassName) {
        logger.trace("getKiePMMLModelCompilationUnit {} {} {}", dataDictionary, model, packageName);
        String className = getSanitizedClassName(model.getModelName());
        String targetField = getTargetFieldName(dataDictionary, model).orElse(null);
        List<KiePMMLOutputField> outputFields = getOutputFields(model);
        CompilationUnit templateCU = getFromFileName(javaTemplate);
        CompilationUnit cloneCU = templateCU.clone();
        cloneCU.setPackageDeclaration(packageName);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(modelClassName)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + modelClassName));
        modelTemplate.setName(className);
        MINING_FUNCTION miningFunction = MINING_FUNCTION.byName(model.getMiningFunction().value());
        final ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format("Missing default constructor in ClassOrInterfaceDeclaration %s ", modelTemplate.getName())));
        setConstructor(model, constructorDeclaration, modelTemplate.getName(), targetField, miningFunction);
        addOutputFieldsPopulation(constructorDeclaration.getBody(), outputFields);
        addFieldTypeMapPopulation(constructorDeclaration.getBody(), fieldTypeMap);
        return cloneCU;
    }

    /**
     * Define the <b>targetField</b>, the <b>miningFunction</b> and the <b>pmmlMODEL</b> inside the constructor
     * @param model
     * @param constructorDeclaration
     * @param tableName
     * @param targetField
     * @param miningFunction
     */
    static void setConstructor(final Model model, final ConstructorDeclaration constructorDeclaration, final SimpleName tableName, final String targetField, MINING_FUNCTION miningFunction) {
        constructorDeclaration.setName(tableName);
        final BlockStmt body = constructorDeclaration.getBody();
        final List<AssignExpr> assignExprs = body.findAll(AssignExpr.class);
        assignExprs.forEach(assignExpr -> {
            if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("targetField")) {
                assignExpr.setValue(new StringLiteralExpr(targetField));
            } else if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("miningFunction")) {
                assignExpr.setValue(new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
            } else if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("pmmlMODEL")) {
                PMML_MODEL pmmlModel = PMML_MODEL.byName(model.getClass().getSimpleName());
                assignExpr.setValue(new NameExpr(pmmlModel.getClass().getName() + "." + pmmlModel.name()));
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

    /**
     * Populate the <b>fieldTypeMap</b> <code>Map&lt;String, KiePMMLOriginalTypeGeneratedType&gt;</code>
     * @param body
     * @param fieldTypeMap
     */
    static void addFieldTypeMapPopulation(BlockStmt body, Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        for (Map.Entry<String, KiePMMLOriginalTypeGeneratedType> entry : fieldTypeMap.entrySet()) {
            KiePMMLOriginalTypeGeneratedType kiePMMLOriginalTypeGeneratedType = entry.getValue();
            NodeList<Expression> expressions = NodeList.nodeList(new StringLiteralExpr(kiePMMLOriginalTypeGeneratedType.getOriginalType()), new StringLiteralExpr(kiePMMLOriginalTypeGeneratedType.getGeneratedType()));
            ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
            objectCreationExpr.setType(KiePMMLOriginalTypeGeneratedType.class.getName());
            objectCreationExpr.setArguments(expressions);
            expressions = NodeList.nodeList(new StringLiteralExpr(entry.getKey()), objectCreationExpr);
            body.addStatement(new MethodCallExpr(new NameExpr("fieldTypeMap"), "put", expressions));
        }
    }
}
