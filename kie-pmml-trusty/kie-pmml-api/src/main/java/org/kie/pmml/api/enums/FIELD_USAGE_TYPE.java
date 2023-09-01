package org.kie.pmml.api.enums;

import java.util.Arrays;
import java.util.Objects;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/MiningSchema.html#xsdType_FIELD-USAGE-TYPE>FIELD-USAGE-TYPE</a>
 */
public enum FIELD_USAGE_TYPE implements Named {

    ACTIVE("active"),
    PREDICTED("predicted"),
    TARGET("target"),
    SUPPLEMENTARY("supplementary"),
    GROUP("group"),
    ORDER("order"),
    FREQUENCY_WEIGHT("frequencyWeight"),
    ANALYSIS_WEIGHT("analysisWeight");

    private String name;

    FIELD_USAGE_TYPE(String name) {
        this.name = name;
    }

    public static FIELD_USAGE_TYPE byName(String name) {
        return Arrays.stream(FIELD_USAGE_TYPE.values()).filter(value -> Objects.equals(name, value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find FIELD_USAGE_TYPE with name: " + name));
    }

    public String getName() {
        return name;
    }
}
