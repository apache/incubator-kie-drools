package org.kie.pmml.api.enums;

import java.util.Arrays;

import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.api.exceptions.KiePMMLException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/TreeModel.html#xsdGroup_PREDICATE>PREDICATE</a>
 */
public enum ARRAY_TYPE implements Named {

    INT("int"),
    STRING("string"),
    REAL("real");

    private final String name;

    ARRAY_TYPE(String name) {
        this.name = name;
    }

    public static ARRAY_TYPE byName(String name) {
        return Arrays.stream(ARRAY_TYPE.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find ARRAY_TYPE with name: " + name));
    }

    public String getName() {
        return name;
    }

    public Object getValue(String rawValue) {
        switch (this) {
            case INT:
                return Integer.valueOf(rawValue);
            case STRING:
                return rawValue;
            case REAL:
                return Double.valueOf(rawValue);
            default:
                throw new KiePMMLException("Unknown ARRAY_TYPE " + this);
        }
    }
}
