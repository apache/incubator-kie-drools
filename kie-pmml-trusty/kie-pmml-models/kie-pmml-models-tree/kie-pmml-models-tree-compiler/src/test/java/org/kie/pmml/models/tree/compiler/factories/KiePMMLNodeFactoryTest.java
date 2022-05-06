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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.PMML;
import org.dmg.pmml.ScoreDistribution;
import org.dmg.pmml.tree.Node;
import org.dmg.pmml.tree.TreeModel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.compiler.api.testutils.TestUtils;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.tree.model.KiePMMLNode;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getFieldsFromDataDictionaryAndDerivedFields;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomPMMLScoreDistributions;
import static org.kie.pmml.compiler.api.utils.ModelUtils.getDerivedFields;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromSource;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.AS_LIST;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.EMPTY_LIST;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.EVALUATE_NODE;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.MISSING_VALUE_PENALTY;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.NODE_FUNCTIONS;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.SCORE;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.SCORE_DISTRIBUTIONS;
import static org.kie.pmml.models.tree.compiler.utils.KiePMMLTreeModelUtils.createNodeClassName;
import static org.kie.test.util.filesystem.FileUtils.getFileContent;

public class KiePMMLNodeFactoryTest {

    private static final String SOURCE_1 = "TreeSample.pmml";
    private static final String SOURCE_2 = "TreeSimplified.pmml";
    private static final String TEST_01_SOURCE = "KiePMMLNodeFactoryTest_01.txt";
    private static PMML pmml1;
    private static Node node1;
    private static DataDictionary dataDictionary1;
    private static List<DerivedField> derivedFields1;
    private static PMML pmml2;
    private static Node nodeRoot;
    private static Node compoundPredicateNode;
    private static Node nodeLeaf;
    private static DataDictionary dataDictionary2;
    private static List<DerivedField> derivedFields2;

    @BeforeClass
    public static void setupClass() throws Exception {
        pmml1 = TestUtils.loadFromFile(SOURCE_1);
        TreeModel model1 = (TreeModel) pmml1.getModels().get(0);
        dataDictionary1 = pmml1.getDataDictionary();
        derivedFields1 = getDerivedFields(pmml1.getTransformationDictionary(), model1.getLocalTransformations());
        node1 = model1.getNode();
        pmml2 = TestUtils.loadFromFile(SOURCE_2);
        TreeModel model2 = (TreeModel) pmml2.getModels().get(0);
        dataDictionary2 = pmml2.getDataDictionary();
        derivedFields2 = getDerivedFields(pmml2.getTransformationDictionary(), model2.getLocalTransformations());
        nodeRoot = model2.getNode();
        compoundPredicateNode = nodeRoot.getNodes().get(0);
        nodeLeaf = nodeRoot.getNodes().get(0).getNodes().get(0).getNodes().get(0);
    }

    @Test
    public void getKiePMMLNode() {
        final KiePMMLNode retrieved = KiePMMLNodeFactory.getKiePMMLNode(node1,
                                                                        getFieldsFromDataDictionaryAndDerivedFields(dataDictionary1, derivedFields1),
                                                                        PACKAGE_NAME,
                                                                        1.0,
                                                                        new HasClassLoaderMock());
        assertThat(retrieved).isNotNull();
        commonVerifyNode(retrieved, node1);
    }

    @Test
    public void getKiePMMLNodeSourcesMap() {
        final KiePMMLNodeFactory.NodeNamesDTO nodeNamesDTO = new KiePMMLNodeFactory.NodeNamesDTO(node1,
                                                                                                 createNodeClassName(),
                                                                                                 null,
                                                                                                 1.0);

        Map<String, String> retrieved = KiePMMLNodeFactory.getKiePMMLNodeSourcesMap(nodeNamesDTO,
                                                                                    getFieldsFromDataDictionaryAndDerivedFields(dataDictionary1, derivedFields1),
                                                                                    PACKAGE_NAME);
        assertThat(retrieved).isNotNull();
        commonVerifyNodeSource(retrieved, PACKAGE_NAME);
    }

