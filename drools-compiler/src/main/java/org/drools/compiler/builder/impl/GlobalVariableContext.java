package org.drools.compiler.builder.impl;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * A build context that holds all the declared global variables
 * as a string -> type map.
 * 
 */
public interface GlobalVariableContext {
    Map<String, Type> getGlobals();

    void addGlobal(String identifier, Type type);
}
