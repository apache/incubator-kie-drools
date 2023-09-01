/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
