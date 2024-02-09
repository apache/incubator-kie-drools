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
package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.List;

import org.dmg.pmml.DataField;
import org.dmg.pmml.Field;
import org.dmg.pmml.MiningField;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.FIELD_USAGE_TYPE;
import org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.MISSING_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.commons.model.KiePMMLMiningField;
import org.kie.pmml.commons.model.expressions.KiePMMLInterval;

import static org.kie.pmml.compiler.api.utils.ModelUtils.convertDataFieldValues;
import static org.kie.pmml.compiler.commons.factories.KiePMMLIntervalInstanceFactory.getKiePMMLIntervals;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLMiningField</code> instance
 * out of <code>MiningField</code>s
 */
public class KiePMMLMiningFieldInstanceFactory {

    private KiePMMLMiningFieldInstanceFactory() {
    }

    public static KiePMMLMiningField getKiePMMLMiningField(final MiningField toConvert, final Field<?> field) {
        String name = toConvert.getName() != null ?toConvert.getName() : "" + toConvert.hashCode();
        final FIELD_USAGE_TYPE fieldUsageType = toConvert.getUsageType() != null ?
                FIELD_USAGE_TYPE.byName(toConvert.getUsageType().value()) : null;
        final OP_TYPE opType = toConvert.getOpType() != null ? OP_TYPE.byName(toConvert.getOpType().value()) : null;
        final DATA_TYPE dataType = field.getDataType() != null ?
                DATA_TYPE.byName(field.getDataType().value()) : null;
        final MISSING_VALUE_TREATMENT_METHOD missingValueTreatmentMethod =
                toConvert.getMissingValueTreatment() != null ?
                        MISSING_VALUE_TREATMENT_METHOD.byName(toConvert.getMissingValueTreatment().value()) : null;
        final INVALID_VALUE_TREATMENT_METHOD invalidValueTreatmentMethod =
                toConvert.getInvalidValueTreatment() != null ?
                        INVALID_VALUE_TREATMENT_METHOD.byName(toConvert.getInvalidValueTreatment().value()) : null;
        final String missingValueReplacement = toConvert.getMissingValueReplacement() != null ?
                toConvert.getMissingValueReplacement().toString() : null;
        final String invalidValueReplacement = toConvert.getInvalidValueReplacement() != null ?
                toConvert.getInvalidValueReplacement().toString() : null;
        final List<String> allowedValues = field instanceof DataField ?
                convertDataFieldValues(((DataField) field).getValues()) : Collections.emptyList();
        final List<KiePMMLInterval> intervals = field instanceof DataField ?
                getKiePMMLIntervals(((DataField) field).getIntervals()) :
                Collections.emptyList();

        final KiePMMLMiningField.Builder builder = KiePMMLMiningField.builder(name, Collections.emptyList())
                .withFieldUsageType(fieldUsageType)
                .withOpType(opType)
                .withDataType(dataType)
                .withMissingValueTreatmentMethod(missingValueTreatmentMethod)
                .withInvalidValueTreatmentMethod(invalidValueTreatmentMethod)
                .withMissingValueReplacement(missingValueReplacement)
                .withInvalidValueReplacement(invalidValueReplacement)
                .withAllowedValues(allowedValues)
                .withIntervals(intervals);
        return builder.build();
    }

}
