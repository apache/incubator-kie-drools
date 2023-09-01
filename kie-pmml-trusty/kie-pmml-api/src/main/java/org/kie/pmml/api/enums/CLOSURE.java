package org.kie.pmml.api.enums;

import java.util.Arrays;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4-1/DataDictionary.html#xsdElement_Interval>Interval</a>
 */
public enum CLOSURE implements Named {

    OPEN_CLOSED("openClosed"),
    OPEN_OPEN("openOpen"),
    CLOSED_OPEN("closedOpen"),
    CLOSED_CLOSED("closedClosed");

    private final String name;

    CLOSURE(String name) {
        this.name = name;
    }

    public static CLOSURE byName(String name) {
        return Arrays.stream(CLOSURE.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find CLOSURE with name: " + name));
    }

    public String getName() {
        return name;
    }
}
