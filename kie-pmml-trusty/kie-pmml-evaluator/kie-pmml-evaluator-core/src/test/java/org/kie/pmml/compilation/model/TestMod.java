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
package org.kie.pmml.compilation.model;

import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.commons.model.KiePMMLMiningField;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.KiePMMLTarget;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.commons.transformations.KiePMMLLocalTransformations;
import org.kie.pmml.commons.transformations.KiePMMLTransformationDictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestMod {

    public static KiePMMLTestingModel getModel() {
        KiePMMLTestingModel toReturn = KiePMMLTestingModel.builder("FileName", "TestMod", Collections.emptyList(), org.kie.pmml.api.enums.MINING_FUNCTION.REGRESSION).withTargetField("fld4").withMiningFields(getCreatedMiningFields()).withOutputFields(getCreatedOutputFields()).withKiePMMLMiningFields(getCreatedKiePMMLMiningFields()).withKiePMMLOutputFields(getCreatedKiePMMLOutputFields()).withKiePMMLTargets(getCreatedKiePMMLTargets()).withKiePMMLTransformationDictionary(getCreatedTransformationDictionary()).withKiePMMLLocalTransformations(getCreatedLocalTransformations()).build();
        return toReturn;
    }

    private static List<MiningField> getCreatedMiningFields() {
        List<MiningField> toReturn = new ArrayList();
        toReturn.add(new org.kie.pmml.api.models.MiningField("fld1", org.kie.pmml.api.enums.FIELD_USAGE_TYPE.ACTIVE, null, org.kie.pmml.api.enums.DATA_TYPE.DOUBLE, null, org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD.RETURN_INVALID, null, null, List.of(), List.of()));
        toReturn.add(new org.kie.pmml.api.models.MiningField("fld2", org.kie.pmml.api.enums.FIELD_USAGE_TYPE.ACTIVE, null, org.kie.pmml.api.enums.DATA_TYPE.DOUBLE, null, org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD.RETURN_INVALID, null, null, List.of(), List.of()));
        toReturn.add(new org.kie.pmml.api.models.MiningField("fld3", org.kie.pmml.api.enums.FIELD_USAGE_TYPE.ACTIVE, null, org.kie.pmml.api.enums.DATA_TYPE.STRING, null, org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD.RETURN_INVALID, null, null, java.util.Arrays.asList("x", "y"), List.of()));
        toReturn.add(new org.kie.pmml.api.models.MiningField("fld4", org.kie.pmml.api.enums.FIELD_USAGE_TYPE.PREDICTED, null, org.kie.pmml.api.enums.DATA_TYPE.DOUBLE, null, org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD.RETURN_INVALID, null, null, List.of(), List.of()));
        return toReturn;
    }

    private static List<OutputField> getCreatedOutputFields() {
        List<OutputField> toReturn = new ArrayList();
        toReturn.add(new org.kie.pmml.api.models.OutputField("result", null, org.kie.pmml.api.enums.DATA_TYPE.DOUBLE, "fld4", org.kie.pmml.api.enums.RESULT_FEATURE.PREDICTED_VALUE, List.of()));
        return toReturn;
    }

    private static List<KiePMMLMiningField> getCreatedKiePMMLMiningFields() {
        KiePMMLMiningField fld1 = KiePMMLMiningField.builder("fld1", Collections.emptyList()).withFieldUsageType(org.kie.pmml.api.enums.FIELD_USAGE_TYPE.ACTIVE).withOpType(null).withDataType(org.kie.pmml.api.enums.DATA_TYPE.DOUBLE).withMissingValueTreatmentMethod(null).withInvalidValueTreatmentMethod(org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD.RETURN_INVALID).withMissingValueReplacement(null).withInvalidValueReplacement(null).withAllowedValues(List.of()).withIntervals(List.of()).build();
        KiePMMLMiningField fld2 = KiePMMLMiningField.builder("fld2", Collections.emptyList()).withFieldUsageType(org.kie.pmml.api.enums.FIELD_USAGE_TYPE.ACTIVE).withOpType(null).withDataType(org.kie.pmml.api.enums.DATA_TYPE.DOUBLE).withMissingValueTreatmentMethod(null).withInvalidValueTreatmentMethod(org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD.RETURN_INVALID).withMissingValueReplacement(null).withInvalidValueReplacement(null).withAllowedValues(List.of()).withIntervals(List.of()).build();
        KiePMMLMiningField fld3 = KiePMMLMiningField.builder("fld3", Collections.emptyList()).withFieldUsageType(org.kie.pmml.api.enums.FIELD_USAGE_TYPE.ACTIVE).withOpType(null).withDataType(org.kie.pmml.api.enums.DATA_TYPE.STRING).withMissingValueTreatmentMethod(null).withInvalidValueTreatmentMethod(org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD.RETURN_INVALID).withMissingValueReplacement(null).withInvalidValueReplacement(null).withAllowedValues(Arrays.asList("x", "y")).withIntervals(List.of()).build();
        KiePMMLMiningField fld4 = KiePMMLMiningField.builder("fld4", Collections.emptyList()).withFieldUsageType(org.kie.pmml.api.enums.FIELD_USAGE_TYPE.PREDICTED).withOpType(null).withDataType(org.kie.pmml.api.enums.DATA_TYPE.DOUBLE).withMissingValueTreatmentMethod(null).withInvalidValueTreatmentMethod(org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD.RETURN_INVALID).withMissingValueReplacement(null).withInvalidValueReplacement(null).withAllowedValues(List.of()).withIntervals(List.of()).build();
        return Arrays.asList(fld1, fld2, fld3, fld4);
    }

    private static List<KiePMMLOutputField> getCreatedKiePMMLOutputFields() {
        KiePMMLOutputField result = KiePMMLOutputField.builder("result", Collections.emptyList()).withResultFeature(org.kie.pmml.api.enums.RESULT_FEATURE.PREDICTED_VALUE).withTargetField("fld4").withValue(null).withDataType(org.kie.pmml.api.enums.DATA_TYPE.DOUBLE).withRank(1).withKiePMMLExpression(null).build();
        return Collections.singletonList(result);
    }

    private static List<KiePMMLTarget> getCreatedKiePMMLTargets() {
        List<KiePMMLTarget> toReturn = new ArrayList();
        return toReturn;
    }

    private static KiePMMLTransformationDictionary getCreatedTransformationDictionary() {
        return null;
    }

    private static KiePMMLLocalTransformations getCreatedLocalTransformations() {
        return null;
    }
}
