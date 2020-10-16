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

package org.kie.pmml.models.drools.tree.compiler.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.PMML;
import org.dmg.pmml.tree.ClassifierNode;
import org.dmg.pmml.tree.LeafNode;
import org.dmg.pmml.tree.Node;
import org.dmg.pmml.tree.TreeModel;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.ast.factories.KiePMMLDataDictionaryASTFactory;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFieldType;

public class KiePMMLTreeModelNodeASTFactoryTest {

    private static final String SOURCE_GOLFING = "TreeSample.pmml";
    private static final String SOURCE_IRIS = "irisTree.pmml";
    private PMML golfingPmml;
    private TreeModel golfingModel;
    private PMML irisPmml;
    private TreeModel irisModel;

    @Before
    public void setUp() throws Exception {
        golfingPmml = TestUtils.loadFromFile(SOURCE_GOLFING);
        assertNotNull(golfingPmml);
        assertEquals(1, golfingPmml.getModels().size());
        assertTrue(golfingPmml.getModels().get(0) instanceof TreeModel);
        golfingModel = ((TreeModel) golfingPmml.getModels().get(0));
        irisPmml = TestUtils.loadFromFile(SOURCE_IRIS);
        assertNotNull(irisPmml);
        assertEquals(1, irisPmml.getModels().size());
        assertTrue(irisPmml.getModels().get(0) instanceof TreeModel);
        irisModel = ((TreeModel) irisPmml.getModels().get(0));
    }

    @Test
    public void declareRulesFromRootGolfingNode() {
        Node rootNode = golfingModel.getNode();
        assertEquals("will play", rootNode.getScore());
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        DATA_TYPE targetType = getTargetFieldType(golfingPmml.getDataDictionary(), golfingModel);
        KiePMMLDataDictionaryASTFactory.factory(fieldTypeMap).declareTypes(golfingPmml.getDataDictionary());
        KiePMMLTreeModelNodeASTFactory.factory(fieldTypeMap, Collections.emptyList(), TreeModel.NoTrueChildStrategy.RETURN_NULL_PREDICTION, targetType).declareRulesFromRootNode(rootNode, "_will");
        assertFalse(fieldTypeMap.isEmpty());
    }

    @Test
    public void declareRulesFromRootIrisNode() {
        Node rootNode = irisModel.getNode();
        assertEquals("setosa", rootNode.getScore());
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        DATA_TYPE targetType = getTargetFieldType(irisPmml.getDataDictionary(), irisModel);
        KiePMMLDataDictionaryASTFactory.factory(fieldTypeMap).declareTypes(irisPmml.getDataDictionary());
        KiePMMLTreeModelNodeASTFactory.factory(fieldTypeMap, Collections.emptyList(), TreeModel.NoTrueChildStrategy.RETURN_NULL_PREDICTION, targetType).declareRulesFromRootNode(rootNode, "_setosa");
        assertFalse(fieldTypeMap.isEmpty());
    }

    @Test
    public void declareIntermediateRuleFromGolfingNode() {
        Node finalNode = golfingModel.getNode()
                .getNodes().get(0);
        assertEquals("will play", finalNode.getScore());
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        DATA_TYPE targetType = getTargetFieldType(golfingPmml.getDataDictionary(), golfingModel);
        KiePMMLDataDictionaryASTFactory.factory(fieldTypeMap).declareTypes(golfingPmml.getDataDictionary());
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        KiePMMLTreeModelNodeASTFactory.factory(fieldTypeMap, Collections.emptyList(), TreeModel.NoTrueChildStrategy.RETURN_NULL_PREDICTION, targetType).declareIntermediateRuleFromNode(finalNode, "_will play", rules);
        assertFalse(rules.isEmpty());
    }

    @Test
    public void declareIntermediateRuleFromIrisNode() {
        Node finalNode = irisModel.getNode()
                .getNodes().get(1);
        assertEquals("versicolor", finalNode.getScore());
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        DATA_TYPE targetType = getTargetFieldType(irisPmml.getDataDictionary(), irisModel);
        KiePMMLDataDictionaryASTFactory.factory(fieldTypeMap).declareTypes(irisPmml.getDataDictionary());
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        KiePMMLTreeModelNodeASTFactory.factory(fieldTypeMap, Collections.emptyList(), TreeModel.NoTrueChildStrategy.RETURN_NULL_PREDICTION, targetType).declareIntermediateRuleFromNode(finalNode, "_setosa", rules);
        assertFalse(rules.isEmpty());
    }

    @Test
    public void isFinalLeaf() {
        Node node = new LeafNode();
        DATA_TYPE targetType = DATA_TYPE.STRING;
        KiePMMLTreeModelNodeASTFactory.factory(new HashMap<>(), Collections.emptyList(), TreeModel.NoTrueChildStrategy.RETURN_NULL_PREDICTION, targetType).isFinalLeaf(node);
        assertTrue(KiePMMLTreeModelNodeASTFactory.factory(new HashMap<>(), Collections.emptyList(), TreeModel.NoTrueChildStrategy.RETURN_NULL_PREDICTION, targetType).isFinalLeaf(node));
        node = new ClassifierNode();
        assertTrue(KiePMMLTreeModelNodeASTFactory.factory(new HashMap<>(), Collections.emptyList(), TreeModel.NoTrueChildStrategy.RETURN_NULL_PREDICTION, targetType).isFinalLeaf(node));
        node.addNodes(new LeafNode());
        assertFalse(KiePMMLTreeModelNodeASTFactory.factory(new HashMap<>(), Collections.emptyList(), TreeModel.NoTrueChildStrategy.RETURN_NULL_PREDICTION, targetType).isFinalLeaf(node));
    }
}