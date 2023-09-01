package org.kie.pmml.models.regression.model.enums;

import java.util.Arrays;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/Regression.html#xsdType_REGRESSIONNORMALIZATIONMETHOD>REGRESSIONNORMALIZATIONMETHOD</a>
 */
public enum REGRESSION_NORMALIZATION_METHOD {
    NONE("none"),
    SIMPLEMAX("simplemax"),
    SOFTMAX("softmax"),
    LOGIT("logit"),
    PROBIT("probit"),
    CLOGLOG("cloglog"),
    EXP("exp"),
    LOGLOG("loglog"),
    CAUCHIT("cauchit");

    private String name;

    REGRESSION_NORMALIZATION_METHOD(String name) {
        this.name = name;
    }

    public static REGRESSION_NORMALIZATION_METHOD byName(String name) {
        return Arrays.stream(REGRESSION_NORMALIZATION_METHOD.values()).filter(value -> name.equals(value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find REGRESSION_NORMALIZATION_METHOD with name: " + name));
    }

    public String getName() {
        return name;
    }
}
