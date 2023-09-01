package org.kie.pmml.api.enums;

import java.util.Arrays;
import java.util.Objects;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/MiningSchema.html#xsdType_OUTLIER-TREATMENT-METHOD>OUTLIER-TREATMENT-METHOD</a>
 */
public enum OUTLIER_TREATMENT_METHOD implements Named {

    AS_IS("asIs"),
    AS_MISSING_VALUES("asMissingValues"),
    AS_EXTREME_VALUES("asExtremeValues");

    private String name;

    OUTLIER_TREATMENT_METHOD(String name) {
        this.name = name;
    }

    public static OUTLIER_TREATMENT_METHOD byName(String name) {
        return Arrays.stream(OUTLIER_TREATMENT_METHOD.values()).filter(value -> Objects.equals(name, value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find OUTLIER_TREATMENT_METHOD with name: " + name));
    }

    @Override
    public String getName() {
        return name;
    }
}
