package org.kie.dmn.api.core;

import java.util.Map;

public interface FEELPropertyAccessible {

    AbstractPropertyValueResult getFEELProperty(String property);

    void setFEELProperty(String key, Object value);

    Map<String, Object> allFEELProperties();

    void fromMap(Map<String, Object> values);
}
