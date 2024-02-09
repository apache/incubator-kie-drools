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
package org.kie.pmml.models.tree.compiler.factories;

import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.PMML;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.tree.TreeModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.api.testutils.TestUtils;
import org.kie.pmml.compiler.commons.mocks.PMMLCompilationContextMock;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.tree.compiler.dto.TreeCompilationDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelFactory.KIE_PMML_TREE_MODEL_TEMPLATE;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelFactory.KIE_PMML_TREE_MODEL_TEMPLATE_JAVA;

public class KiePMMLTreeModelFactoryTest {

    private static final String SOURCE_1 = "TreeSample.pmml";
    private static final String SOURCE_2 = "TreeSimplified.pmml";
    private static PMML pmml1;
    private static DataDictionary dataDictionary1;
    private static TransformationDictionary transformationDictionary1;
    private static TreeModel treeModel1;
    private static PMML pmml2;
    private static DataDictionary dataDictionary2;
    private static TransformationDictionary transformationDictionary2;
    private static TreeModel treeModel2;

    @BeforeAll
    public static void setupClass() throws Exception {
        pmml1 = TestUtils.loadFromFile(SOURCE_1);
        dataDictionary1 = pmml1.getDataDictionary();
        transformationDictionary1 = pmml1.getTransformationDictionary();
        treeModel1 = ((TreeModel) pmml1.getModels().get(0));
        pmml2 = TestUtils.loadFromFile(SOURCE_2);
        dataDictionary2 = pmml2.getDataDictionary();
        transformationDictionary2 = pmml2.getTransformationDictionary();
        treeModel2 = ((TreeModel) pmml2.getModels().get(0));
    }

    @Test
    void getKiePMMLTreeModelSourcesMap() {
        CommonCompilationDTO<TreeModel> source = CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                                                        pmml1,
                                                                                                        treeModel1,
                                                                                                        new PMMLCompilationContextMock(),
                                                                                                        SOURCE_1);
        Map<String, String> retrieved =
                KiePMMLTreeModelFactory.getKiePMMLTreeModelSourcesMap(TreeCompilationDTO.fromCompilationDTO(source));
        assertThat(retrieved).isNotNull();
        source = CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                        pmml2,
                                                                        treeModel2,
                                                                        new PMMLCompilationContextMock(),
                                                                        SOURCE_2);
        retrieved =
                KiePMMLTreeModelFactory.getKiePMMLTreeModelSourcesMap(TreeCompilationDTO.fromCompilationDTO(source));
        assertThat(retrieved).isNotNull();
    }

    @Test
    void setConstructor() {
        String className = getSanitizedClassName(treeModel1.getModelName());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, PACKAGE_NAME,
                KIE_PMML_TREE_MODEL_TEMPLATE_JAVA,
                KIE_PMML_TREE_MODEL_TEMPLATE);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        String targetField = "whatIdo";
        String fullNodeClassName = "full.Node.ClassName";
        CommonCompilationDTO<TreeModel> source = CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                                                        pmml1,
                                                                                                        treeModel1,
                                                                                                        new PMMLCompilationContextMock(),
                                                                                                        SOURCE_1);
        KiePMMLTreeModelFactory.setConstructor(TreeCompilationDTO.fromCompilationDTO(source),
                modelTemplate,
                fullNodeClassName);
        ConstructorDeclaration constructorDeclaration = modelTemplate
                .getDefaultConstructor()
                .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR,
                        modelTemplate.getName())))
                .clone();
        BlockStmt body = constructorDeclaration.getBody();
        // targetField
        Optional<AssignExpr> optRetrieved = CommonCodegenUtils.getAssignExpression(body, "targetField");
        assertThat(optRetrieved).isPresent();
        AssignExpr retrieved = optRetrieved.get();
        Expression initializer = retrieved.getValue();
        assertThat(initializer).isInstanceOf(StringLiteralExpr.class);
        String expected = String.format("\"%s\"", targetField);
        assertThat(initializer.toString()).isEqualTo(expected);
        // miningFunction
        optRetrieved = CommonCodegenUtils.getAssignExpression(body, "miningFunction");
        assertThat(optRetrieved).isPresent();
        retrieved = optRetrieved.get();
        initializer = retrieved.getValue();
        assertThat(initializer).isInstanceOf(NameExpr.class);
        MINING_FUNCTION miningFunction = MINING_FUNCTION.byName(treeModel1.getMiningFunction().value());
        expected = miningFunction.getClass().getName() + "." + miningFunction.name();
        assertThat(initializer.toString()).isEqualTo(expected);
        // pmmlMODEL
        optRetrieved = CommonCodegenUtils.getAssignExpression(body, "pmmlMODEL");
        assertThat(optRetrieved).isPresent();
        retrieved = optRetrieved.get();
        initializer = retrieved.getValue();
        assertThat(initializer).isInstanceOf(NameExpr.class);
        expected = PMML_MODEL.TREE_MODEL.getClass().getName() + "." + PMML_MODEL.TREE_MODEL.name();
        assertThat(initializer.toString()).isEqualTo(expected);
        // nodeFunction
        optRetrieved = CommonCodegenUtils.getAssignExpression(body, "nodeFunction");
        assertThat(optRetrieved).isPresent();
        retrieved = optRetrieved.get();
        initializer = retrieved.getValue();
        assertThat(initializer).isInstanceOf(MethodReferenceExpr.class);
        expected = fullNodeClassName;
        assertThat(((MethodReferenceExpr) initializer).getScope().toString()).isEqualTo(expected);
        expected = "evaluateNode";
        assertThat(((MethodReferenceExpr) initializer).getIdentifier()).isEqualTo(expected);
    }
}