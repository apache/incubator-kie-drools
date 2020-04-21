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

package org.kie.pmml.models.drools.scorecard.drools.compiler.factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.dmg.pmml.PMML;
import org.dmg.pmml.scorecard.Characteristics;
import org.dmg.pmml.scorecard.Scorecard;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.commons.model.enums.DATA_TYPE;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.drools.ast.factories.KiePMMLDataDictionaryASTFactory;
import org.kie.pmml.models.drools.scorecard.compiler.factories.KiePMMLScorecardModelCharacteristicASTFactory;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFieldType;

public class KiePMMLScorecardModelCharacteristicASTFactoryTest {

    private static final String SOURCE_SAMPLE = "ScorecardSample.pmml";
    private PMML samplePmml;
    private Scorecard scorecardModel;

    @Before
    public void setUp() throws Exception {
        samplePmml = TestUtils.loadFromFile(SOURCE_SAMPLE);
        assertNotNull(samplePmml);
        assertEquals(1, samplePmml.getModels().size());
        assertTrue(samplePmml.getModels().get(0) instanceof Scorecard);
        scorecardModel = ((Scorecard) samplePmml.getModels().get(0));
    }

    @Test
    public void declareRulesFromCharacteristics() {
        Characteristics characteristics = scorecardModel.getCharacteristics();
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        DATA_TYPE targetType = getTargetFieldType(samplePmml.getDataDictionary(), scorecardModel);
        KiePMMLDataDictionaryASTFactory.factory(fieldTypeMap).declareTypes(samplePmml.getDataDictionary());
        KiePMMLScorecardModelCharacteristicASTFactory.factory(fieldTypeMap, Collections.emptyList(), targetType).declareRulesFromCharacteristics(characteristics, "_will");
        assertFalse(fieldTypeMap.isEmpty());
    }
}