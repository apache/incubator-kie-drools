package org.kie.pmml.api.enums;

import java.util.Arrays;
import java.util.Objects;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/GeneralStructure.html#xsdType_MINING-FUNCTION>MINING-FUNCTION</a>
 */
public enum MINING_FUNCTION implements Named {

    ASSOCIATION_RULES("associationRules"),
    SEQUENCES("sequences"),
    CLASSIFICATION("classification"),
    REGRESSION("regression"),
    CLUSTERING("clustering"),
    TIME_SERIES("timeSeries"),
    MIXED("mixed");

    private String name;

    MINING_FUNCTION(String name) {
        this.name = name;
    }

    public static MINING_FUNCTION byName(String name) {
        return Arrays.stream(MINING_FUNCTION.values()).filter(value -> Objects.equals(name, value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find MINING_FUNCTION with name: " + name));
    }

    public String getName() {
        return name;
    }
}
