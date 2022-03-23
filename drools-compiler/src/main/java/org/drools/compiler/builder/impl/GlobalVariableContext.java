package org.drools.compiler.builder.impl;

import java.util.Map;

public interface GlobalVariableContext {
    Map<String, Class<?>> getGlobals();

    void addGlobal(String identifier, Class<?> clazz);
}
