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

package org.kie.dmn.feel.lang.types;

import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.runtime.functions.*;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    BuiltInScope      builtInScope = new BuiltInScope();
    Map<String, Type> types        = new HashMap<>();

    public SymbolTable() {
        init();
    }

    private void init() {
        // the following automatically adds the GLOBAL scope as a child to the built-in scope
        new LocalScope( Scope.GLOBAL, builtInScope );

        // pre-loads all the built in functions
        builtInScope.define( new BuiltInTypeSymbol( "true", BuiltInType.BOOLEAN, builtInScope ) );
        builtInScope.define( new BuiltInTypeSymbol( "false", BuiltInType.BOOLEAN, builtInScope ) );
        builtInScope.define( new FunctionSymbol( "date", new DateFunction() ) );
        builtInScope.define( new FunctionSymbol( "time", new TimeFunction() ) );
        builtInScope.define( new FunctionSymbol( "date and time", new DateTimeFunction() ) );
        builtInScope.define( new FunctionSymbol( "duration", new DurationFunction() ) );
        builtInScope.define( new FunctionSymbol( "years and months duration", new YearsAndMonthsFunction() ) );
        builtInScope.define( new FunctionSymbol( "decision table", null ) );
    }

    public Scope getBuiltInScope() {
        return builtInScope;
    }

    public Scope getGlobalScope() { return builtInScope.getChildScopes().get( Scope.GLOBAL ); }
}
