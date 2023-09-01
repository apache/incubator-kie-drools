package org.kie.dmn.feel.lang.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.DefaultBuiltinFEELTypeRegistry;
import org.kie.dmn.feel.lang.types.FEELTypeRegistry;
import org.kie.dmn.feel.runtime.FEELFunction;

public class CompilerContextImpl implements CompilerContext {
    private final FEELEventListenersManager eventsManager;
    private Map<String, Object> inputVariables = new HashMap<>();
    private Map<String, Type> inputVariableTypes = new HashMap<>();
    private Set<FEELFunction> customFunctions = new LinkedHashSet<>();
    private boolean doCompile;
    private FEELTypeRegistry typeRegistry = DefaultBuiltinFEELTypeRegistry.INSTANCE;

    /**
     * PLEASE NOTICE: it is recommended to instance the CompilerContext via the FEEL instance, so to have all profile configuration applied correctly.
     */
    public CompilerContextImpl(FEELEventListenersManager eventsManager) {
        this.eventsManager = eventsManager;
    }

    @Override
    public Set<FEELEventListener> getListeners() {
        return eventsManager.getListeners();
    }

    @Override
    public CompilerContext addInputVariableType(String name, Type type) {
        this.inputVariableTypes.put( name, type );
        return this;
    }

    @Override
    public Map<String, Type> getInputVariableTypes() {
        return inputVariableTypes;
    }

    @Override
    public CompilerContext addInputVariable(String name, Object value) {
        inputVariables.put( name, value );
        return this;
    }

    @Override
    public Map<String, Object> getInputVariables() {
        return this.inputVariables;
    }

    @Override
    public CompilerContextImpl addFEELFunctions(Collection<FEELFunction> customFunction) {
        this.customFunctions.addAll(customFunction);
        return this;
    }

    @Override
    public Collection<FEELFunction> getFEELFunctions() {
        return this.customFunctions;
    }

    @Override
    public boolean isDoCompile() {
        return doCompile;
    }

    @Override
    public void setDoCompile( boolean doCompile ) {
        this.doCompile = doCompile;
    }

    @Override
    public void setFEELTypeRegistry(FEELTypeRegistry typeRegistry) {
        this.typeRegistry = typeRegistry;
    }

    @Override
    public FEELTypeRegistry getFEELFeelTypeRegistry() {
        return this.typeRegistry;
    }
}
