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
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.commons.builders.KiePMMLModelCodegenUtils;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.regression.compiler.dto.RegressionCompilationDTO;
import org.kie.pmml.models.regression.model.KiePMMLRegressionModel;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;

public class KiePMMLRegressionModelFactory {

    static final String KIE_PMML_REGRESSION_MODEL_TEMPLATE_JAVA = "KiePMMLRegressionModelTemplate.tmpl";
    static final String KIE_PMML_REGRESSION_MODEL_TEMPLATE = "KiePMMLRegressionModelTemplate";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLRegressionModelFactory.class.getName());

    private KiePMMLRegressionModelFactory() {
    }

    public static KiePMMLRegressionModel getKiePMMLRegressionModelClasses(final RegressionCompilationDTO compilationDTO) throws IOException, IllegalAccessException, InstantiationException {
        logger.trace("getKiePMMLRegressionModelClasses {} {}", compilationDTO.getFields(), compilationDTO.getModel());
        Map<String, String> sourcesMap =
                getKiePMMLRegressionModelSourcesMap(compilationDTO);
        try {
            Class<?> kiePMMLRegressionModelClass = compilationDTO.compileAndLoadClass(sourcesMap);
            return (KiePMMLRegressionModel) kiePMMLRegressionModelClass.newInstance();
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

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
                                                                          ".KiePMMLRegressionTableClassification"))
                        .findFirst()
                        .orElseThrow(() -> new KiePMMLException("Failed to find expected " +
                                                                        "KiePMMLRegressionTableClassification"));
        setConstructor(compilationDTO,
                       modelTemplate,
                       nestedTable);
        Map<String, String> toReturn = tablesSourceMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getSource()));
        toReturn.put(getFullClassName(cloneCU), cloneCU.toString());
        return toReturn;
    }

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
                    KiePMMLRegressionTableRegressionFactory.getRegressionTables(regressionCompilationDTO);
        } else {
            final List<RegressionTable> regressionTables = compilationDTO.getModel().getRegressionTables();
            final RegressionCompilationDTO regressionCompilationDTO =
                    RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(compilationDTO,
                                                                                                      regressionTables,
                                                                                                      RegressionModel.NormalizationMethod.NONE);
            toReturn = KiePMMLRegressionTableClassificationFactory.getRegressionTables(regressionCompilationDTO);
        }
        return toReturn;
    }

    static void setConstructor(final CompilationDTO<RegressionModel> compilationDTO,
                               final ClassOrInterfaceDeclaration modelTemplate,
                               final String nestedTable) {
        KiePMMLModelCodegenUtils.init(compilationDTO,
                                      modelTemplate);
        final ConstructorDeclaration constructorDeclaration =
                modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())));
        final BlockStmt body = constructorDeclaration.getBody();
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(nestedTable);
        CommonCodegenUtils.setAssignExpressionValue(body, "regressionTable", objectCreationExpr);
    }
}
