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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.PMML;
import org.dmg.pmml.tree.TreeModel;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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

    @Before
    public void setUp() throws Exception {
        golfingPmml = TestUtils.loadFromFile(SOURCE_GOLFING);
        assertNotNull(golfingPmml);
        assertEquals(1, golfingPmml.getModels().size());
        assertTrue(golfingPmml.getModels().get(0) instanceof TreeModel);
        golfingModel = ((TreeModel) golfingPmml.getModels().get(0));
        //
        irisPmml = TestUtils.loadFromFile(SOURCE_IRIS);
        assertNotNull(irisPmml);
        assertEquals(1, irisPmml.getModels().size());
        assertTrue(irisPmml.getModels().get(0) instanceof TreeModel);
        irisModel = ((TreeModel) irisPmml.getModels().get(0));
        //
        simpleSetPmml = TestUtils.loadFromFile(SOURCE_SIMPLESET);
        assertNotNull(simpleSetPmml);
        assertEquals(1, simpleSetPmml.getModels().size());
        assertTrue(simpleSetPmml.getModels().get(0) instanceof TreeModel);
        simpleSetModel = ((TreeModel) simpleSetPmml.getModels().get(0));
    }

    @Test
    public void getKiePMMLDroolsGolfingAST() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = getFieldTypeMap(golfingPmml.getDataDictionary(), golfingPmml.getTransformationDictionary(),  golfingModel.getLocalTransformations());
        List<KiePMMLDroolsType> types = Collections.emptyList();
        KiePMMLDroolsAST retrieved = KiePMMLTreeModelASTFactory.getKiePMMLDroolsAST(golfingPmml.getDataDictionary(), golfingModel, fieldTypeMap, types);
        assertNotNull(retrieved);
        assertEquals(types, retrieved.getTypes());
        assertFalse(retrieved.getRules().isEmpty());
    }

    @Test
    public void getKiePMMLDroolsIrisAST() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = getFieldTypeMap(irisPmml.getDataDictionary(), irisPmml.getTransformationDictionary(),  irisModel.getLocalTransformations());
        List<KiePMMLDroolsType> types = Collections.emptyList();
        KiePMMLDroolsAST retrieved = KiePMMLTreeModelASTFactory.getKiePMMLDroolsAST(irisPmml.getDataDictionary(), irisModel, fieldTypeMap, types);
        assertNotNull(retrieved);
        assertEquals(types, retrieved.getTypes());
        assertFalse(retrieved.getRules().isEmpty());
    }

    @Test
    public void getKiePMMLDroolsSimpleSetAST() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = getFieldTypeMap(simpleSetPmml.getDataDictionary(), simpleSetPmml.getTransformationDictionary(),  simpleSetModel.getLocalTransformations());
        List<KiePMMLDroolsType> types = Collections.emptyList();
        KiePMMLDroolsAST retrieved = KiePMMLTreeModelASTFactory.getKiePMMLDroolsAST(simpleSetPmml.getDataDictionary(), simpleSetModel, fieldTypeMap, types);
        assertNotNull(retrieved);
        assertEquals(types, retrieved.getTypes());
        assertFalse(retrieved.getRules().isEmpty());
    }
}