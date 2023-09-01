package org.kie.pmml.api.enums;

import java.util.Arrays;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_SimpleSetPredicate>SimpleSetPredicate</a>
 */
public enum IN_NOTIN implements Named {

    IN("isIn"),
    NOT_IN("isNotIn");

    private final String name;

    IN_NOTIN(String name) {
        this.name = name;
    }

    public static IN_NOTIN byName(String name) {
        return Arrays.stream(IN_NOTIN.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find IN_NOTIN with name: " + name));
    }

    public String getName() {
        return name;
    }

}
