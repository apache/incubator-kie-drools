package org.kie.dmn.core.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

// All implementations are used only for templating purposes and should never be called
public interface DMNTypeSafeTypeTemplate {

    default org.kie.dmn.feel.util.EvalHelper.PropertyValueResult getFEELProperty(String property) {
        switch (property) {
            case "<PROPERTY_NAME>":
                return org.kie.dmn.feel.util.EvalHelper.PropertyValueResult.ofValue(this.getPropertyName());
            default:
                return org.kie.dmn.feel.util.EvalHelper.PropertyValueResult.notDefined();
        }
    }

    default void setFEELProperty(String property, Object value) {
        switch (property) {
            case "<PROPERTY_NAME>":
                this.setPropertyName((PropertyType)value); return;
        }
    }

    java.util.Map.Map<String, Object> allFEELProperties() {
        java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("<PROPERTY_NAME>", this.getPropertyName());
        return result;
    }

    void fromMap(Map<String, Object> values) {
        // Simple fields
        {
            $property$ = (PropertyType) values.get("$property$");
        }

        // Composite fields
        {
            $property$ = new PropertyType();
            $property$.fromMap((java.util.Map<String, Object>) values.get("$property$"));
        }

        // Collections fields
        {
            $property$ = new java.util.ArrayList<>();
            for(java.util.Map<String, Object> v : (java.util.Collection<java.util.Map<String, Object>>)values.get("$property$")) {
                PropertyType item = new PropertyType();
                item.fromMap(v);
                $property$.add(item);
            }
        }
    }
}
