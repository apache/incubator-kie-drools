package org.kie.pmml.api.enums;

import java.util.Arrays;
import java.util.Objects;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/MiningSchema.html#xsdType_INVALID-VALUE-TREATMENT-METHOD>INVALID-VALUE-TREATMENT-METHOD</a>
 */
public enum INVALID_VALUE_TREATMENT_METHOD implements Named {

    RETURN_INVALID("returnInvalid"),
    AS_IS("asIs"),
    AS_MISSING("asMissing"),
    AS_VALUE("asValue");

    private String name;

    INVALID_VALUE_TREATMENT_METHOD(String name) {
        this.name = name;
    }

    public static INVALID_VALUE_TREATMENT_METHOD byName(String name) {
        return Arrays.stream(INVALID_VALUE_TREATMENT_METHOD.values()).filter(value -> Objects.equals(name, value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find INVALID_VALUE_TREATMENT_METHOD with name: " + name));
    }

    public String getName() {
        return name;
    }
}
