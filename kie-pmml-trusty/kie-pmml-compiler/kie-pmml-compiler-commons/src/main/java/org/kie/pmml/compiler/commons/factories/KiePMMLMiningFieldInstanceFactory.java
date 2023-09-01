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
        String name = toConvert.getName() != null ? toConvert.getName().getValue() : "" + toConvert.hashCode();
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
