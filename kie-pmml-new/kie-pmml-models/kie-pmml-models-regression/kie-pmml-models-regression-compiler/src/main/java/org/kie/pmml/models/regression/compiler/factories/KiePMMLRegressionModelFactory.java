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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.OpType;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.regression.RegressionModel;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.commons.model.enums.RESULT_FEATURE;
import org.kie.pmml.models.regression.compiler.utils.KiePMMLRegressionCompiler;
import org.kie.pmml.models.regression.model.KiePMMLRegressionModel;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionFactory.getKiePMMLExtensions;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetField;

public class KiePMMLRegressionModelFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLRegressionModelFactory.class.getName());
    private static final String KIE_PMML_REGRESSION_MODEL_TEMPLATE_JAVA = "KiePMMLRegressionModelTemplate.tmpl";
    private static final String KIE_PMML_REGRESSION_MODEL_TEMPLATE = "KiePMMLRegressionModelTemplate";
    private static final String MAIN_CLASS_NOT_FOUND = "Main class not found";
    private static final String BASE_PACKAGE = "org.kie.pmml.models.regression.evaluator.";

    private static AtomicInteger classArity = new AtomicInteger(0);

    private KiePMMLRegressionModelFactory() {
    }

    public static KiePMMLRegressionModel getKiePMMLRegressionModel(DataDictionary dataDictionary, RegressionModel model) throws IOException, IllegalAccessException, InstantiationException {
        logger.debug("getKiePMMLRegressionModel {}", model);
        String name = model.getModelName();
        String targetFieldName = getTargetField(dataDictionary, model).orElse(null);
        List<KiePMMLOutputField> outputFields = getOutputFields(model);
        Map<String, KiePMMLTableSourceCategory> tablesSourceMap = getRegressionTablesMap(dataDictionary, model, targetFieldName, outputFields);
        CompilationUnit templateCU = StaticJavaParser.parseResource(KIE_PMML_REGRESSION_MODEL_TEMPLATE_JAVA);
        CompilationUnit cloneCU = templateCU.clone();
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(KIE_PMML_REGRESSION_MODEL_TEMPLATE)
                .orElseThrow(() -> new RuntimeException(MAIN_CLASS_NOT_FOUND));
        String className = "KiePMMLRegressionModel" + classArity.addAndGet(1);
        modelTemplate.setName(className);
        setModelName(name, modelTemplate);
        String nestedTable = tablesSourceMap.size() == 1 ? tablesSourceMap.keySet().iterator().next() :
                tablesSourceMap.keySet().stream().filter(tableName -> tableName.startsWith("KiePMMLRegressionTableClassification"))
                        .findFirst().orElseThrow(() -> new RuntimeException("Failed to find expected KiePMMLRegressionTableClassification"));
        setConstructor(className, nestedTable, modelTemplate, targetFieldName, MINING_FUNCTION.byName(model.getMiningFunction().value()), PMML_MODEL.REGRESSION_MODEL);
        Map<String, String> sourcesMap = tablesSourceMap.entrySet().stream().collect(Collectors.toMap(entry -> BASE_PACKAGE + entry.getKey(), entry -> entry.getValue().getSource()));
        String fullClassName = BASE_PACKAGE + className;
        sourcesMap.put(fullClassName, cloneCU.toString());
        final Map<String, Class<?>> compiledClasses = KiePMMLRegressionCompiler.compile(sourcesMap, Thread.currentThread().getContextClassLoader());
        return (KiePMMLRegressionModel) compiledClasses.get(fullClassName).newInstance();
    }

    private static List<KiePMMLOutputField> getOutputFields(RegressionModel model) {
        List<KiePMMLOutputField> outputFields = new ArrayList<>();
        if (model.getOutput() != null) {
            outputFields.addAll(model.getOutput().getOutputFields().stream().map(KiePMMLRegressionModelFactory::getKiePMMLOutputField).collect(Collectors.toList()));
        }
        return outputFields;
    }

    private static Map<String, KiePMMLTableSourceCategory> getRegressionTablesMap(DataDictionary dataDictionary, RegressionModel model, String targetFieldName, List<KiePMMLOutputField> outputFields) throws IOException {
        final DataField targetDataField = dataDictionary.getDataFields().stream()
                .filter(field -> Objects.equals(targetFieldName, field.getName().getValue()))
                .findFirst().orElse(null);
        final OpType opType = targetDataField != null ? targetDataField.getOpType() : null;
        Map<String, KiePMMLTableSourceCategory> toReturn;
        if (isRegression(model.getMiningFunction(), targetFieldName, opType)) {
            toReturn = KiePMMLRegressionTableRegressionFactory.getRegressionTables(Collections.singletonList(model.getRegressionTables().get(0)), model.getNormalizationMethod(), targetFieldName);
        } else {
            toReturn = KiePMMLRegressionTableClassificationFactory.getRegressionTables(model.getRegressionTables(), model.getNormalizationMethod(), opType, outputFields, targetFieldName);
        }
        return toReturn;
    }

    private static void setModelName(String modelName, ClassOrInterfaceDeclaration modelTemplate) {
        modelTemplate.getFieldByName("MODEL_NAME").ifPresent(fieldDeclaration -> fieldDeclaration.getVariable(0).setInitializer(new StringLiteralExpr(modelName)));
    }

    private static void setConstructor(String generatedClassName, String nestedTable, ClassOrInterfaceDeclaration modelTemplate, String targetField, MINING_FUNCTION miningFunction, PMML_MODEL pmmlMODEL) {
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(nestedTable);
        modelTemplate.getDefaultConstructor().ifPresent(constructor -> {
            constructor.setName(generatedClassName);
            final BlockStmt body = constructor.getBody();
            final List<AssignExpr> assignExprs = body.findAll(AssignExpr.class);
            assignExprs.forEach(assignExpr -> {
                if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("regressionTable")) {
                    assignExpr.setValue(objectCreationExpr);
                } else if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("targetField")) {
                    assignExpr.setValue(new StringLiteralExpr(targetField));
                } else if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("miningFunction")) {
                    assignExpr.setValue(new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
                } else if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("pmmlMODEL")) {
                    assignExpr.setValue(new NameExpr(pmmlMODEL.getClass().getName() + "." + pmmlMODEL.name()));
                }
            });
        });
    }

    private static KiePMMLOutputField getKiePMMLOutputField(OutputField outputField) {
        return KiePMMLOutputField.builder(outputField.getName().getValue(), getKiePMMLExtensions(outputField.getExtensions()))
                .withResultFeature(RESULT_FEATURE.byName(outputField.getResultFeature().value()))
                .withTargetField(outputField.getTargetField() != null ? outputField.getTargetField().getValue() : null)
                .withValue(outputField.getValue())
                .build();
    }

    private static boolean isRegression(MiningFunction miningFunction, String targetField, OpType targetOpType) {
        return Objects.equals(MiningFunction.REGRESSION, miningFunction) && (targetField == null || Objects.equals(OpType.CONTINUOUS, targetOpType));
    }
}
