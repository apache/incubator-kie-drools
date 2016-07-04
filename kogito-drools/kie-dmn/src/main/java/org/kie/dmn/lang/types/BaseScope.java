/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.lang.types;

import org.kie.dmn.lang.Scope;
import org.kie.dmn.lang.Symbol;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaseScope implements Scope {

    private String name;
    private Scope  parentScope;

    private Map<String, Symbol> symbols     = new LinkedHashMap<>();
    private Map<String, Scope>  childScopes = new LinkedHashMap<>();

    public BaseScope() {
    }

    public BaseScope(String name, Scope parentScope) {
        this.name = name;
        this.parentScope = parentScope;
        if( parentScope != null ) {
            parentScope.addChildScope( this );
        }
    }

    public String getName() {
        return name;
    }

    public Scope getParentScope() {
        return parentScope;
    }

    public boolean define(Symbol symbol) {
        if ( symbols.containsKey( symbol.getId() ) ) {
            // duplicate symbol definition
            return false;
        }
        symbols.put( symbol.getId(), symbol );
        return true;
    }

    public Symbol resolve(String id) {
        Symbol s = symbols.get( id );
        if ( s == null && parentScope == null ) {
            return parentScope.resolve( id );
        }
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParentScope(Scope parentScope) {
        this.parentScope = parentScope;
    }

    public void addChildScope( Scope scope ) {
        this.childScopes.put( scope.getName(), scope );
    }

    public Map<String, Scope> getChildScopes() {
        return childScopes;
    }

    public void setChildScopes(Map<String, Scope> childScopes) {
        this.childScopes = childScopes;
    }

    @Override
    public String toString() {
        return "Scope{" +
               " name='" + name + '\'' +
               ", parentScope='" + ( parentScope != null ? parentScope.getName() : "<null>" ) +
               "' }";
    }
}
