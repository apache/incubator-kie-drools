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
package org.kie.pmml.models.drools.tree.compiler.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.Field;
import org.dmg.pmml.PMML;
import org.dmg.pmml.tree.ClassifierNode;
import org.dmg.pmml.tree.LeafNode;
import org.dmg.pmml.tree.Node;
import org.dmg.pmml.tree.TreeModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.compiler.api.testutils.TestUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.ast.factories.KiePMMLDataDictionaryASTFactory;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getFieldsFromDataDictionary;
import static org.kie.pmml.compiler.api.utils.ModelUtils.getTargetFieldType;

public class KiePMMLTreeModelNodeASTFactoryTest {

    private static final String SOURCE_GOLFING = "TreeSample.pmml";
    private static final String SOURCE_IRIS = "irisTree.pmml";
    private PMML golfingPmml;
    private TreeModel golfingModel;
    private PMML irisPmml;
    private TreeModel irisModel;

    @BeforeEach
    public void setUp() throws Exception {
        golfingPmml = TestUtils.loadFromFile(SOURCE_GOLFING);
        assertThat(golfingPmml).isNotNull();
        assertThat(golfingPmml.getModels()).hasSize(1);
        assertThat(golfingPmml.getModels().get(0)).isInstanceOf(TreeModel.class);
        golfingModel = ((TreeModel) golfingPmml.getModels().get(0));
        irisPmml = TestUtils.loadFromFile(SOURCE_IRIS);
        assertThat(irisPmml).isNotNull();
        assertThat(irisPmml.getModels()).hasSize(1);
        assertThat(irisPmml.getModels().get(0)).isInstanceOf(TreeModel.class);
        irisModel = ((TreeModel) irisPmml.getModels().get(0));
    }

    @Test
    void declareRulesFromRootGolfingNode() {
        Node rootNode = golfingModel.getNode();
        assertThat(rootNode.getScore()).isEqualTo("will play");
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        final List<Field<?>> fields = getFieldsFromDataDictionary(golfingPmml.getDataDictionary());
        DATA_TYPE targetType = getTargetFieldType(fields, golfingModel);
        KiePMMLDataDictionaryASTFactory.factory(fieldTypeMap).declareTypes(fields);
        KiePMMLTreeModelNodeASTFactory.factory(fieldTypeMap, Collections.emptyList(), TreeModel.NoTrueChildStrategy.RETURN_NULL_PREDICTION, targetType).declareRulesFromRootNode(rootNode, "_will");
        assertThat(fieldTypeMap).isNotEmpty();
    }

    @Test
    void declareRulesFromRootIrisNode() {
        Node rootNode = irisModel.getNode();
        assertThat(rootNode.getScore()).isEqualTo("setosa");
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        final List<Field<?>> fields = getFieldsFromDataDictionary(irisPmml.getDataDictionary());
        DATA_TYPE targetType = getTargetFieldType(fields, irisModel);
        KiePMMLDataDictionaryASTFactory.factory(fieldTypeMap).declareTypes(fields);
        KiePMMLTreeModelNodeASTFactory.factory(fieldTypeMap, Collections.emptyList(), TreeModel.NoTrueChildStrategy.RETURN_NULL_PREDICTION, targetType).declareRulesFromRootNode(rootNode, "_setosa");
        assertThat(fieldTypeMap).isNotEmpty();
    }

    @Test
    void declareIntermediateRuleFromGolfingNode() {
        Node finalNode = golfingModel.getNode()
                .getNodes().get(0);
        assertThat(finalNode.getScore()).isEqualTo("will play");
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        final List<Field<?>> fields = getFieldsFromDataDictionary(golfingPmml.getDataDictionary());
        DATA_TYPE targetType = getTargetFieldType(fields, golfingModel);
        KiePMMLDataDictionaryASTFactory.factory(fieldTypeMap).declareTypes(fields);
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        KiePMMLTreeModelNodeASTFactory.factory(fieldTypeMap, Collections.emptyList(), TreeModel.NoTrueChildStrategy.RETURN_NULL_PREDICTION, targetType).declareIntermediateRuleFromNode(finalNode, "_will play", rules);
        assertThat(rules).isNotEmpty();
    }

    @Test
    void declareIntermediateRuleFromIrisNode() {
        Node finalNode = irisModel.getNode()
                .getNodes().get(1);
        assertThat(finalNode.getScore()).isEqualTo("versicolor");
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        final List<Field<?>> fields = getFieldsFromDataDictionary(irisPmml.getDataDictionary());
        DATA_TYPE targetType = getTargetFieldType(fields, irisModel);
        KiePMMLDataDictionaryASTFactory.factory(fieldTypeMap).declareTypes(fields);
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        KiePMMLTreeModelNodeASTFactory.factory(fieldTypeMap, Collections.emptyList(), TreeModel.NoTrueChildStrategy.RETURN_NULL_PREDICTION, targetType).declareIntermediateRuleFromNode(finalNode, "_setosa", rules);
        assertThat(rules).isNotEmpty();
    }

    @Test
    void isFinalLeaf() {
        Node node = new LeafNode();
        DATA_TYPE targetType = DATA_TYPE.STRING;
        KiePMMLTreeModelNodeASTFactory.factory(new HashMap<>(), Collections.emptyList(), TreeModel.NoTrueChildStrategy.RETURN_NULL_PREDICTION, targetType).isFinalLeaf(node);
        assertThat(KiePMMLTreeModelNodeASTFactory.factory(new HashMap<>(), Collections.emptyList(), TreeModel.NoTrueChildStrategy.RETURN_NULL_PREDICTION, targetType).isFinalLeaf(node)).isTrue();
        node = new ClassifierNode();
        assertThat(KiePMMLTreeModelNodeASTFactory.factory(new HashMap<>(), Collections.emptyList(), TreeModel.NoTrueChildStrategy.RETURN_NULL_PREDICTION, targetType).isFinalLeaf(node)).isTrue();
        node.addNodes(new LeafNode());
        assertThat(KiePMMLTreeModelNodeASTFactory.factory(new HashMap<>(), Collections.emptyList(), TreeModel.NoTrueChildStrategy.RETURN_NULL_PREDICTION, targetType).isFinalLeaf(node)).isFalse();
    }
}