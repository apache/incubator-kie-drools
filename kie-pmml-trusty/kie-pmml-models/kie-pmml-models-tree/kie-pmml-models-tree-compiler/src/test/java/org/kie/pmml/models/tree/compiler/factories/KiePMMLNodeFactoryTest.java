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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.Visitor;
import org.dmg.pmml.VisitorAction;
import org.dmg.pmml.tree.Node;
import org.dmg.pmml.tree.TreeModel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.tree.model.KiePMMLNode;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomDataDictionary;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomSimplePredicateOperator;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomValue;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromSource;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.AS_LIST;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.EMPTY_LIST;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.EVALUATE_NODE;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.EVALUATE_PREDICATE;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.NODE_FUNCTIONS;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.PREDICATE_FUNCTION;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.SCORE;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.STRING_OBJECT_MAP;
import static org.kie.pmml.models.tree.compiler.utils.KiePMMLTreeModelUtils.createNodeClassName;

public class KiePMMLNodeFactoryTest {

    private static final String SOURCE_1 = "TreeSample.pmml";
    private static final String SOURCE_2 = "TreeSimplified.pmml";
    private static final String PACKAGE_NAME = "packagename";
    private static PMML pmml1;
    private static Node node1;
    private static DataDictionary dataDictionary1;
    private static PMML pmml2;
    private static Node nodeRoot;
    private static Node nodeLeaf;
    private static DataDictionary dataDictionary2;

    @BeforeClass
    public static void setupClass() throws Exception {
        pmml1 = TestUtils.loadFromFile(SOURCE_1);
        dataDictionary1 = pmml1.getDataDictionary();
        node1 = ((TreeModel) pmml1.getModels().get(0)).getNode();
        pmml2 = TestUtils.loadFromFile(SOURCE_2);
        dataDictionary2 = pmml2.getDataDictionary();
        nodeRoot = ((TreeModel) pmml2.getModels().get(0)).getNode();
        nodeLeaf = nodeRoot.getNodes().get(0).getNodes().get(0).getNodes().get(0);
    }

    @Test
    public void getKiePMMLNode() {
        final KiePMMLNode retrieved = KiePMMLNodeFactory.getKiePMMLNode(node1, dataDictionary1, PACKAGE_NAME,
                                                                        new HasClassLoaderMock());
        assertNotNull(retrieved);
        commonVerifyNode(retrieved, node1);
    }

    @Test
    public void getKiePMMLNodeSourcesMap() {
        final KiePMMLNodeFactory.NodeNamesDTO nodeNamesDTO = new KiePMMLNodeFactory.NodeNamesDTO(node1,
                                                                                                 createNodeClassName(),
                                                                                                 null);

        Map<String, String> retrieved = KiePMMLNodeFactory.getKiePMMLNodeSourcesMap(nodeNamesDTO, dataDictionary1,
                                                                                    PACKAGE_NAME);
        assertNotNull(retrieved);
        commonVerifyNodeSource(retrieved, node1, PACKAGE_NAME);
    }

    @Test
    public void populateJavaParserDTOAndSourcesMap() {
        boolean isRoot = true;
        Map<String, String> sourcesMap = new HashMap<>();
        KiePMMLNodeFactory.NodeNamesDTO nodeNamesDTO = new KiePMMLNodeFactory.NodeNamesDTO(nodeRoot,
                                                                                           createNodeClassName(), null);
        KiePMMLNodeFactory.JavaParserDTO toPopulate = new KiePMMLNodeFactory.JavaParserDTO(nodeNamesDTO, PACKAGE_NAME);
        KiePMMLNodeFactory.populateJavaParserDTOAndSourcesMap(toPopulate, sourcesMap, nodeNamesDTO, dataDictionary2,
                                                              isRoot);
        MethodDeclaration retrieved = CommonCodegenUtils.getMethodDeclaration(toPopulate.nodeTemplate,
                                                                              EVALUATE_PREDICATE).orElseThrow(() -> new RuntimeException("No EVALUATE_PREDICATE in generated class"));
        commonVerifyEvaluatePredicateMethodDeclaration(retrieved, nodeNamesDTO.nodeClassName, isRoot);
        commonVerifyEvaluateNode(toPopulate, nodeNamesDTO, isRoot);
    }

