package org.drools.compiler.builder.impl;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GlobalVariableContextImpl implements GlobalVariableContext {
    private final Map<String, Type> globals = new HashMap<>();

    @Override
    public Map<String, Type> getGlobals() {
        return this.globals;
    }

    @Override
    public void addGlobal(String name, Type type) {
        globals.put(name, type);
    }

}
