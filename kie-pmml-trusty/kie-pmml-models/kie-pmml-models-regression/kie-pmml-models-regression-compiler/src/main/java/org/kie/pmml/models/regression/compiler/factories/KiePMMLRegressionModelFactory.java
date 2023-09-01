/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.models.regression.compiler.factories;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.regression.compiler.dto.RegressionCompilationDTO;
import org.kie.pmml.models.regression.model.AbstractKiePMMLTable;
import org.kie.pmml.models.regression.model.KiePMMLClassificationTable;
import org.kie.pmml.models.regression.model.KiePMMLRegressionModel;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.GET_MODEL;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.commons.Constants.TO_RETURN;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getMethodDeclarationBlockStmt;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableFactory.GETKIEPMML_TABLE;

public class KiePMMLRegressionModelFactory {

    static final String KIE_PMML_REGRESSION_MODEL_TEMPLATE_JAVA = "KiePMMLRegressionModelTemplate.tmpl";
    static final String KIE_PMML_REGRESSION_MODEL_TEMPLATE = "KiePMMLRegressionModelTemplate";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLRegressionModelFactory.class.getName());

    private KiePMMLRegressionModelFactory() {
    }

    //  KiePMMLRegressionModel instantiation

    public static KiePMMLRegressionModel getKiePMMLRegressionModelClasses(final RegressionCompilationDTO compilationDTO) throws IOException, IllegalAccessException, InstantiationException {
        logger.trace("getKiePMMLRegressionModelClasses {} {}", compilationDTO.getFields(), compilationDTO.getModel());
        Map<String, AbstractKiePMMLTable> regressionTablesMap = getRegressionTables(compilationDTO);
        try {
            AbstractKiePMMLTable nestedTable = regressionTablesMap.size() == 1 ?
                    regressionTablesMap.values().iterator().next() :
                    regressionTablesMap.values().stream().filter(KiePMMLClassificationTable.class::isInstance)
                            .findFirst()
                            .orElseThrow(() -> new KiePMMLException("Failed to find expected " +
                                                                            KiePMMLClassificationTable.class.getSimpleName()));

            return KiePMMLRegressionModel.builder(compilationDTO.getFileName(), compilationDTO.getModelName(), compilationDTO.getMINING_FUNCTION())
                    .withAbstractKiePMMLTable(nestedTable)
                    .withTargetField(compilationDTO.getTargetFieldName())
                    .withMiningFields(compilationDTO.getKieMiningFields())
                    .withOutputFields(compilationDTO.getKieOutputFields())
                    .withKiePMMLMiningFields(compilationDTO.getKiePMMLMiningFields())
                    .withKiePMMLOutputFields(compilationDTO.getKiePMMLOutputFields())
                    .withKiePMMLTargets(compilationDTO.getKiePMMLTargetFields())
                    .withKiePMMLTransformationDictionary(compilationDTO.getKiePMMLTransformationDictionary())
                    .withKiePMMLLocalTransformations(compilationDTO.getKiePMMLLocalTransformations())
                    .build();
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    // Source code generation

    public static Map<String, String> getKiePMMLRegressionModelSourcesMap(final RegressionCompilationDTO compilationDTO) throws IOException {
        logger.trace("getKiePMMLRegressionModelSourcesMap {} {} {}", compilationDTO.getFields(),
                     compilationDTO.getModel(),
                     compilationDTO.getPackageName());
        String className = compilationDTO.getSimpleClassName();
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className,
                                                                                 compilationDTO.getPackageName(),
                                                                                 KIE_PMML_REGRESSION_MODEL_TEMPLATE_JAVA, KIE_PMML_REGRESSION_MODEL_TEMPLATE);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        Map<String, KiePMMLTableSourceCategory> tablesSourceMap = getRegressionTablesMap(compilationDTO);
        String nestedTable = tablesSourceMap.size() == 1 ? tablesSourceMap.keySet().iterator().next() :
                tablesSourceMap
                        .keySet()
                        .stream()
                        .filter(tableName -> tableName.startsWith(compilationDTO.getPackageName() +
                                                                          ".KiePMMLClassificationTable"))
                        .findFirst()
                        .orElseThrow(() -> new KiePMMLException("Failed to find expected " +
                                                                        "KiePMMLClassificationTable"));
        setStaticGetter(compilationDTO,
                        modelTemplate,
                        nestedTable);
        Map<String, String> toReturn = tablesSourceMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getSource()));
        toReturn.put(getFullClassName(cloneCU), cloneCU.toString());
        return toReturn;
    }

    //  not-public KiePMMLRegressionModel instantiation

    static Map<String, AbstractKiePMMLTable> getRegressionTables(final RegressionCompilationDTO compilationDTO) {
        Map<String, AbstractKiePMMLTable> toReturn = new HashMap<>();
        if (compilationDTO.isRegression()) {
            final List<RegressionTable> regressionTables =
                    Collections.singletonList(compilationDTO.getModel().getRegressionTables().get(0));
            final RegressionCompilationDTO regressionCompilationDTO =
                    RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(compilationDTO,
                                                                                                      regressionTables,
                                                                                                      compilationDTO.getModel().getNormalizationMethod());
            toReturn.putAll(KiePMMLRegressionTableFactory.getRegressionTables(regressionCompilationDTO));
        } else {
            final List<RegressionTable> regressionTables = compilationDTO.getModel().getRegressionTables();
            final RegressionCompilationDTO regressionCompilationDTO =
                    RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(compilationDTO,
                                                                                                      regressionTables,
                                                                                                      RegressionModel.NormalizationMethod.NONE);
            KiePMMLClassificationTable kiePMMLClassificationTable =
                    KiePMMLClassificationTableFactory.getClassificationTable(regressionCompilationDTO);
            toReturn.put(kiePMMLClassificationTable.getName(), kiePMMLClassificationTable);
        }
        return toReturn;
    }

    // not-public code-generation

    static Map<String, KiePMMLTableSourceCategory> getRegressionTablesMap(final RegressionCompilationDTO compilationDTO) {
        Map<String, KiePMMLTableSourceCategory> toReturn;
        if (compilationDTO.isRegression()) {
            final List<RegressionTable> regressionTables =
                    Collections.singletonList(compilationDTO.getModel().getRegressionTables().get(0));
            final RegressionCompilationDTO regressionCompilationDTO =
                    RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(compilationDTO,
                                                                                                      regressionTables,
                                                                                                      compilationDTO.getModel().getNormalizationMethod());
            toReturn =
                    KiePMMLRegressionTableFactory.getRegressionTableBuilders(regressionCompilationDTO);
        } else {
            final List<RegressionTable> regressionTables = compilationDTO.getModel().getRegressionTables();
            final RegressionCompilationDTO regressionCompilationDTO =
                    RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(compilationDTO,
                                                                                                      regressionTables,
                                                                                                      RegressionModel.NormalizationMethod.NONE);
            toReturn = KiePMMLClassificationTableFactory.getClassificationTableBuilders(regressionCompilationDTO);
        }
        return toReturn;
    }

    static void setStaticGetter(final CompilationDTO<RegressionModel> compilationDTO,
                                final ClassOrInterfaceDeclaration modelTemplate,
                                final String nestedTable) {

        KiePMMLModelFactoryUtils.initStaticGetter(compilationDTO,
                                                  modelTemplate);
        final BlockStmt body = getMethodDeclarationBlockStmt(modelTemplate, GET_MODEL);
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(body, TO_RETURN).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, TO_RETURN, body)));

        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      TO_RETURN, body)))
                .asMethodCallExpr();

        MethodCallExpr methodCallExpr = new MethodCallExpr();
        methodCallExpr.setScope(new NameExpr(nestedTable));
        methodCallExpr.setName(GETKIEPMML_TABLE);

        getChainedMethodCallExprFrom("withAbstractKiePMMLTable", initializer).setArgument(0, methodCallExpr);
    }
}
