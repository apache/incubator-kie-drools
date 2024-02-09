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
package org.kie.pmml.compiler.commons.utils;

import java.util.List;
import java.util.Optional;

import org.dmg.pmml.Field;
import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameOpType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.kie.pmml.api.enums.OP_TYPE.CATEGORICAL;
import static org.kie.pmml.api.enums.OP_TYPE.CONTINUOUS;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getFieldsFromDataDictionary;
import static org.kie.pmml.compiler.api.utils.ModelUtils.getOpType;
import static org.kie.pmml.compiler.api.utils.ModelUtils.getTargetFieldName;
import static org.kie.pmml.compiler.api.utils.ModelUtils.getTargetFields;
import static org.kie.pmml.compiler.commons.utils.KiePMMLUtil.MODELNAME_TEMPLATE;
import static org.drools.util.FileUtils.getFileInputStream;

public class KiePMMLLoadedModelUtilsTest {

    private static final String NO_TARGET_SOURCE = "NoTargetFieldSample.pmml";
    private static final String ONE_MINING_TARGET_SOURCE = "OneMiningTargetFieldSample.pmml";
    private static final String MULTIPLE_TARGETS_SOURCE = "MultipleTargetsFieldSample.pmml";
    private static final String NO_MODELNAME_SAMPLE_NAME = "NoModelNameSample";
    private static final String NO_MODELNAME_SAMPLE = NO_MODELNAME_SAMPLE_NAME + ".pmml";
    private static final String WHAT_I_DO_TARGET_FIELD = "whatIdo";
    private static final String CAR_LOCATION_FIELD = "car_location";
    private static final String NUMBER_OF_CLAIMS_FIELD = "number_of_claims";
    private static final String SALARY_FIELD = "salary";
    private static final String OUTLOOK_FIELD = "outlook";
    private static final String TEMPERATURE_FIELD = "temperature";
    private static final String NOT_EXISTING_FIELD = "not_existing";
    private PMML pmmlModel;

    @Test
    void getTargetFieldNoTarget() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(NO_TARGET_SOURCE), NO_TARGET_SOURCE);
        final List<Field<?>> fields = getFieldsFromDataDictionary(pmmlModel.getDataDictionary());
        assertThat(getTargetFieldName(fields, pmmlModel.getModels().get(0))).isPresent();
        assertThat(getTargetFields(fields, pmmlModel.getModels().get(0))).isNotEmpty();
    }

    @Test
    void getTargetFieldOneMiningTarget() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(ONE_MINING_TARGET_SOURCE), ONE_MINING_TARGET_SOURCE);
        final List<Field<?>> fields = getFieldsFromDataDictionary(pmmlModel.getDataDictionary());
        final Optional<String> retrieved = getTargetFieldName(fields, pmmlModel.getModels().get(0));
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(WHAT_I_DO_TARGET_FIELD);
        final List<KiePMMLNameOpType> retrieveds = getTargetFields(fields, pmmlModel.getModels().get(0));
        assertThat(retrieveds).hasSize(1);
        assertThat(retrieveds.get(0).getName()).isEqualTo(WHAT_I_DO_TARGET_FIELD);
    }

    @Test
    void getTargetFieldMultipleTargets() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(MULTIPLE_TARGETS_SOURCE), MULTIPLE_TARGETS_SOURCE);
        final List<Field<?>> fields = getFieldsFromDataDictionary(pmmlModel.getDataDictionary());
        final Optional<String> retrieved = getTargetFieldName(fields, pmmlModel.getModels().get(0));
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(CAR_LOCATION_FIELD);
        final List<KiePMMLNameOpType> retrieveds = getTargetFields(fields, pmmlModel.getModels().get(0));
        assertThat(retrieveds).hasSize(1);
        assertThat(retrieveds.get(0).getName()).isEqualTo(CAR_LOCATION_FIELD);
    }

    @Test
    void getOpTypeFromDataFieldExisting() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(NO_TARGET_SOURCE), NO_TARGET_SOURCE);
        final OP_TYPE retrieved = getOpType(getFieldsFromDataDictionary(pmmlModel.getDataDictionary()), pmmlModel.getModels().get(0), TEMPERATURE_FIELD);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(CONTINUOUS);
    }

    @Test
    void getOpTypeFromMiningFieldExisting() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(ONE_MINING_TARGET_SOURCE), ONE_MINING_TARGET_SOURCE);
        final OP_TYPE retrieved = getOpType(getFieldsFromDataDictionary(pmmlModel.getDataDictionary()), pmmlModel.getModels().get(0), OUTLOOK_FIELD);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(CATEGORICAL);
    }

    @Test
    void getOpTypeNotExistingField() throws Exception {
        assertThatExceptionOfType(KiePMMLInternalException.class).isThrownBy(() -> {
            pmmlModel = KiePMMLUtil.load(getFileInputStream(ONE_MINING_TARGET_SOURCE), ONE_MINING_TARGET_SOURCE);
            getOpType(getFieldsFromDataDictionary(pmmlModel.getDataDictionary()), pmmlModel.getModels().get(0), NOT_EXISTING_FIELD);
        });
    }

    @Test
    void populateMissingModelNames() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(NO_MODELNAME_SAMPLE), NO_MODELNAME_SAMPLE);
        final List<Model> models = pmmlModel.getModels();
        for (int i = 0; i < models.size(); i++) {
            Model model = models.get(i);
            assertThat(model.getModelName()).isNotNull();
            assertThat(model.getModelName()).isNotEmpty();
            String expected = String.format(MODELNAME_TEMPLATE,
                    NO_MODELNAME_SAMPLE_NAME,
                    model.getClass().getSimpleName(),
                    i);
            assertThat(model.getModelName()).isEqualTo(expected);
        }

    }
}