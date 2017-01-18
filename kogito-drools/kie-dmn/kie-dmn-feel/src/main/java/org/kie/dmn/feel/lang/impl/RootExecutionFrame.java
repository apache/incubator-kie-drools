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

package org.kie.dmn.feel.lang.impl;

import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.util.EvalHelper;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a thread safe implementation of a root
 * execution frame that automatically registers all
 * the built in functions.
 */
public class RootExecutionFrame implements ExecutionFrame {

    public static final ExecutionFrame INSTANCE = new RootExecutionFrame();

    private final Map<String, Object> functions;

    private RootExecutionFrame() {
        Map<String, Object> builtIn = new ConcurrentHashMap<>(  );
        for( FEELFunction f : BuiltInFunctions.getFunctions() ) {
            builtIn.put( EvalHelper.normalizeVariableName( f.getName() ), f );
        }
        functions = Collections.unmodifiableMap( builtIn );
    }

    public Object getValue(String symbol) {
        symbol = EvalHelper.normalizeVariableName( symbol );
        if ( functions.containsKey( symbol ) ) {
            return functions.get( symbol );
        }
        return null;
    }

    public boolean isDefined( String symbol ) {
        symbol = EvalHelper.normalizeVariableName( symbol );
        return functions.containsKey( symbol );
    }

    public void setValue(String symbol, Object value) {
        throw new UnsupportedOperationException( "No value or variable can be set on the RootExecutionFrame" );
    }

    public Map<String, Object> getAllValues() {
        return this.functions;
    }
}
