package org.kie.dmn.feel.lang;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.lang.types.FEELTypeRegistry;
import org.kie.dmn.feel.runtime.FEELFunction;

public interface CompilerContext {

    CompilerContext addInputVariableType( String name, Type type );

    Map<String, Type> getInputVariableTypes();

    CompilerContext addInputVariable( String name, Object value );

    Map<String, Object> getInputVariables();

    Set<FEELEventListener> getListeners();

    CompilerContext addFEELFunctions(Collection<FEELFunction> customFunction);

    Collection<FEELFunction> getFEELFunctions();

    boolean isDoCompile();

    void setDoCompile( boolean doCompile );

    void setFEELTypeRegistry(FEELTypeRegistry typeRegistry);

    FEELTypeRegistry getFEELFeelTypeRegistry();
}