    @Test
    public void populateJavaParserDTOAndSourcesMap() {
        boolean isRoot = true;
        Map<String, String> sourcesMap = new HashMap<>();
        KiePMMLNodeFactory.NodeNamesDTO nodeNamesDTO = new KiePMMLNodeFactory.NodeNamesDTO(nodeRoot,
                                                                                           createNodeClassName(), null,
                                                                                           1.0);
        KiePMMLNodeFactory.JavaParserDTO toPopulate = new KiePMMLNodeFactory.JavaParserDTO(nodeNamesDTO, PACKAGE_NAME);
        KiePMMLNodeFactory.populateJavaParserDTOAndSourcesMap(toPopulate, sourcesMap, nodeNamesDTO,
                                                              getFieldsFromDataDictionaryAndDerivedFields(dataDictionary2, derivedFields2),
                                                              isRoot);
        commonVerifyEvaluateNode(toPopulate, nodeNamesDTO, isRoot);
    }

    @Test
    public void mergeNodeReferences() {
        KiePMMLNodeFactory.NodeNamesDTO nodeNamesDTO = new KiePMMLNodeFactory.NodeNamesDTO(nodeRoot,
                                                                                           createNodeClassName(), null,
                                                                                           1.0);
        KiePMMLNodeFactory.JavaParserDTO toPopulate = new KiePMMLNodeFactory.JavaParserDTO(nodeNamesDTO, PACKAGE_NAME);
        Node nestedNode = nodeRoot.getNodes().get(0);
        // Creating evaluateNodeInitializer
        String nestedNodeClassName = nodeNamesDTO.childrenNodes.get(nestedNode);
        String fullNestedNodeClassName = String.format(PACKAGE_CLASS_TEMPLATE, PACKAGE_NAME, nestedNodeClassName);

        final NodeList<Expression> methodReferenceExprs =
                NodeList.nodeList(KiePMMLNodeFactory.getEvaluateNodeMethodReference(fullNestedNodeClassName));
        MethodReferenceExpr evaluateNodeReference = new MethodReferenceExpr();
        evaluateNodeReference.setScope(new NameExpr(fullNestedNodeClassName));
        evaluateNodeReference.setIdentifier(EVALUATE_NODE);
        MethodCallExpr evaluateNodeInitializer = new MethodCallExpr();
        evaluateNodeInitializer.setScope(new TypeExpr(parseClassOrInterfaceType(Arrays.class.getName())));
        evaluateNodeInitializer.setName(AS_LIST);
        evaluateNodeInitializer.setArguments(methodReferenceExprs);
        //
        KiePMMLNodeFactory.NodeNamesDTO nestedNodeNamesDTO = new KiePMMLNodeFactory.NodeNamesDTO(nestedNode,
                                                                                                 nodeNamesDTO.getNestedNodeClassName(nestedNode), nodeNamesDTO.nodeClassName, nodeNamesDTO.missingValuePenalty);
        KiePMMLNodeFactory.mergeNodeReferences(toPopulate, nestedNodeNamesDTO, evaluateNodeInitializer);

        MethodReferenceExpr retrieved = evaluateNodeInitializer.getArguments().get(0).asMethodReferenceExpr();
        String expected = toPopulate.nodeClassName;
        assertEquals(expected, retrieved.getScope().asNameExpr().toString());
        expected = EVALUATE_NODE + nestedNodeClassName;
        assertEquals(expected, retrieved.getIdentifier());
    }

    @Test
    public void populateEvaluateNode() {
        final String packageName = "packageName";
        // empty node
        boolean isRoot = false;
        KiePMMLNodeFactory.NodeNamesDTO nodeNamesDTO = new KiePMMLNodeFactory.NodeNamesDTO(nodeLeaf,
                                                                                           createNodeClassName(),
                                                                                           "PARENTNODECLASS",
                                                                                           1.0);
        KiePMMLNodeFactory.JavaParserDTO toPopulate = new KiePMMLNodeFactory.JavaParserDTO(nodeNamesDTO, packageName);
        KiePMMLNodeFactory.populateEvaluateNode(toPopulate,
                                                nodeNamesDTO,
                                                getFieldsFromDataDictionaryAndDerivedFields(dataDictionary2,
                                                                                            derivedFields2),
                                                isRoot);
        commonVerifyEvaluateNode(toPopulate, nodeNamesDTO, isRoot);

        // populated node
        isRoot = true;
        nodeNamesDTO = new KiePMMLNodeFactory.NodeNamesDTO(nodeRoot, createNodeClassName(), null, 1.0);
        toPopulate = new KiePMMLNodeFactory.JavaParserDTO(nodeNamesDTO, packageName);
        KiePMMLNodeFactory.populateEvaluateNode(toPopulate,
                                                nodeNamesDTO,
                                                getFieldsFromDataDictionaryAndDerivedFields(dataDictionary2,
                                                                                            derivedFields2),
                                                isRoot);
        commonVerifyEvaluateNode(toPopulate, nodeNamesDTO, isRoot);
    }

