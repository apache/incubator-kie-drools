package org.drools.base.facttemplates;

import java.util.Map;

public interface Fact {

    Object get(String name);

    void set(String name, Object value);

    Map<String, Object> asMap();

    FactTemplate getFactTemplate();

    default boolean isEvent() {
        return false;
    }
}
