package org.kie.pmml.api.enums;

import java.util.Arrays;
import java.util.Objects;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-2-1/DataDictionary.html#xsdType_OPTYPE>OPTYPE</a>
 */
public enum OP_TYPE implements Named {

    CATEGORICAL("categorical"),
    ORDINAL("ordinal"),
    CONTINUOUS("continuous");

    private String name;

    OP_TYPE(String name) {
        this.name = name;
    }

    public static OP_TYPE byName(String name) {
        return Arrays.stream(OP_TYPE.values()).filter(value -> Objects.equals(name, value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find OP_TYPE with name: " + name));
    }

    public String getName() {
        return name;
    }
}
