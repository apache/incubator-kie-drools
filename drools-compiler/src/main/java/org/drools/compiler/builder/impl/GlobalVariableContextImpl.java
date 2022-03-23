package org.drools.compiler.builder.impl;

import java.util.HashMap;
import java.util.Map;

public class GlobalVariableContextImpl implements GlobalVariableContext {
    private final Map<String, Class<?>> globals = new HashMap<>();

    @Override
    public Map<String, Class<?>> getGlobals() {
        return this.globals;
    }

    @Override
    public void addGlobal(String name, Class<?> type) {
        globals.put(name, type);
    }

}
