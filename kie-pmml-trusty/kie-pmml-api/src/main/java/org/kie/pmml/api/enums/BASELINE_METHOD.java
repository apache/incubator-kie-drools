package org.kie.pmml.api.enums;

import java.util.Arrays;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/Scorecard.html#baselinemethod>baselinemethod</a>
 */
public enum BASELINE_METHOD implements Named {

    MAX("max"),
    MIN("min"),
    MEAN("mean"),
    NEUTRAL("neutral"),
    OTHER("other");

    private String name;

    BASELINE_METHOD(String name) {
        this.name = name;
    }

    public static BASELINE_METHOD byName(String name) {
        return Arrays.stream(BASELINE_METHOD.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find BASELINE_METHOD with name: " + name));
    }

    public String getName() {
        return name;
    }

}
