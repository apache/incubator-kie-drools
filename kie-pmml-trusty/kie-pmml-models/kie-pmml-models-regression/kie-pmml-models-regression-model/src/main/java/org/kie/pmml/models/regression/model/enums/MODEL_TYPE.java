package org.kie.pmml.models.regression.model.enums;

import java.util.Arrays;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/Regression.html#xsdElement_RegressionModel>RegressionModel</a>
 */
public enum MODEL_TYPE {

    LINEAR_REGRESSION("linearRegression"),
    STEPWISE_POLYNOMIAL_REGRESSION("stepwisePolynomialRegression"),
    LOGISTIC_REGRESSION("logisticRegression");

    private String name;

    MODEL_TYPE(String name) {
        this.name = name;
    }

    public static MODEL_TYPE byName(String name) {
        return Arrays.stream(MODEL_TYPE.values()).filter(value -> name.equals(value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find MODEL_TYPE with name: " + name));
    }

    public String getName() {
        return name;
    }
}
