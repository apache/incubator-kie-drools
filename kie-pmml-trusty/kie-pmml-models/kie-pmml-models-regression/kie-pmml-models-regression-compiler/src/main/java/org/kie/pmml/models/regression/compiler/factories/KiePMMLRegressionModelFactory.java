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
package org.kie.pmml.models.regression.compiler.factories;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.OpType;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.regression.RegressionModel;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.models.regression.model.KiePMMLRegressionModel;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.commons.factories.KiePMMLOutputFieldFactory.getOutputFields;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.addTransformationsInClassOrInterfaceDeclaration;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFieldName;

public class KiePMMLRegressionModelFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLRegressionModelFactory.class.getName());
    static final String KIE_PMML_REGRESSION_MODEL_TEMPLATE_JAVA = "KiePMMLRegressionModelTemplate.tmpl";
    static final String KIE_PMML_REGRESSION_MODEL_TEMPLATE = "KiePMMLRegressionModelTemplate";

    private KiePMMLRegressionModelFactory() {
    }

    public static KiePMMLRegressionModel getKiePMMLRegressionModelClasses(final DataDictionary dataDictionary,
                                                                          final TransformationDictionary transformationDictionary,
                                                                          final RegressionModel model) throws IOException, IllegalAccessException, InstantiationException {
        logger.trace("getKiePMMLRegressionModelClasses {} {}", dataDictionary, model);
        String className = getSanitizedClassName(model.getModelName());
        String packageName = getSanitizedPackageName(model.getModelName());
        Map<String, String> sourcesMap = getKiePMMLRegressionModelSourcesMap(dataDictionary, transformationDictionary, model, packageName);
        String fullClassName = packageName + "." + className;
        final Map<String, Class<?>> compiledClasses = KieMemoryCompiler.compile(sourcesMap, Thread.currentThread().getContextClassLoader());
        return (KiePMMLRegressionModel) compiledClasses.get(fullClassName).newInstance();
    }

    public static Map<String, String> getKiePMMLRegressionModelSourcesMap(final DataDictionary dataDictionary,
                                                                          final TransformationDictionary transformationDictionary,
                                                                          final RegressionModel model,
                                                                          final String packageName) throws IOException {
        logger.trace("getKiePMMLRegressionModelSourcesMap {} {} {}", dataDictionary, model, packageName);
        String className = getSanitizedClassName(model.getModelName());
        String modelName = model.getModelName();
        String targetFieldName = getTargetFieldName(dataDictionary, model).orElse(null);
        List<KiePMMLOutputField> outputFields = getOutputFields(model);
        Map<String, KiePMMLTableSourceCategory> tablesSourceMap = getRegressionTablesMap(dataDictionary, model, targetFieldName, outputFields, packageName);
        CompilationUnit templateCU = getFromFileName(KIE_PMML_REGRESSION_MODEL_TEMPLATE_JAVA);
        CompilationUnit cloneCU = templateCU.clone();
        cloneCU.setPackageDeclaration(packageName);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(KIE_PMML_REGRESSION_MODEL_TEMPLATE)
                .orElseThrow(() -> new RuntimeException(MAIN_CLASS_NOT_FOUND));
        modelTemplate.setName(className);
        String nestedTable = tablesSourceMap.size() == 1 ? tablesSourceMap.keySet().iterator().next() :
                tablesSourceMap.keySet().stream().filter(tableName -> tableName.startsWith("KiePMMLRegressionTableClassification"))
                        .findFirst().orElseThrow(() -> new RuntimeException("Failed to find expected KiePMMLRegressionTableClassification"));
        final ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format("Missing default constructor in ClassOrInterfaceDeclaration %s ", modelTemplate.getName())));
        populateConstructor(className, nestedTable, constructorDeclaration, targetFieldName, MINING_FUNCTION.byName(model.getMiningFunction().value()), modelName);
        addTransformationsInClassOrInterfaceDeclaration(modelTemplate, transformationDictionary, model.getLocalTransformations());
        Map<String, String> toReturn = tablesSourceMap.entrySet().stream().collect(Collectors.toMap(entry -> packageName + "." + entry.getKey(), entry -> entry.getValue().getSource()));
        String fullClassName = packageName + "." + className;
        toReturn.put(fullClassName, cloneCU.toString());
        return toReturn;
    }

    static Map<String, KiePMMLTableSourceCategory> getRegressionTablesMap(final DataDictionary dataDictionary,
                                                                                  final RegressionModel model,
                                                                                  final String targetFieldName,
                                                                                  final List<KiePMMLOutputField> outputFields,
                                                                                  final String packageName) throws IOException {
        final DataField targetDataField = dataDictionary.getDataFields().stream()
                .filter(field -> Objects.equals(targetFieldName, field.getName().getValue()))
                .findFirst().orElse(null);
        final OpType opType = targetDataField != null ? targetDataField.getOpType() : null;
        Map<String, KiePMMLTableSourceCategory> toReturn;
        if (isRegression(model.getMiningFunction(), targetFieldName, opType)) {
            toReturn = KiePMMLRegressionTableRegressionFactory.getRegressionTables(Collections.singletonList(model.getRegressionTables().get(0)), model.getNormalizationMethod(), targetFieldName, packageName);
        } else {
            toReturn = KiePMMLRegressionTableClassificationFactory.getRegressionTables(model.getRegressionTables(), model.getNormalizationMethod(), opType, outputFields, targetFieldName, packageName);
        }
        return toReturn;
    }

    static void populateConstructor(final String generatedClassName,
                                            final String nestedTable,
                                            final ConstructorDeclaration constructorDeclaration,
                                            final String targetField,
                                            final MINING_FUNCTION miningFunction,
                                            final String modelName) {
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(nestedTable);
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
            if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("regressionTable")) {
                assignExpr.setValue(objectCreationExpr);
            } else if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("targetField")) {
                assignExpr.setValue(new StringLiteralExpr(targetField));
            } else if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("miningFunction")) {
                assignExpr.setValue(new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
            } else if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("pmmlMODEL")) {
                assignExpr.setValue(new NameExpr(PMML_MODEL.REGRESSION_MODEL.getClass().getName() + "." + PMML_MODEL.REGRESSION_MODEL.name()));
            }
        });
    }

    static boolean isRegression(final MiningFunction miningFunction, final String targetField, final OpType targetOpType) {
        return Objects.equals(MiningFunction.REGRESSION, miningFunction) && (targetField == null || Objects.equals(OpType.CONTINUOUS, targetOpType));
    }
}
