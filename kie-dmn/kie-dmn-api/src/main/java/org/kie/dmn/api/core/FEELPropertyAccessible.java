package org.kie.dmn.api.core;

import java.util.Map;
import java.util.Optional;

public interface FEELPropertyAccessible {

    interface AbstractPropertyValueResult {

        Optional<Object> toOptional();
    }

    AbstractPropertyValueResult getFEELProperty(String property);

    void setFEELProperty(String key, Object value);

    Map<String, Object> allFEELProperties();

    void fromMap(Map<String, Object> values);
}
