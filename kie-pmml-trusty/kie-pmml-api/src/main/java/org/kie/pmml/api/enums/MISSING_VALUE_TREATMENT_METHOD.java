package org.kie.pmml.api.enums;

import java.util.Arrays;
import java.util.Objects;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see
 * <a href=http://dmg.org/pmml/v4-4/MiningSchema.html#xsdType_MISSING-VALUE-TREATMENT-METHOD>MISSING-VALUE_TREATMENT-METHOD</a>
 */
public enum MISSING_VALUE_TREATMENT_METHOD implements Named {

    AS_IS("asIs"),
    AS_MEAN("asMean"),
    AS_MODE("asMode"),
    AS_MEDIAN("asMedian"),
    AS_VALUE("asValue"),
    RETURN_INVALID("returnInvalid");

    private String name;

    MISSING_VALUE_TREATMENT_METHOD(String name) {
        this.name = name;
    }

    public static MISSING_VALUE_TREATMENT_METHOD byName(String name) {
        return Arrays.stream(MISSING_VALUE_TREATMENT_METHOD.values()).filter(value -> Objects.equals(name,
                                                                                                     value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find MISSING_VALUE_TREATMENT_METHOD with name: " + name));
    }

    public String getName() {
        return name;
    }
}
