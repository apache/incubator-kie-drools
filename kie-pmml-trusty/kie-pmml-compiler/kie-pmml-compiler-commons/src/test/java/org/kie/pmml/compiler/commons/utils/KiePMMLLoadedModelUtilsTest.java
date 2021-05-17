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

package org.kie.pmml.compiler.commons.utils;

import java.util.List;
import java.util.Optional;

import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.commons.model.tuples.KiePMMLNameOpType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.api.enums.OP_TYPE.CATEGORICAL;
import static org.kie.pmml.api.enums.OP_TYPE.CONTINUOUS;
import static org.kie.pmml.compiler.commons.utils.KiePMMLUtil.MODELNAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getOpType;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFieldName;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFields;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

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
    public void getTargetFieldNoTarget() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(NO_TARGET_SOURCE), NO_TARGET_SOURCE);
        assertTrue(getTargetFieldName(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0)).isPresent());
        assertFalse(getTargetFields(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0)).isEmpty());
    }

    @Test
    public void getTargetFieldOneMiningTarget() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(ONE_MINING_TARGET_SOURCE), ONE_MINING_TARGET_SOURCE);
        final Optional<String> retrieved = getTargetFieldName(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0));
        assertTrue(retrieved.isPresent());
        assertEquals(WHAT_I_DO_TARGET_FIELD, retrieved.get());
        final List<KiePMMLNameOpType> retrieveds = getTargetFields(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0));
        assertEquals(1, retrieveds.size());
        assertEquals(WHAT_I_DO_TARGET_FIELD, retrieveds.get(0).getName());
    }

    @Test
    public void getTargetFieldMultipleTargets() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(MULTIPLE_TARGETS_SOURCE), MULTIPLE_TARGETS_SOURCE);
        final Optional<String> retrieved = getTargetFieldName(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0));
        assertTrue(retrieved.isPresent());
        assertEquals(CAR_LOCATION_FIELD, retrieved.get());
        final List<KiePMMLNameOpType> retrieveds = getTargetFields(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0));
        assertEquals(1, retrieveds.size());
        assertEquals(CAR_LOCATION_FIELD, retrieveds.get(0).getName());
    }

    @Test
    public void getOpTypeFromDataFieldExisting() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(NO_TARGET_SOURCE), NO_TARGET_SOURCE);
        final OP_TYPE retrieved = getOpType(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0), TEMPERATURE_FIELD);
        assertNotNull(retrieved);
        assertEquals(CONTINUOUS, retrieved);
    }

    @Test
    public void getOpTypeFromMiningFieldExisting() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(ONE_MINING_TARGET_SOURCE), ONE_MINING_TARGET_SOURCE);
        final OP_TYPE retrieved = getOpType(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0), OUTLOOK_FIELD);
        assertNotNull(retrieved);
        assertEquals(CATEGORICAL, retrieved);
    }

    @Test(expected = KiePMMLInternalException.class)
    public void getOpTypeNotExistingField() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(ONE_MINING_TARGET_SOURCE), ONE_MINING_TARGET_SOURCE);
        getOpType(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0), NOT_EXISTING_FIELD);
    }

    @Test
    public void populateMissingModelNames() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(NO_MODELNAME_SAMPLE), NO_MODELNAME_SAMPLE);
        final List<Model> models = pmmlModel.getModels();
        for (int i = 0; i < models.size(); i ++) {
            Model model = models.get(i);
            assertNotNull(model.getModelName());
            assertFalse(model.getModelName().isEmpty());
            String expected = String.format(MODELNAME_TEMPLATE,
                                            NO_MODELNAME_SAMPLE_NAME,
                                            model.getClass().getSimpleName(),
                                            i);
            assertEquals(expected, model.getModelName());
        }

    }
}