    @Test
    public void populateEvaluateNodeWithNodeFunctions() {
        final BlockStmt toPopulate = new BlockStmt();
        final VariableDeclarator variableDeclarator = new VariableDeclarator();
        variableDeclarator.setType("Object");
        variableDeclarator.setName(NODE_FUNCTIONS);
        toPopulate.addStatement(new VariableDeclarationExpr(variableDeclarator));
        assertFalse(variableDeclarator.getInitializer().isPresent());
        // empty list
        List<String> nestedNodesFullClasses = Collections.emptyList();
        KiePMMLNodeFactory.populateEvaluateNodeWithNodeFunctions(toPopulate, nestedNodesFullClasses);
        commonVerifyEvaluateNodeWithNodeFunctions(variableDeclarator, nestedNodesFullClasses);

        // populated list
        nestedNodesFullClasses = IntStream.range(0, 2)
                .mapToObj(i -> "full.node.NodeClassName" + i)
                .collect(Collectors.toList());
        KiePMMLNodeFactory.populateEvaluateNodeWithNodeFunctions(toPopulate, nestedNodesFullClasses);
        commonVerifyEvaluateNodeWithNodeFunctions(variableDeclarator, nestedNodesFullClasses);
    }

    @Test
    public void getEvaluateNodeMethodReference() {
        String fullNodeClassName = "full.node.NodeClassName";
        MethodReferenceExpr retrieved = KiePMMLNodeFactory.getEvaluateNodeMethodReference(fullNodeClassName);
        assertEquals(fullNodeClassName, retrieved.getScope().toString());
        assertEquals(EVALUATE_NODE, retrieved.getIdentifier());
    }

    @Test
    public void populateEvaluateNodeWithScore() {
        final BlockStmt toPopulate = new BlockStmt();
        final VariableDeclarator variableDeclarator = new VariableDeclarator();
        variableDeclarator.setType("Object");
        variableDeclarator.setName(SCORE);
        toPopulate.addStatement(new VariableDeclarationExpr(variableDeclarator));
        assertFalse(variableDeclarator.getInitializer().isPresent());
        // null score
        Object score = null;
        KiePMMLNodeFactory.populateEvaluateNodeWithScore(toPopulate, score);
        commonVerifyEvaluateNodeWithScore(variableDeclarator, score);
        // string score
        score = "scoreValue";
        KiePMMLNodeFactory.populateEvaluateNodeWithScore(toPopulate, score);
        commonVerifyEvaluateNodeWithScore(variableDeclarator, score);
        // not-string score
        score = 54345.34;
        KiePMMLNodeFactory.populateEvaluateNodeWithScore(toPopulate, score);
        commonVerifyEvaluateNodeWithScore(variableDeclarator, score);
    }

    @Test
    public void populateEvaluateNodeWithScoreDistributions() {
        final BlockStmt toPopulate = new BlockStmt();
        final VariableDeclarator variableDeclarator = new VariableDeclarator();
        variableDeclarator.setType("List");
        variableDeclarator.setName(SCORE_DISTRIBUTIONS);
        toPopulate.addStatement(new VariableDeclarationExpr(variableDeclarator));
        assertFalse(variableDeclarator.getInitializer().isPresent());
        // Without probability
        List<ScoreDistribution> scoreDistributions = getRandomPMMLScoreDistributions(false);
        KiePMMLNodeFactory.populateEvaluateNodeWithScoreDistributions(toPopulate, scoreDistributions);
        commonVerifyEvaluateNodeWithScoreDistributions(variableDeclarator, scoreDistributions);
        // With probability
        scoreDistributions = getRandomPMMLScoreDistributions(true);
        KiePMMLNodeFactory.populateEvaluateNodeWithScoreDistributions(toPopulate, scoreDistributions);
        commonVerifyEvaluateNodeWithScoreDistributions(variableDeclarator, scoreDistributions);
    }