    @Test
    public void mergeNodeReferences() {
        KiePMMLNodeFactory.NodeNamesDTO nodeNamesDTO = new KiePMMLNodeFactory.NodeNamesDTO(nodeRoot,
                                                                                           createNodeClassName(), null);
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
                                                                                                 nodeNamesDTO.getNestedNodeClassName(nestedNode), nodeNamesDTO.nodeClassName);
        KiePMMLNodeFactory.mergeNodeReferences(toPopulate, nestedNodeNamesDTO, evaluateNodeInitializer);

        MethodReferenceExpr retrieved = evaluateNodeInitializer.getArguments().get(0).asMethodReferenceExpr();
        String expected = toPopulate.nodeClassName;
        assertEquals(expected, retrieved.getScope().asNameExpr().toString());
        expected = EVALUATE_NODE + nestedNodeClassName;
        assertEquals(expected, retrieved.getIdentifier());
    }

    @Test
    public void populateEvaluatePredicate() {
        final String packageName = "packageName";
        final DataDictionary dataDictionary = getRandomDataDictionary();
        final DataField dataField = dataDictionary.getDataFields().get(0);
        dataField.setDataType(DataType.DOUBLE);

        // empty node
        boolean isRoot = false;
        Node node = new NodeMock(false, dataField.getName().getValue(), dataField.getDataType());
        KiePMMLNodeFactory.NodeNamesDTO nodeNamesDTO = new KiePMMLNodeFactory.NodeNamesDTO(node, createNodeClassName(),
                                                                                           "PARENTNODECLASS");
        KiePMMLNodeFactory.JavaParserDTO toPopulate = new KiePMMLNodeFactory.JavaParserDTO(nodeNamesDTO, packageName);

        KiePMMLNodeFactory.populateEvaluatePredicate(toPopulate, dataDictionary, nodeNamesDTO, isRoot);
        MethodDeclaration retrieved = CommonCodegenUtils.getMethodDeclaration(toPopulate.nodeTemplate,
                                                                              EVALUATE_PREDICATE + nodeNamesDTO.nodeClassName).orElseThrow(() -> new RuntimeException("No EVALUATE_PREDICATE in generated class"));
        commonVerifyEvaluatePredicateMethodDeclaration(retrieved, nodeNamesDTO.nodeClassName, isRoot);

        // root node
        isRoot = true;
        node = new NodeMock(true, dataField.getName().getValue(), dataField.getDataType());
        nodeNamesDTO = new KiePMMLNodeFactory.NodeNamesDTO(node, createNodeClassName(), null);
        toPopulate = new KiePMMLNodeFactory.JavaParserDTO(nodeNamesDTO, packageName);
        KiePMMLNodeFactory.populateEvaluatePredicate(toPopulate, dataDictionary, nodeNamesDTO, isRoot);
        retrieved =
                CommonCodegenUtils.getMethodDeclaration(toPopulate.nodeTemplate, EVALUATE_PREDICATE).orElseThrow(() -> new RuntimeException("No EVALUATE_PREDICATE in generated class"));
        commonVerifyEvaluatePredicateMethodDeclaration(retrieved, nodeNamesDTO.nodeClassName, isRoot);
    }

    @Test
    public void populateEvaluateNode() {
        final String packageName = "packageName";
        // empty node
        boolean isRoot = false;
        KiePMMLNodeFactory.NodeNamesDTO nodeNamesDTO = new KiePMMLNodeFactory.NodeNamesDTO(nodeLeaf,
                                                                                           createNodeClassName(),
                                                                                           "PARENTNODECLASS");
        KiePMMLNodeFactory.JavaParserDTO toPopulate = new KiePMMLNodeFactory.JavaParserDTO(nodeNamesDTO, packageName);
        KiePMMLNodeFactory.populateEvaluateNode(toPopulate, nodeNamesDTO, isRoot);
        commonVerifyEvaluateNode(toPopulate, nodeNamesDTO, isRoot);

        // populated node
        isRoot = true;
        nodeNamesDTO = new KiePMMLNodeFactory.NodeNamesDTO(nodeRoot, createNodeClassName(), null);
        toPopulate = new KiePMMLNodeFactory.JavaParserDTO(nodeNamesDTO, packageName);
        KiePMMLNodeFactory.populateEvaluateNode(toPopulate, nodeNamesDTO, isRoot);
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
    public void populateEvaluateNodeWithPredicateFunction() {
        BlockStmt toPopulate = new BlockStmt();
        VariableDeclarator variableDeclarator = new VariableDeclarator();
        variableDeclarator.setName(PREDICATE_FUNCTION);
        toPopulate.addStatement(new VariableDeclarationExpr(variableDeclarator));
        assertFalse(variableDeclarator.getInitializer().isPresent());
        final String nodeClassName = "NodeClassName";
        final String nestedNodeClassName = "NestedNodeClassName";
        boolean isRoot = true;
        KiePMMLNodeFactory.populateEvaluateNodeWithPredicateFunction(toPopulate, nodeClassName, nestedNodeClassName,
                                                                     isRoot);
        commonVerifyEvaluateNodeWithPredicateFunction(variableDeclarator, nodeClassName, nestedNodeClassName, isRoot);
        toPopulate = new BlockStmt();
        variableDeclarator = new VariableDeclarator();
        variableDeclarator.setName(PREDICATE_FUNCTION);
        toPopulate.addStatement(new VariableDeclarationExpr(variableDeclarator));
        assertFalse(variableDeclarator.getInitializer().isPresent());
        isRoot = false;
        KiePMMLNodeFactory.populateEvaluateNodeWithPredicateFunction(toPopulate, nodeClassName, nestedNodeClassName,
                                                                     isRoot);
        commonVerifyEvaluateNodeWithPredicateFunction(variableDeclarator, nodeClassName, nestedNodeClassName, isRoot);
    }

    @Test
    public void getEvaluateNestedPredicateMethodDeclaration() {
        final BlockStmt body = new BlockStmt();
        final String nodeClassName = "NodeClassName";
        // nested node
        MethodDeclaration retrieved = KiePMMLNodeFactory.getEvaluateNestedPredicateMethodDeclaration(body,
                                                                                                     nodeClassName);
        commonVerifyEvaluatePredicateMethodDeclaration(retrieved, nodeClassName, false);
    }

    @Test
    public void nodeNamesDTO() {
        KiePMMLNodeFactory.NodeNamesDTO retrieved = new KiePMMLNodeFactory.NodeNamesDTO(nodeRoot, createNodeClassName(),
                                                                                        PACKAGE_NAME);
        assertEquals(nodeRoot.getNodes().size(), retrieved.childrenNodes.size());
    }

    private void commonVerifyEvaluateNode(final KiePMMLNodeFactory.JavaParserDTO toPopulate,
                                          final KiePMMLNodeFactory.NodeNamesDTO nodeNamesDTO, final boolean isRoot) {
        BlockStmt blockStmt = isRoot ? toPopulate.evaluateRootNodeBody :
                toPopulate.nodeTemplate.getMethodsByName(EVALUATE_NODE + nodeNamesDTO.nodeClassName).get(0).getBody().orElseThrow(() -> new RuntimeException("No body in nested node evaluate node"));
        VariableDeclarator variableDeclarator = CommonCodegenUtils.getVariableDeclarator(blockStmt,
                                                                                         PREDICATE_FUNCTION).orElseThrow(() -> new RuntimeException("No PREDICATE_FUNCTION variable declarator in generated methodCallExpr"));
        commonVerifyEvaluateNodeWithPredicateFunction(variableDeclarator, toPopulate.nodeClassName,
                                                      nodeNamesDTO.nodeClassName, isRoot);
        variableDeclarator =
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

    private void commonVerifyEvaluateNodeWithPredicateFunction(final VariableDeclarator variableDeclarator,
                                                               final String nodeClassName,
                                                               final String nestedNodeClassName, final boolean isRoot) {
        assertTrue(variableDeclarator.getInitializer().isPresent());
        Expression expression = variableDeclarator.getInitializer().get();
        assertTrue(expression instanceof MethodReferenceExpr);
        MethodReferenceExpr retrieved = (MethodReferenceExpr) expression;
        assertEquals(nodeClassName, retrieved.getScope().toString());
        String expected = isRoot ? EVALUATE_PREDICATE : EVALUATE_PREDICATE + nestedNodeClassName;
        assertEquals(expected, retrieved.getIdentifier());
    }

    private void commonVerifyEvaluatePredicateMethodDeclaration(final MethodDeclaration retrieved,
                                                                final String nodeClassName,
                                                                boolean isRoot) {
        assertNotNull(retrieved);
        assertEquals("boolean", retrieved.getType().asString());
        assertEquals(1, retrieved.getParameters().size());
        Parameter retrievedParameter = retrieved.getParameter(0);
        String expected = STRING_OBJECT_MAP;
        assertEquals(expected, retrievedParameter.getName().asString());
        expected = String.format("%1$s<%2$s,%3$s>", Map.class.getName(), "String", "Object");
        assertEquals(expected, retrievedParameter.getType().asString());
        assertEquals(2, retrieved.getModifiers().size());
        assertTrue(retrieved.getModifiers().stream().anyMatch(modifier -> modifier.getKeyword().equals(Modifier.Keyword.PRIVATE)));
        assertTrue(retrieved.getModifiers().stream().anyMatch(modifier -> modifier.getKeyword().equals(Modifier.Keyword.STATIC)));
        expected = isRoot ? EVALUATE_PREDICATE : EVALUATE_PREDICATE + nodeClassName;
        assertEquals(expected, retrieved.getName().asString());
    }

    private void commonVerifyNode(KiePMMLNode toVerify, Node original) {
        assertEquals(original.getId(), toVerify.getName());
    }

    private void commonVerifyNodeSource(final Map<String, String> retrieved, final Node original,
                                        final String packageName) {
        assertEquals(1, retrieved.size());
        String toVerify = retrieved.values().iterator().next();
        CompilationUnit nodeCompilationUnit = getFromSource(toVerify);
        assertEquals(packageName, nodeCompilationUnit.getPackageDeclaration().get().getName().asString());
    }

    private static class NodeMock extends Node {

        Predicate predicate = new SimplePredicate();
        Object score;
        boolean hasNodes;
        List<Node> nodes;

        public NodeMock(boolean hasNodes) {
            score = new Random().nextInt(20);
            this.hasNodes = hasNodes;
            if (hasNodes) {
                nodes = IntStream.range(0, 2)
                        .mapToObj(i -> new NodeMock(false))
                        .collect(Collectors.toList());
            }
            ((SimplePredicate) predicate).setOperator(getRandomSimplePredicateOperator());
        }

        public NodeMock(boolean hasNodes, String predicateField, DataType dataType) {
            this(hasNodes);
            ((SimplePredicate) predicate).setField(FieldName.create(predicateField));
            ((SimplePredicate) predicate).setValue(getRandomValue(dataType));
        }

        @Override
        public Predicate getPredicate() {
            return predicate;
        }

        @Override
        public Node setPredicate(Predicate predicate) {
            this.predicate = predicate;
            return this;
        }

        @Override
        public VisitorAction accept(Visitor visitor) {
            return null;
        }

        @Override
        public Object getScore() {
            return score;
        }

        @Override
        public boolean hasNodes() {
            return hasNodes;
        }

        @Override
        public List<Node> getNodes() {
            return hasNodes ? nodes : super.getNodes();
        }
    }
}