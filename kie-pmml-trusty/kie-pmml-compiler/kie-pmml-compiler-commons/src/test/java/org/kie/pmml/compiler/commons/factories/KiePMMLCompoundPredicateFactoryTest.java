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

package org.kie.pmml.compiler.commons.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.PMML;
import org.dmg.pmml.tree.TreeModel;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;
import org.kie.pmml.compiler.commons.utils.ModelUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.factories.KiePMMLCompoundPredicateFactory.getCompoundPredicateBody;
import static org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactoryTest.getNodeById;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

public class KiePMMLCompoundPredicateFactoryTest {

    private static final String SOURCE = "TreeSample.pmml";
    private static PMML pmmlModel;
    private static TreeModel model;
    private static DataDictionary pmmlDataDictionary;
    private static List<DerivedField> derivedFields;


    @Before
    public void setUp() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(SOURCE), "");
        assertNotNull(pmmlModel);
        pmmlDataDictionary = pmmlModel.getDataDictionary();
        assertNotNull(pmmlDataDictionary);
        model = (TreeModel) pmmlModel.getModels().get(0);
        assertNotNull(model);
        derivedFields = ModelUtils.getDerivedFields(pmmlModel.getTransformationDictionary(),
                                                    model.getLocalTransformations());
    }

    @Test
    public void getCompoundSimplePredicateRefPredicatesBody() {
        CompoundPredicate predicate = (CompoundPredicate) getNodeById(model, "A_A_A").getPredicate();
        final List<MethodDeclaration> compoundPredicateMethods = new ArrayList<>();
        final String rootNodeClassName = "rootNodeClassName";
        final String nodeClassName = "nodeClassName";
        final AtomicInteger counter = new AtomicInteger();
        BlockStmt retrieved = getCompoundPredicateBody(predicate,
                                                       pmmlDataDictionary,
                                                       derivedFields,
                                                       compoundPredicateMethods,
                                                       rootNodeClassName,
                                                       nodeClassName,
                                                       counter);
        assertNotNull(retrieved);
        BlockStmt expected = JavaParserUtils.parseBlock("{\n" +
                                                                "    Boolean toReturn = null;\n" +
                                                                "    final List<Function<Map<String, Object>, Boolean>> functions = java.util.Arrays.asList" +
                                                                "(rootNodeClassName::evaluateNestedPredicatenodeClassName1, " +
                                                                "rootNodeClassName::evaluateNestedPredicatenodeClassName2);\n" +
                                                                "    for (Function<Map<String, Object>, Boolean> function : functions) {\n" +
                                                                "        Boolean evaluation = function.apply(stringObjectMap);\n" +
                                                                "        // generated\n" +
                                                                "        toReturn = toReturn != null ? toReturn && evaluation : evaluation;\n" +
                                                                "    }\n" +
                                                                "    return toReturn != null && toReturn;\n" +
                                                                "}");
        JavaParserUtils.equalsNode(expected, retrieved);
        commonValidateBlockStmt(retrieved, compoundPredicateMethods, rootNodeClassName, predicate.getPredicates().size());
    }

    @Test
    public void getCompoundSimplePredicateNoRefPredicatesBody() {
        CompoundPredicate predicate = (CompoundPredicate) getNodeById(model, "A_B").getPredicate();
        final List<MethodDeclaration> compoundPredicateMethods = new ArrayList<>();
        final String rootNodeClassName = "rootNodeClassName";
        final String nodeClassName = "nodeClassName";
        final AtomicInteger counter = new AtomicInteger();
        BlockStmt retrieved = getCompoundPredicateBody(predicate,
                                                       pmmlDataDictionary,
                                                       derivedFields,
                                                       compoundPredicateMethods,
                                                       rootNodeClassName,
                                                       nodeClassName,
                                                       counter);
        assertNotNull(retrieved);
        BlockStmt expected = JavaParserUtils.parseBlock("{\n" +
                                                                "    Boolean toReturn = null;\n" +
                                                                "    final List<Function<Map<String, Object>, Boolean>> functions = java.util.Arrays.asList" +
                                                                "(rootNodeClassName::evaluateNestedPredicatenodeClassName1, " +
                                                                "rootNodeClassName::evaluateNestedPredicatenodeClassName2);\n" +
                                                                "    for (Function<Map<String, Object>, Boolean> function : functions) {\n" +
                                                                "        Boolean evaluation = function.apply(stringObjectMap);\n" +
                                                                "        // generated\n" +
                                                                "        toReturn = toReturn != null ? toReturn || evaluation : evaluation;\n" +
                                                                "    }\n" +
                                                                "    return toReturn != null && toReturn;\n" +
                                                                "}");
        JavaParserUtils.equalsNode(expected, retrieved);
        commonValidateBlockStmt(retrieved, compoundPredicateMethods, rootNodeClassName, predicate.getPredicates().size());
    }

    @Test
    public void getCompoundPredicateMixPredicatesBody() {
        CompoundPredicate predicate = (CompoundPredicate) getNodeById(model, "A_B_A").getPredicate();
        final List<MethodDeclaration> compoundPredicateMethods = new ArrayList<>();
        final String rootNodeClassName = "rootNodeClassName";
        final String nodeClassName = "nodeClassName";
        final AtomicInteger counter = new AtomicInteger();
        BlockStmt retrieved = getCompoundPredicateBody(predicate,
                                               pmmlDataDictionary,
                                               derivedFields,
                                               compoundPredicateMethods,
                                               rootNodeClassName,
                                               nodeClassName,
                                               counter);
        assertNotNull(retrieved);
        BlockStmt expected = JavaParserUtils.parseBlock("{\n" +
                                                                "    Boolean toReturn = null;\n" +
                                                                "    final List<Function<Map<String, Object>, Boolean>> functions = java.util.Arrays.asList" +
                                                                "(rootNodeClassName::evaluateNestedPredicatenodeClassName1, " +
                                                                "rootNodeClassName::evaluateNestedPredicatenodeClassName2, " +
                                                                "rootNodeClassName::evaluateNestedPredicatenodeClassName3, " +
                                                                "rootNodeClassName::evaluateNestedPredicatenodeClassName4, " +
                                                                "rootNodeClassName::evaluateNestedPredicatenodeClassName5);\n" +
                                                                "    for (Function<Map<String, Object>, Boolean> function : functions) {\n" +
                                                                "        Boolean evaluation = function.apply(stringObjectMap);\n" +
                                                                "        // generated\n" +
                                                                "        toReturn = toReturn != null ? toReturn && evaluation : evaluation;\n" +
                                                                "    }\n" +
                                                                "    return toReturn != null && toReturn;\n" +
                                                                "}");
        JavaParserUtils.equalsNode(expected, retrieved);
        commonValidateBlockStmt(retrieved, compoundPredicateMethods, rootNodeClassName, predicate.getPredicates().size());
    }

    private void commonValidateBlockStmt(final BlockStmt retrieved,
                                         final List<MethodDeclaration> compoundPredicateMethods,
                                         final String rootNodeClassName,
                                         final int expectedSize) {
        Optional<VariableDeclarator> optVar = CommonCodegenUtils.getVariableDeclarator(retrieved, "functions");
        assertTrue(optVar.isPresent());
        VariableDeclarator variableDeclarator = optVar.get();
        Optional<Expression> optInit = variableDeclarator.getInitializer();
        assertTrue(optInit.isPresent());
        MethodCallExpr methodCallExpr = (MethodCallExpr) optInit.get();
        NodeList<Expression> arguments = methodCallExpr.getArguments();
        assertEquals(expectedSize, arguments.size());
        assertEquals(arguments.size(), compoundPredicateMethods.size());
        arguments.forEach(expression -> {
            assertTrue(expression instanceof MethodReferenceExpr);
            MethodReferenceExpr methodReferenceExpr = (MethodReferenceExpr)expression;
            assertEquals(rootNodeClassName, methodReferenceExpr.getScope().toString());
            String identifier = methodReferenceExpr.getIdentifier();
            assertTrue(compoundPredicateMethods.stream().anyMatch(methodDeclaration -> identifier.equals(methodDeclaration.getName().asString())) );
        });
    }
}