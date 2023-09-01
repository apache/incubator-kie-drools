package org.kie.pmml.api.enums;

import java.util.Arrays;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/Scorecard.html#reasoncodealgorithm>reasonCodeAlgorithm</a>
 */
public enum REASONCODE_ALGORITHM implements Named {

    POINTS_ABOVE("pointsAbove"),
    POINTS_BELOW("pointsBelow");

    private String name;

    REASONCODE_ALGORITHM(String name) {
        this.name = name;
    }

    public static REASONCODE_ALGORITHM byName(String name) {
        return Arrays.stream(REASONCODE_ALGORITHM.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find REASONCODE_ALGORITHM with name: " + name));
    }

    public String getName() {
        return name;
    }
}
