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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.PMML;
import org.dmg.pmml.tree.TreeModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.compiler.api.testutils.TestUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getFieldsFromDataDictionary;
import static org.kie.pmml.models.drools.utils.KiePMMLASTTestUtils.getFieldTypeMap;

public class KiePMMLTreeModelASTFactoryTest {

    private static final String SOURCE_GOLFING = "TreeSample.pmml";
    private static final String SOURCE_IRIS = "irisTree.pmml";
    private static final String SOURCE_SIMPLESET = "SimpleSetPredicateTree.pmml";
    private PMML golfingPmml;
    private TreeModel golfingModel;
    private PMML irisPmml;
    private TreeModel irisModel;
    private PMML simpleSetPmml;
    private TreeModel simpleSetModel;

    @BeforeEach
    public void setUp() throws Exception {
        golfingPmml = TestUtils.loadFromFile(SOURCE_GOLFING);
        assertThat(golfingPmml).isNotNull();
        assertThat(golfingPmml.getModels()).hasSize(1);
        assertThat(golfingPmml.getModels().get(0)).isInstanceOf(TreeModel.class);
        golfingModel = ((TreeModel) golfingPmml.getModels().get(0));
        //
        irisPmml = TestUtils.loadFromFile(SOURCE_IRIS);
        assertThat(irisPmml).isNotNull();
        assertThat(irisPmml.getModels()).hasSize(1);
        assertThat(irisPmml.getModels().get(0)).isInstanceOf(TreeModel.class);
        irisModel = ((TreeModel) irisPmml.getModels().get(0));
        //
        simpleSetPmml = TestUtils.loadFromFile(SOURCE_SIMPLESET);
        assertThat(simpleSetPmml).isNotNull();
        assertThat(simpleSetPmml.getModels()).hasSize(1);
        assertThat(simpleSetPmml.getModels().get(0)).isInstanceOf(TreeModel.class);
        simpleSetModel = ((TreeModel) simpleSetPmml.getModels().get(0));
    }

    @Test
    void getKiePMMLDroolsGolfingAST() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = getFieldTypeMap(golfingPmml.getDataDictionary(), golfingPmml.getTransformationDictionary(),  golfingModel.getLocalTransformations());
        List<KiePMMLDroolsType> types = Collections.emptyList();
        KiePMMLDroolsAST retrieved = KiePMMLTreeModelASTFactory.getKiePMMLDroolsAST(getFieldsFromDataDictionary(golfingPmml.getDataDictionary()), golfingModel, fieldTypeMap, types);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getTypes()).isEqualTo(types);
        assertThat(retrieved.getRules()).isNotEmpty();
    }

    @Test
    void getKiePMMLDroolsIrisAST() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = getFieldTypeMap(irisPmml.getDataDictionary(), irisPmml.getTransformationDictionary(),  irisModel.getLocalTransformations());
        List<KiePMMLDroolsType> types = Collections.emptyList();
        KiePMMLDroolsAST retrieved = KiePMMLTreeModelASTFactory.getKiePMMLDroolsAST(getFieldsFromDataDictionary(irisPmml.getDataDictionary()), irisModel, fieldTypeMap, types);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getTypes()).isEqualTo(types);
        assertThat(retrieved.getRules()).isNotEmpty();
    }

    @Test
    void getKiePMMLDroolsSimpleSetAST() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = getFieldTypeMap(simpleSetPmml.getDataDictionary(), simpleSetPmml.getTransformationDictionary(),  simpleSetModel.getLocalTransformations());
        List<KiePMMLDroolsType> types = Collections.emptyList();
        KiePMMLDroolsAST retrieved = KiePMMLTreeModelASTFactory.getKiePMMLDroolsAST(getFieldsFromDataDictionary(simpleSetPmml.getDataDictionary()), simpleSetModel, fieldTypeMap, types);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getTypes()).isEqualTo(types);
        assertThat(retrieved.getRules()).isNotEmpty();
    }
}