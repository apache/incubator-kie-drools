package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.dmg.pmml.Target;
import org.kie.pmml.api.enums.CAST_INTEGER;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.models.TargetField;
import org.kie.pmml.api.models.TargetValue;
import org.kie.pmml.commons.model.KiePMMLTarget;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLTarget</code> instance
 * out of <code>Target</code>s
 */
public class KiePMMLTargetInstanceFactory {

    private KiePMMLTargetInstanceFactory() {
        // Avoid instantiation
    }

    public static KiePMMLTarget getKiePMMLTarget(final Target target) {
        final List<TargetValue> targetValues = target.hasTargetValues() ? target.getTargetValues()
                .stream()
                .map(KiePMMLTargetInstanceFactory::getKieTargetValue)
                .collect(Collectors.toList()) : Collections.emptyList();
        final OP_TYPE opType = target.getOpType() != null ? OP_TYPE.byName(target.getOpType().value()) : null;
        final String field = target.getField() != null ? target.getField().getValue() : null;
        final CAST_INTEGER castInteger = target.getCastInteger() != null ?
                CAST_INTEGER.byName(target.getCastInteger().value()) : null;
        TargetField targetField = new TargetField(targetValues,
                                                  opType,
                                                  field,
                                                  castInteger,
                                                  target.getMin(),
                                                  target.getMax(),
                                                  target.getRescaleConstant(),
                                                  target.getRescaleFactor());
        final KiePMMLTarget.Builder builder = KiePMMLTarget.builder(targetField.getName(), Collections.emptyList(),
                                                                    targetField);
        return builder.build();
    }

    private static TargetValue getKieTargetValue(org.dmg.pmml.TargetValue source) {
        final String value = source.getValue() != null ? source.getValue().toString() : null;
        final String displayValue = source.getDisplayValue() != null ? source.getDisplayValue() : null;
        return new TargetValue(value, displayValue, source.getPriorProbability(), source.getDefaultValue());
    }
}
