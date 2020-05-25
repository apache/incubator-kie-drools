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
package org.kie.pmml.models.drools.scorecard.compiler.factories;

import java.io.IOException;
import java.util.HashMap;
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
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.scorecard.Scorecard;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.commons.model.enums.RESULT_FEATURE;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.scorecard.model.KiePMMLScorecardModel;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.commons.factories.KiePMMLOutputFieldFactory.getOutputFields;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFieldName;

/**
 * Class used to generate <code>KiePMMLScorecard</code> out of a <code>DataDictionary</code> and a <code>ScorecardModel</code>
 */
public class KiePMMLScorecardModelFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLScorecardModelFactory.class.getName());

    private static final String KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA = "KiePMMLScorecardModelTemplate.tmpl";
    private static final String KIE_PMML_SCORECARD_MODEL_TEMPLATE = "KiePMMLScorecardModelTemplate";

    private KiePMMLScorecardModelFactory() {
        // Avoid instantiation
    }

    public static KiePMMLScorecardModel getKiePMMLScorecardModel(DataDictionary dataDictionary, Scorecard model, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) throws IOException, IllegalAccessException, InstantiationException {
        logger.trace("getKiePMMLScorecardModel {}", model);
        String className = getSanitizedClassName(model.getModelName());
        String packageName = getSanitizedPackageName(className);
        Map<String, String> sourcesMap = getKiePMMLScorecardModelSourcesMap(dataDictionary, model, fieldTypeMap, packageName);
        String fullClassName = packageName + "." + className;
        final Map<String, Class<?>> compiledClasses = KieMemoryCompiler.compile(sourcesMap, Thread.currentThread().getContextClassLoader());
        return (KiePMMLScorecardModel) compiledClasses.get(fullClassName).newInstance();
    }

    public static Map<String, String> getKiePMMLScorecardModelSourcesMap(final DataDictionary dataDictionary, final Scorecard model, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final String packageName) throws IOException {
        logger.trace("getKiePMMLScorecardModelSourcesMap {} {} {}", dataDictionary, model, packageName);
        String className = getSanitizedClassName(model.getModelName());
        String targetField = getTargetFieldName(dataDictionary, model).orElse(null);
        List<KiePMMLOutputField> outputFields = getOutputFields(model);
        CompilationUnit templateCU = getFromFileName(KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA);
        CompilationUnit cloneCU = templateCU.clone();
        cloneCU.setPackageDeclaration(packageName);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(KIE_PMML_SCORECARD_MODEL_TEMPLATE)
                .orElseThrow(() -> new RuntimeException(MAIN_CLASS_NOT_FOUND));
        modelTemplate.setName(className);
        MINING_FUNCTION miningFunction = MINING_FUNCTION.byName(model.getMiningFunction().value());
        final ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format("Missing default constructor in ClassOrInterfaceDeclaration %s ", modelTemplate.getName())));
        setConstructor(model, constructorDeclaration, modelTemplate.getName(), targetField, miningFunction);
        addOutputFieldsPopulation(constructorDeclaration.getBody(), outputFields);
        addFieldTypeMapPopulation(constructorDeclaration.getBody(), fieldTypeMap);
        Map<String, String> toReturn = new HashMap<>();
        String fullClassName = packageName + "." + className;
        toReturn.put(fullClassName, cloneCU.toString());
        return toReturn;
    }

    /**
     * This method returns a <code>KiePMMLDroolsAST</code> out of the given <code>DataDictionary</code> and <code>Scorecard</code>.
     * <b>It also populate the given <code>Map</code> that has to be used for final <code>KiePMMLScorecardModel</code></b>
     * @param dataDictionary
     * @param model
     * @param fieldTypeMap
     * @return
     */
    public static KiePMMLDroolsAST getKiePMMLDroolsAST(DataDictionary dataDictionary, Scorecard model, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        logger.trace("getKiePMMLDroolsAST {}", model);
        return KiePMMLScorecardModelASTFactory.getKiePMMLDroolsAST(dataDictionary, model, fieldTypeMap);
    }

    private static void addFieldTypeMapPopulation(BlockStmt body, Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
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

    private static void addOutputFieldsPopulation(final BlockStmt body, final List<KiePMMLOutputField> outputFields) {
        for (KiePMMLOutputField outputField : outputFields) {
            NodeList<Expression> expressions = NodeList.nodeList(new StringLiteralExpr(outputField.getName()), new NameExpr("Collections.emptyList()"));
            MethodCallExpr builder = new MethodCallExpr(new NameExpr("KiePMMLOutputField"), "builder", expressions);
            if (outputField.getRank() != null) {
                expressions = NodeList.nodeList(new IntegerLiteralExpr(outputField.getRank()));
                builder = new MethodCallExpr(builder, "withRank", expressions);
            }
            if (outputField.getValue() != null) {
                expressions = NodeList.nodeList(new NameExpr(outputField.getValue().toString()));
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

    private static void setConstructor(final Scorecard scorecard, final ConstructorDeclaration constructorDeclaration, final SimpleName tableName, final String targetField, MINING_FUNCTION miningFunction) {
        constructorDeclaration.setName(tableName);
        final BlockStmt body = constructorDeclaration.getBody();
        body.getStatements().iterator().forEachRemaining(statement -> {
            if (statement instanceof ExplicitConstructorInvocationStmt) {
                ExplicitConstructorInvocationStmt superStatement = (ExplicitConstructorInvocationStmt) statement;
                NameExpr modelNameExpr = (NameExpr) superStatement.getArgument(0);
                modelNameExpr.setName(String.format("\"%s\"", scorecard.getModelName()));
            }
        });
        final List<AssignExpr> assignExprs = body.findAll(AssignExpr.class);
        assignExprs.forEach(assignExpr -> {
            if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("targetField")) {
                assignExpr.setValue(new StringLiteralExpr(targetField));
            } else if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("miningFunction")) {
                assignExpr.setValue(new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
            } else if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("pmmlMODEL")) {
                assignExpr.setValue(new NameExpr(PMML_MODEL.SCORECARD_MODEL.getClass().getName() + "." + PMML_MODEL.SCORECARD_MODEL.name()));
            }
        });
    }
}
