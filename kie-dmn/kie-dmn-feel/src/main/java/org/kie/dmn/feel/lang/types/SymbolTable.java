package org.kie.dmn.feel.lang.types;

import java.util.stream.Stream;

import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;

public class SymbolTable {
    private Scope builtInScope = new ScopeImpl( Scope.BUILT_IN, null );

    public SymbolTable() {
        init();
    }

    private void init() {
        // the following automatically adds the GLOBAL scope as a child to the built-in scope
        new ScopeImpl( Scope.GLOBAL, builtInScope );

        // pre-loads all the built in functions and types
        Stream.of( BuiltInFunctions.getFunctions() ).forEach( f -> builtInScope.define( f.getSymbol() ) );
        Stream.of(BuiltInType.values()).flatMap(b -> b.getSymbols().stream()).forEach(t -> builtInScope.define(t));
    }

    public Scope getBuiltInScope() {
        return builtInScope;
    }

    public Scope getGlobalScope() {
        return builtInScope.getChildScopes().get( Scope.GLOBAL );
    }
}
