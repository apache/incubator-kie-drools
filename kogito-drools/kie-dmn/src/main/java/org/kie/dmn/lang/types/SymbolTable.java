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
import org.kie.dmn.lang.Type;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    BuiltInScope      builtInScope = new BuiltInScope();
    Map<String, Type> types        = new HashMap<>();

    public SymbolTable() {
        init();
    }

    private void init() {
        builtInScope.define( new BuiltInTypeSymbol( "true", BuiltInType.BOOLEAN, builtInScope ) );
        builtInScope.define( new BuiltInTypeSymbol( "false", BuiltInType.BOOLEAN, builtInScope ) );
        builtInScope.define( new FunctionSymbol( "date" ) );
        builtInScope.define( new FunctionSymbol( "time" ) );
        builtInScope.define( new FunctionSymbol( "date and time" ) );
        builtInScope.define( new FunctionSymbol( "duration" ) );
        builtInScope.define( new FunctionSymbol( "decision table" ) );
    }

    public Scope getBuiltInScope() {
        return builtInScope;
    }
}
