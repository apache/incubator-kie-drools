package org.kie.dmn.feel.lang;

import java.util.Map;

public interface Scope {

    String BUILT_IN = "<built-in>";
    String GLOBAL   = "<global>";
    String LOCAL    = "<local>";

    /**
     * Returns the scope name
     *
     * @return
     */
    String getName();

    /**
     * Returns the enclosing (parent) scope
     * @return
     */
    Scope getParentScope();

    /**
     * Adds an enclosed (child) scope
     *
     * @param scope
     */
    void addChildScope(Scope scope);

    /**
     * Gets a map of all children scopes.
     * The key of the map is the string name of the
     * children scope and the value is the scope itself.
     *
     * @return
     */
    Map<String, Scope> getChildScopes();

    /**
     * Defines a new symbol in this scope
     *
     * @param symbol
     * @return
     */
    boolean define(Symbol symbol);

    /**
     * Searches and returns a symbol with the given
     * id if it exists. The search is recursive
     * up, so if a symbol is not found in the current
     * scope, the algorithm searches the parent
     * scopes all the way to the root built-in
     * scope.
     *
     * @param id
     * @return
     */
    Symbol resolve(String id);

    /**
     * Searches and returns a symbol with the given
     * qualified name if it exists. The search is recursive
     * up, so if a symbol is not found in the current scope,
     * the algorith searches the parend scopes all the
     * way to the root built-in scope.
     *
     * @param qualifiedName
     * @return
     */
    Symbol resolve(String[] qualifiedName);

    /**
     * This method is used during context-aware parsing
     * to find multi-token symbols iteratively. It is used
     * in conjunction with the #followUp method below.
     *
     * @param token
     */
    void start(String token);

    /**
     * This method is used during context-aware parsing
     * to find multi-token symbols iteratively. It is used
     * in conjunction with the #start method above.
     *
     * @param token
     */
    boolean followUp(String token, boolean isPredict);

    Map<String, Symbol> getSymbols();

    /**
     * maybe null.
     */
    Type getType();

}
