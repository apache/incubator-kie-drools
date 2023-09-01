package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;

import org.dmg.pmml.TargetValue;
import org.kie.pmml.commons.model.KiePMMLTargetValue;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLTargetValue</code> instance
 * out of <code>KiePMMLTargetValue</code>s
 */
public class KiePMMLTargetValueInstanceFactory {

    private KiePMMLTargetValueInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLTargetValue getKiePMMLTargetValue(final TargetValue targetValue) {
        final String value = targetValue.getValue() != null ? targetValue.getValue().toString() : null;
        final String displayValue = targetValue.getDisplayValue() != null ? targetValue.getDisplayValue() : null;
        final org.kie.pmml.api.models.TargetValue kieTargetValue = new org.kie.pmml.api.models.TargetValue(value,
                                                                                                           displayValue,
                                                                                                           targetValue.getPriorProbability(),
                                                                                                           targetValue.getDefaultValue());
        return KiePMMLTargetValue.builder(kieTargetValue.getName(),
                                          Collections.emptyList(), kieTargetValue)
                .build();
    }
}
