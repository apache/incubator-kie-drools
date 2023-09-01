package org.kie.pmml.api.enums;

import java.util.Arrays;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_CompoundPredicate>CompoundPredicate</a>
 */
public enum BOOLEAN_OPERATOR implements Named {

    OR("or", "||"),
    AND("and", "&&"),
    XOR("xor", "^"),
    SURROGATE("surrogate", "surrogate");

    private String name;
    private String customOperator;

    BOOLEAN_OPERATOR(String name, String customOperator) {
        this.name = name;
        this.customOperator = customOperator;
    }

    public static BOOLEAN_OPERATOR byName(String name) {
        return Arrays.stream(BOOLEAN_OPERATOR.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find BOOLEAN_OPERATOR with name: " + name));
    }

    public String getName() {
        return name;
    }

    public String getCustomOperator() {
        return customOperator;
    }
}
