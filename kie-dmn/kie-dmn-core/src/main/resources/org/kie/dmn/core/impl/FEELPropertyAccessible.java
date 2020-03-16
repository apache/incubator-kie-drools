package org.kie.dmn.core.impl;

import java.util.Map;

public interface FEELPropertyAccessible {

    default org.kie.dmn.feel.util.EvalHelper.PropertyValueResult getFEELProperty(String property) {
        // This implementation is used only for templating purposes and should never be called
        switch (property) {
            case "<PROPERTY_NAME>":
                return org.kie.dmn.feel.util.EvalHelper.PropertyValueResult.ofValue("propertyName");
            default:
                return org.kie.dmn.feel.util.EvalHelper.PropertyValueResult.notDefined();
        }
    }

    void setFEELProperty(String key, Object value);

    Map<String, Object> allFEELProperties();

    void setAll(Map<String, Object> values);
}
