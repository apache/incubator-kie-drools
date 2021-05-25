/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.tree.model.KiePMMLTreeModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelFactory.KIE_PMML_TREE_MODEL_TEMPLATE;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelFactory.KIE_PMML_TREE_MODEL_TEMPLATE_JAVA;

public class KiePMMLTreeModelFactoryTest {

    private static final String SOURCE_1 = "TreeSample.pmml";
    private static final String SOURCE_2 = "TreeSimplified.pmml";
    private static final String PACKAGE_NAME = "packagename";
    private static PMML pmml1;
    private static DataDictionary dataDictionary1;
    private static TransformationDictionary transformationDictionary1;
    private static TreeModel treeModel1;
    private static PMML pmml2;
    private static DataDictionary dataDictionary2;
    private static TransformationDictionary transformationDictionary2;
    private static TreeModel treeModel2;

    @BeforeClass
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
    public void getKiePMMLTreeModel() {
        KiePMMLTreeModel retrieved = KiePMMLTreeModelFactory.getKiePMMLTreeModel(dataDictionary1,
                                                                                 transformationDictionary1,
                                                                                 treeModel1,
                                                                                 PACKAGE_NAME,
                                                                                 new HasClassLoaderMock());
        assertNotNull(retrieved);
        retrieved = KiePMMLTreeModelFactory.getKiePMMLTreeModel(dataDictionary2,
                                                                transformationDictionary2,
                                                                treeModel2,
                                                                PACKAGE_NAME,
                                                                new HasClassLoaderMock());
        assertNotNull(retrieved);
    }

    @Test
    public void getKiePMMLTreeModelSourcesMap() {
        Map<String, String> retrieved = KiePMMLTreeModelFactory.getKiePMMLTreeModelSourcesMap(dataDictionary1,
                                                                                              transformationDictionary1,
                                                                                              treeModel1,
                                                                                              PACKAGE_NAME);
        assertNotNull(retrieved);
        retrieved = KiePMMLTreeModelFactory.getKiePMMLTreeModelSourcesMap(dataDictionary2,
                                                                          transformationDictionary2,
                                                                          treeModel2,
                                                                          PACKAGE_NAME);
        assertNotNull(retrieved);
    }

    @Test
    public void setConstructor() {
        String className = getSanitizedClassName(treeModel1.getModelName());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, PACKAGE_NAME, KIE_PMML_TREE_MODEL_TEMPLATE_JAVA, KIE_PMML_TREE_MODEL_TEMPLATE);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        String targetField = "whatIdo";
        String fullNodeClassName = "full.Node.ClassName";
        KiePMMLTreeModelFactory.setConstructor(treeModel1,
                                               dataDictionary1,
                                               transformationDictionary1,
                                               modelTemplate,
                                               fullNodeClassName);
        ConstructorDeclaration constructorDeclaration = modelTemplate
                .getDefaultConstructor()
                .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())))
                .clone();
        BlockStmt body = constructorDeclaration.getBody();
        // targetField
        Optional<AssignExpr> optRetrieved = CommonCodegenUtils.getAssignExpression(body, "targetField");
        assertTrue(optRetrieved.isPresent());
        AssignExpr retrieved = optRetrieved.get();
        Expression initializer = retrieved.getValue();
        assertTrue(initializer instanceof StringLiteralExpr);
        String expected = String.format("\"%s\"", targetField);
        assertEquals(expected, initializer.toString());
        // miningFunction
        optRetrieved = CommonCodegenUtils.getAssignExpression(body, "miningFunction");
        assertTrue(optRetrieved.isPresent());
        retrieved = optRetrieved.get();
        initializer = retrieved.getValue();
        assertTrue(initializer instanceof NameExpr);
        MINING_FUNCTION miningFunction = MINING_FUNCTION.byName(treeModel1.getMiningFunction().value());
        expected = miningFunction.getClass().getName() + "." + miningFunction.name();
        assertEquals(expected, initializer.toString());
        // pmmlMODEL
        optRetrieved = CommonCodegenUtils.getAssignExpression(body, "pmmlMODEL");
        assertTrue(optRetrieved.isPresent());
        retrieved = optRetrieved.get();
        initializer = retrieved.getValue();
        assertTrue(initializer instanceof NameExpr);
        expected = PMML_MODEL.TREE_MODEL.getClass().getName() + "." + PMML_MODEL.TREE_MODEL.name();
        assertEquals(expected, initializer.toString());
        // nodeFunction
        optRetrieved = CommonCodegenUtils.getAssignExpression(body, "nodeFunction");
        assertTrue(optRetrieved.isPresent());
        retrieved = optRetrieved.get();
        initializer = retrieved.getValue();
        assertTrue(initializer instanceof MethodReferenceExpr);
        expected = fullNodeClassName;
        assertEquals(expected, ((MethodReferenceExpr)initializer).getScope().toString());
        expected = "evaluateNode";
        assertEquals(expected, ((MethodReferenceExpr)initializer).getIdentifier());
    }
}