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
package org.kie.pmml.models.drools.scorecard.compiler.factories;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.PMML;
import org.dmg.pmml.scorecard.Scorecard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.compiler.api.testutils.TestUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getFieldsFromDataDictionary;
import static org.kie.pmml.models.drools.utils.KiePMMLASTTestUtils.getFieldTypeMap;

public class KiePMMLScorecardModelASTFactoryTest {

    private static final String SOURCE_SAMPLE = "ScorecardSample.pmml";
    private PMML samplePmml;
    private Scorecard scorecardModel;

    @BeforeEach
    public void setUp() throws Exception {
        samplePmml = TestUtils.loadFromFile(SOURCE_SAMPLE);
        assertThat(samplePmml).isNotNull();
        assertThat(samplePmml.getModels()).hasSize(1);
        assertThat(samplePmml.getModels().get(0)).isInstanceOf(Scorecard.class);
        scorecardModel = ((Scorecard) samplePmml.getModels().get(0));
    }

    @Test
    void getKiePMMLDroolsSampleAST() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = getFieldTypeMap(samplePmml.getDataDictionary(), samplePmml.getTransformationDictionary(),  scorecardModel.getLocalTransformations());
        List<KiePMMLDroolsType> types = Collections.emptyList();
        KiePMMLDroolsAST retrieved = KiePMMLScorecardModelASTFactory.getKiePMMLDroolsAST(getFieldsFromDataDictionary(samplePmml.getDataDictionary()), scorecardModel, fieldTypeMap, types);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getTypes()).isEqualTo(types);
        assertThat(retrieved.getRules()).isNotEmpty();
    }

}