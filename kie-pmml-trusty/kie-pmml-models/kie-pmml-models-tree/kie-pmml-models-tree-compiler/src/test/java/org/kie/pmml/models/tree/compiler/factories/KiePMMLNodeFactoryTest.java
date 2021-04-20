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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.PMML;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.tree.Node;
import org.dmg.pmml.tree.TreeModel;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.commons.model.predicates.KiePMMLCompoundPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLFalsePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimplePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimpleSetPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLTruePredicate;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.tree.model.KiePMMLNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromSource;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.KIE_PMML_NODE_TEMPLATE;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.KIE_PMML_NODE_TEMPLATE_JAVA;
import static org.kie.pmml.models.tree.compiler.utils.KiePMMLTreeModelUtils.getNodeClassName;

public class KiePMMLNodeFactoryTest {

    private static final String SOURCE_1 = "TreeSample.pmml";
    private static final String PACKAGE_NAME = "packagename";
    private static PMML pmml;
    private static Node node;
    private static DataDictionary dataDictionary;
    private CompilationUnit compilationUnit;
    private ClassOrInterfaceDeclaration modelTemplate;
    private ConstructorDeclaration constructorDeclaration;

    @BeforeClass
    public static void setupClass() throws Exception {
        pmml = TestUtils.loadFromFile(SOURCE_1);
        dataDictionary = pmml.getDataDictionary();
        node = ((TreeModel) pmml.getModels().get(0)).getNode();
    }

    @Before
    public void setup() {
        compilationUnit = getFromFileName(KIE_PMML_NODE_TEMPLATE_JAVA);
        modelTemplate = compilationUnit.getClassByName(KIE_PMML_NODE_TEMPLATE).get();
        constructorDeclaration = modelTemplate.getDefaultConstructor().get();
    }

    @Test
    public void getKiePMMLNode() {
        final KiePMMLNode retrieved = KiePMMLNodeFactory.getKiePMMLNode(node, dataDictionary, PACKAGE_NAME,
                                                                        new HasClassLoaderMock());
        assertNotNull(retrieved);
        commonVerifyNode(retrieved, node);
    }

    @Test
    public void getKiePMMLNodeSourcesMap() {
        Map<String, String> retrieved = KiePMMLNodeFactory.getKiePMMLNodeSourcesMap(node, dataDictionary,
                                                                                    PACKAGE_NAME);
        assertNotNull(retrieved);
        commonVerifyNodeSource(retrieved, node, PACKAGE_NAME);
    }

    @Test
    public void setConstructor() {
        final String nodeClassName = "NODECLASSNAME";
        final String nodeName = "NODENAME";
        final Object score = "score";
        final List<String> nestedNodes = IntStream.range(0, 3)
                .mapToObj(i -> nodeClassName.toLowerCase() + ".NestedNode" + i)
                .collect(Collectors.toList());
        String expected = "public KiePMMLNodeTemplate() {\n" +
                "    super(name, Collections.emptyList());\n" +
                "}".replace("\n", System.lineSeparator());
        assertEquals(expected, constructorDeclaration.toString());
        KiePMMLNodeFactory.setConstructor(nodeClassName, nodeName, constructorDeclaration);
        commonVerifyConstructorString(constructorDeclaration.toString(), nodeClassName, nodeName);
    }

    @Test
    public void getNodesObjectCreations() {
        List<String> nestedNodes = IntStream.range(0, 3)
                .mapToObj(i -> "apackage.node.NestedNode" + i)
                .collect(Collectors.toList());
        List<ObjectCreationExpr> retrieved = KiePMMLNodeFactory.getNodesObjectCreations(nestedNodes);
        commonNodesObjectCreations(retrieved, nestedNodes);
    }

    private void commonVerifyNode(KiePMMLNode toVerify, Node original) {
        assertEquals(original.getId(), toVerify.getName());
//        assertEquals(original.getScore(), toVerify.getScore());
//        if (original.hasNodes()) {
//            assertEquals(original.getNodes().size(), toVerify.getNodes().size());
//            for (KiePMMLNode toVerifyNested : toVerify.getNodes()) {
//                Optional<Node> originalNested = original.getNodes().stream()
//                        .filter(nestedNode -> nestedNode.getId().equals(toVerifyNested.getName()))
//                        .findFirst();
//                assertTrue(originalNested.isPresent());
//                commonVerifyNode(toVerifyNested, originalNested.get());
//            }
//        } else {
//            assertTrue(toVerify.getNodes().isEmpty());
//        }
    }

    private void commonVerifyConstructorString(final String toVerify,
                                               final String nodeClassName,
                                               final String nodeName) {
        String expected = String.format("public %1$s() {\n" +
                                                "    super(\"%2$s\", Collections.emptyList());" +
                                                "\n" +
                                                "}", nodeClassName, nodeName).replace("\n", System.lineSeparator());
        assertEquals(expected, toVerify);
    }

    private void commonVerifyNodeSource(final Map<String, String> retrieved, final Node original, final String packageName) {
        String nodeClassName = getNodeClassName(original);
        String expectedRootNode = packageName + "." + nodeClassName;
        assertTrue(retrieved.containsKey(expectedRootNode));
        String toVerify = retrieved.get(expectedRootNode);
        CompilationUnit nodeCompilationUnit = getFromSource(toVerify);
        assertEquals(packageName, nodeCompilationUnit.getPackageDeclaration().get().getName().asString());
        final Optional<ConstructorDeclaration> constructorDec = nodeCompilationUnit
                .getClassByName(nodeClassName).get()
                .getDefaultConstructor();
        assertTrue(constructorDec.isPresent());
        if (original.hasNodes()) {
            original.getNodes().forEach(nestedNode -> {
                String childPackage = getSanitizedPackageName(expectedRootNode);
                commonVerifyNodeSource(retrieved, nestedNode, childPackage);
            });
        }

    }

    private void commonNodesObjectCreations(List<ObjectCreationExpr> toVerify, List<String> nestedNodes) {
        assertEquals(toVerify.size(), nestedNodes.size());
        toVerify.forEach(objCrt -> {
            assertTrue(nestedNodes.contains(objCrt.getType().asString()));
        });
    }

    private void commonVerifyPredicate(KiePMMLPredicate toVerify, Predicate original) {
        switch (original.getClass().getSimpleName()) {
            case "True":
                assertTrue(toVerify instanceof KiePMMLTruePredicate);
                break;
            case "False":
                assertTrue(toVerify instanceof KiePMMLFalsePredicate);
                break;
            case "SimplePredicate":
                assertTrue(toVerify instanceof KiePMMLSimplePredicate);
                break;
            case "SimpleSetPredicate":
                assertTrue(toVerify instanceof KiePMMLSimpleSetPredicate);
                break;
            case "CompoundPredicate":
                assertTrue(toVerify instanceof KiePMMLCompoundPredicate);
                break;
            default:
                fail("Unknown Predicate " + original.getClass().getName());
        }
    }
}