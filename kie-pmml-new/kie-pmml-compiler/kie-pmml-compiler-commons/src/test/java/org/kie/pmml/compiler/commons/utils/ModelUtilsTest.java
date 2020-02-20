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

import org.dmg.pmml.PMML;
import org.junit.Test;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.enums.OP_TYPE;
import org.kie.pmml.commons.model.tuples.KiePMMLNameOpType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.model.enums.OP_TYPE.CATEGORICAL;
import static org.kie.pmml.commons.model.enums.OP_TYPE.CONTINUOUS;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getOpType;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetField;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFields;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

public class ModelUtilsTest {

    private static final String NO_TARGET_SOURCE = "NoTargetFieldSample.pmml";
    private static final String ONE_MINING_TARGET_SOURCE = "OneMiningTargetFieldSample.pmml";
    private static final String MULTIPLE_TARGETS_SOURCE = "MultipleTargetsFieldSample.pmml";
    private static final String WHAT_I_DO_TARGET_FIELD = "whatIdo";
    private static final String NUMBER_OF_CLAIMS_FIELD = "number_of_claims";
    private static final String SALARY_FIELD = "salary";
    private static final String OUTLOOK_FIELD = "outlook";
    private static final String TEMPERATURE_FIELD = "temperature";
    private static final String NOT_EXISTING_FIELD = "not_existing";
    private PMML pmmlModel;

    @Test
    public void getTargetFieldNoTarget() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(NO_TARGET_SOURCE));
        assertFalse(getTargetField(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0)).isPresent());
        assertTrue(getTargetFields(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0)).isEmpty());
    }

    @Test
    public void getTargetFieldOneMiningTarget() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(ONE_MINING_TARGET_SOURCE));
        final Optional<String> retrieved = getTargetField(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0));
        assertTrue(retrieved.isPresent());
        assertEquals(WHAT_I_DO_TARGET_FIELD, retrieved.get());
        final List<KiePMMLNameOpType> retrieveds = getTargetFields(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0));
        assertEquals(1, retrieveds.size());
        assertEquals(WHAT_I_DO_TARGET_FIELD, retrieveds.get(0).getName());
    }

    @Test
    public void getTargetFieldMultipleTargets() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(MULTIPLE_TARGETS_SOURCE));
        final Optional<String> retrieved = getTargetField(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0));
        assertTrue(retrieved.isPresent());
        assertEquals(NUMBER_OF_CLAIMS_FIELD, retrieved.get());
        final List<KiePMMLNameOpType> retrieveds = getTargetFields(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0));
        assertEquals(2, retrieveds.size());
        assertEquals(NUMBER_OF_CLAIMS_FIELD, retrieveds.get(0).getName());
        assertEquals(SALARY_FIELD, retrieveds.get(1).getName());
    }

    @Test
    public void getOpTypeFromDataFieldExisting() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(NO_TARGET_SOURCE));
        final OP_TYPE retrieved = getOpType(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0), TEMPERATURE_FIELD);
        assertNotNull(retrieved);
        assertEquals(CONTINUOUS, retrieved);
    }

    @Test
    public void getOpTypeFromMiningFieldExisting() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(ONE_MINING_TARGET_SOURCE));
        final OP_TYPE retrieved = getOpType(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0), OUTLOOK_FIELD);
        assertNotNull(retrieved);
        assertEquals(CATEGORICAL, retrieved);
    }

    @Test(expected = KiePMMLInternalException.class)
    public void getOpTypeNotExistingField() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(ONE_MINING_TARGET_SOURCE));
        getOpType(pmmlModel.getDataDictionary(), pmmlModel.getModels().get(0), NOT_EXISTING_FIELD);
    }
}