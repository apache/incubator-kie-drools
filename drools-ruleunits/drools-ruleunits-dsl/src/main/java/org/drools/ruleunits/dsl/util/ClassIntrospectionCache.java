package org.drools.ruleunits.dsl.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.base.util.PropertyReactivityUtil;

public class ClassIntrospectionCache {
    private static final Map<Class<?>, List<String>> propertiesMap = new HashMap<>();

    public static int getFieldIndex(Class<?> patternClass, String fieldName) {
        return propertiesMap.computeIfAbsent(patternClass, PropertyReactivityUtil::getAccessiblePropertiesIncludingNonGetterValueMethod).indexOf(fieldName);
    }
}