    @Test
    public void populateEvaluateNodeWithMissingValuePenalty() {
        final BlockStmt toPopulate = new BlockStmt();
        final VariableDeclarator variableDeclarator = new VariableDeclarator();
        variableDeclarator.setType("double");
        variableDeclarator.setName(MISSING_VALUE_PENALTY);
        toPopulate.addStatement(new VariableDeclarationExpr(variableDeclarator));
        assertFalse(variableDeclarator.getInitializer().isPresent());
        final double missingValuePenalty = new Random().nextDouble();
        KiePMMLNodeFactory.populateEvaluateNodeWithMissingValuePenalty(toPopulate, missingValuePenalty);
        assertTrue(variableDeclarator.getInitializer().isPresent());
        Expression expression = variableDeclarator.getInitializer().get();
        assertTrue(expression instanceof DoubleLiteralExpr);
        DoubleLiteralExpr doubleLiteralExpr = (DoubleLiteralExpr) expression;
        assertEquals(missingValuePenalty, doubleLiteralExpr.asDouble(), 0.0);
    }

    @Test
    public void populateEvaluateNodeWithPredicateFunction() throws IOException {
        BlockStmt toPopulate = new BlockStmt();
        KiePMMLNodeFactory.populateEvaluateNodeWithPredicate(toPopulate,
                                                             compoundPredicateNode.getPredicate(),
                                                             getFieldsFromDataDictionaryAndDerivedFields(dataDictionary2, derivedFields2));
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(text);
        assertTrue(JavaParserUtils.equalsNode(expected, toPopulate));
    }

    @Test
    public void nodeNamesDTO() {
        KiePMMLNodeFactory.NodeNamesDTO retrieved = new KiePMMLNodeFactory.NodeNamesDTO(nodeRoot, createNodeClassName(),
                                                                                        PACKAGE_NAME, 1.0);
        assertEquals(nodeRoot.getNodes().size(), retrieved.childrenNodes.size());
    }

    private void commonVerifyEvaluateNode(final KiePMMLNodeFactory.JavaParserDTO toPopulate,
                                          final KiePMMLNodeFactory.NodeNamesDTO nodeNamesDTO, final boolean isRoot) {
        BlockStmt blockStmt = isRoot ? toPopulate.evaluateRootNodeBody :
                toPopulate.nodeTemplate.getMethodsByName(EVALUATE_NODE + nodeNamesDTO.nodeClassName).get(0).getBody().orElseThrow(() -> new RuntimeException("No body in nested node evaluate node"));
        VariableDeclarator variableDeclarator =
                CommonCodegenUtils.getVariableDeclarator(blockStmt, SCORE).orElseThrow(() -> new RuntimeException("No SCORE variable declarator in generated methodCallExpr"));
        commonVerifyEvaluateNodeWithScore(variableDeclarator, nodeNamesDTO.node.getScore());
        variableDeclarator =
                CommonCodegenUtils.getVariableDeclarator(blockStmt, NODE_FUNCTIONS).orElseThrow(() -> new RuntimeException("No NODE_FUNCTIONS variable declarator in generated methodCallExpr"));
        if (isRoot) {
            commonVerifyEvaluateNodeWithNodeFunctions(variableDeclarator,
                                                      nodeNamesDTO.getNestedNodesFullClassNames(toPopulate.packageName));
        } else {
            commonVerifyEvaluateNodeWithNodeFunctions(variableDeclarator,
                                                      new ArrayList<>(nodeNamesDTO.childrenNodes.values()));
        }
    }

