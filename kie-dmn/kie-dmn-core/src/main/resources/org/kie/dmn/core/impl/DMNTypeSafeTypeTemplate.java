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
            Object propertyValues = values.get("$property$");
            if(propertyValues != null) {
                $property$ = (PropertyType) propertyValues;
            }
        }

        // Composite fields
        {
            Object propertyValues = values.get("$property$");
            if(propertyValues != null) {
                if (propertyValues instanceof PropertyType) {
                    $property$ = (PropertyType) propertyValues;
                } else {
                    $property$ = new PropertyType();
                    $property$.fromMap((java.util.Map<String, Object>) propertyValues);
                }
            }
        }

        // Collections of composite fields
        {
            Object propertyValues = values.get("$property$");
            if(propertyValues != null) {
                $property$ = new java.util.ArrayList<>();
                processCompositeCollection($property$, (java.util.Collection)propertyValues, PropertyType.class);
            }
        }

        // Collections of basic fields
        {
            Object propertyValues = values.get("$property$");
            if(propertyValues != null) {
                $property$ = new java.util.ArrayList<>();
                for (Object item : (Iterable<?>)propertyValues) {
                    $property$.add((PropertyType)item);
                }
            }
        }
    }

    void processCompositeCollection(java.util.Collection destCol, java.util.Collection srcCol, Class<?> baseClass) {
        for (Object v : (java.util.Collection<Object>) srcCol) {
            if (v instanceof java.util.Collection) {
                java.util.Collection innerDestcol = new java.util.ArrayList();
                processCompositeCollection(innerDestcol, (java.util.Collection) v, baseClass);
                destCol.add(innerDestcol);
            } else if (baseClass.isAssignableFrom(v.getClass())) {
                destCol.add(v);
            } else {
                try {
                    org.kie.dmn.api.core.FEELPropertyAccessible item = (org.kie.dmn.api.core.FEELPropertyAccessible) baseClass.newInstance();
                    item.fromMap((java.util.Map<String, Object>) v);
                    destCol.add(item);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new org.kie.dmn.typesafe.DMNTypeSafeException(e);
                }
            }
        }
    }
}
