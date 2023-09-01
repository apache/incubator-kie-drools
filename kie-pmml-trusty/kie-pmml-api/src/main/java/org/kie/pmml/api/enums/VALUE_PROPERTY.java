package org.kie.pmml.api.enums;

import java.util.Arrays;
import java.util.Objects;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/DataDictionary.html#xsdElement_Value>Value</a>
 */
public enum VALUE_PROPERTY implements Named {

    VALID("valid"),
    INVALID("invalid"),
    MISSING("missing");

    private String name;

    VALUE_PROPERTY(String name) {
        this.name = name;
    }

    public static VALUE_PROPERTY byName(String name) {
        return Arrays.stream(VALUE_PROPERTY.values()).filter(value -> Objects.equals(name, value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find VALUE_PROPERTY with name: " + name));
    }

    public String getName() {
        return name;
    }
}
