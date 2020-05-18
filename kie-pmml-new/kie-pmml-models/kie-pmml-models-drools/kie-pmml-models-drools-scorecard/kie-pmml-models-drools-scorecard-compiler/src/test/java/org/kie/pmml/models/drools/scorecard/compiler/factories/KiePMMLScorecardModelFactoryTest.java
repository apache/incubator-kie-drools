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

package org.kie.pmml.models.drools.scorecard.compiler.factories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.PMML;
import org.dmg.pmml.scorecard.Scorecard;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.scorecard.model.KiePMMLScorecardModel;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class KiePMMLScorecardModelFactoryTest {

    private static final String SOURCE_1 = "ScorecardSample.pmml";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLScorecardModelFactoryTest.class);
    private static final String TARGET_FIELD = "overallScore";
    private static final MINING_FUNCTION _MINING_FUNCTION = MINING_FUNCTION.CLASSIFICATION;
    private PMML pmml;
    private Scorecard scorecardModel;

    @Before
    public void setUp() throws Exception {
        pmml = TestUtils.loadFromFile(SOURCE_1);
        assertNotNull(pmml);
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof Scorecard);
        scorecardModel = (Scorecard) pmml.getModels().get(0);
    }

    @Test
    public void getKiePMMLScorecardModel() throws Exception {
        final DataDictionary dataDictionary = pmml.getDataDictionary();
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        IntStream.range(0,3).forEach(index -> {
            fieldTypeMap.put("TYPE-"+ index, new KiePMMLOriginalTypeGeneratedType("Original-"+ index, "Generated-"+ index));
        });
        KiePMMLScorecardModel retrieved = KiePMMLScorecardModelFactory.getKiePMMLScorecardModel(dataDictionary, scorecardModel, fieldTypeMap);
        assertNotNull(retrieved);
        assertEquals(scorecardModel.getModelName(), retrieved.getName());
        assertEquals(TARGET_FIELD, retrieved.getTargetField());
    }

    @Test
    public void getKiePMMLDroolsAST() {
        final DataDictionary dataDictionary = pmml.getDataDictionary();
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        KiePMMLDroolsAST retrieved = KiePMMLScorecardModelFactory.getKiePMMLDroolsAST(dataDictionary, scorecardModel, fieldTypeMap);
        assertNotNull(retrieved);
        List<DataField> dataFields = dataDictionary.getDataFields();
        assertEquals(dataFields.size(), fieldTypeMap.size());
        dataFields.forEach(dataField -> assertTrue(fieldTypeMap.containsKey(dataField.getName().getValue())));
    }
}