package org.kie.dmn.core.impl;

import java.util.Map;

import org.kie.dmn.feel.util.EvalHelper;

public interface FEELPropertyAccessible {

    default EvalHelper.PropertyValueResult getFEELProperty(String property) {
        // This implementation is used only for templating purposes and should never be called
        switch (property) {
            case "<PROPERTY_NAME>":
                return EvalHelper.PropertyValueResult.ofValue(this.getProperty());
            default:
                return EvalHelper.PropertyValueResult.notDefined();
        }
    }

    void setFEELProperty(String key, Object value);

    Map<String, Object> allFEELProperties();

    void setAll(Map<String, Object> values);
}