    private void commonVerifyEvaluateNodeWithNodeFunctions(final VariableDeclarator variableDeclarator,
                                                           final List<String> nestedNodesFullClasses) {
        assertTrue(variableDeclarator.getInitializer().isPresent());
        Expression expression = variableDeclarator.getInitializer().get();
        assertTrue(expression instanceof MethodCallExpr);
        MethodCallExpr methodCallExpr = (MethodCallExpr) expression;
        Expression scope = methodCallExpr.getScope().orElseThrow(() -> new RuntimeException("No scope in generated " +
                                                                                                    "methodCallExpr"));
        if (nestedNodesFullClasses.isEmpty()) {
            assertEquals(Collections.class.getName(), scope.toString());
            assertEquals(EMPTY_LIST, methodCallExpr.getName().asString());
            assertTrue(methodCallExpr.getArguments().isEmpty());
        } else {
            assertEquals(Arrays.class.getName(), scope.toString());
            assertEquals(AS_LIST, methodCallExpr.getName().asString());
            assertEquals(nestedNodesFullClasses.size(), methodCallExpr.getArguments().size());
        }
    }

    private void commonVerifyEvaluateNodeWithScore(final VariableDeclarator variableDeclarator, final Object score) {
        assertTrue(variableDeclarator.getInitializer().isPresent());
        Expression expression = variableDeclarator.getInitializer().get();
        if (score == null) {
            assertTrue(expression instanceof NullLiteralExpr);
        } else {
            assertTrue(expression instanceof NameExpr);
            String toFormat = score instanceof String ? "\"%s\"" : "%s";
            assertEquals(String.format(toFormat, score), expression.toString());
        }
    }

    private void commonVerifyEvaluateNodeWithScoreDistributions(final VariableDeclarator variableDeclarator,
                                                                final List<ScoreDistribution> scoreDistributions) {
        assertTrue(variableDeclarator.getInitializer().isPresent());
        Expression expression = variableDeclarator.getInitializer().get();
        assertTrue(expression instanceof MethodCallExpr);
        MethodCallExpr methodCallExpr = (MethodCallExpr) expression;
        assertEquals("Arrays", methodCallExpr.getScope().get().toString());
        assertEquals("asList", methodCallExpr.getName().toString());
        NodeList<Expression> arguments = methodCallExpr.getArguments();
        assertEquals(scoreDistributions.size(), arguments.size());
        arguments.forEach(argument -> assertTrue(argument instanceof ObjectCreationExpr));
        List<ObjectCreationExpr> objectCreationExprs = arguments.stream()
                .map(ObjectCreationExpr.class::cast)
                .collect(Collectors.toList());
        scoreDistributions.forEach(scoreDistribution -> {
            Optional<ObjectCreationExpr> retrieved = objectCreationExprs.stream()
                    .filter(objectCreationExpr -> scoreDistribution.getValue().equals(objectCreationExpr.getArgument(2).asStringLiteralExpr().asString()))
                    .findFirst();
            assertTrue(retrieved.isPresent());
            Expression recordCountExpected = getExpressionForObject(scoreDistribution.getRecordCount().intValue());
            Expression confidenceExpected = getExpressionForObject(scoreDistribution.getConfidence().doubleValue());
            Expression probabilityExpected = scoreDistribution.getProbability() != null ?
                    getExpressionForObject(scoreDistribution.getProbability().doubleValue()) : new NullLiteralExpr();
            retrieved.ifPresent(objectCreationExpr -> {
                assertEquals(recordCountExpected, objectCreationExpr.getArgument(3));
                assertEquals(confidenceExpected, objectCreationExpr.getArgument(4));
                assertEquals(probabilityExpected, objectCreationExpr.getArgument(5));
            });
        });
    }

    private void commonVerifyNode(KiePMMLNode toVerify, Node original) {
        assertEquals(original.getId(), toVerify.getName());
    }

    private void commonVerifyNodeSource(final Map<String, String> retrieved, final String packageName) {
        assertEquals(1, retrieved.size());
        String toVerify = retrieved.values().iterator().next();
        CompilationUnit nodeCompilationUnit = getFromSource(toVerify);
        assertEquals(packageName, nodeCompilationUnit.getPackageDeclaration().get().getName().asString());
    }
}