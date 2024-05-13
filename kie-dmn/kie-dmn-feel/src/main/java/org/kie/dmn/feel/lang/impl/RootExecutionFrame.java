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
package org.kie.dmn.feel.lang.impl;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.util.StringEvalHelper;

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
            builtIn.put(StringEvalHelper.normalizeVariableName(f.getName() ), f );
        }
        functions = Collections.unmodifiableMap( builtIn );
    }

    public Object getValue(String symbol) {
        symbol = StringEvalHelper.normalizeVariableName( symbol );
        if ( functions.containsKey( symbol ) ) {
            return functions.get( symbol );
        }
        return null;
    }

    public boolean isDefined( String symbol ) {
        symbol = StringEvalHelper.normalizeVariableName( symbol );
        return functions.containsKey( symbol );
    }

    public void setValue(String symbol, Object value) {
        throw new UnsupportedOperationException( "No value or variable can be set on the RootExecutionFrame" );
    }

    public Map<String, Object> getAllValues() {
        return this.functions;
    }

    @Override
    public void setRootObject(Object v) {
        throw new UnsupportedOperationException("Setting root object is not supported on the Root frame");
    }

    @Override
    public Object getRootObject() {
        return null;
    }
}
