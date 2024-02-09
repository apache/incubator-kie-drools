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
package org.kie.dmn.core.compiler;

import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.feel.lang.CompilerContext;

public class DMNCompilerContext {

    private final DMNFEELHelper feelHelper;
    private Deque<DMNScope> stack = new ArrayDeque<>();
    private Function<String, Reader> relativeResolver;

    public DMNCompilerContext(DMNFEELHelper feelHelper) {
        this.feelHelper = feelHelper;
        this.stack.push( new DMNScope(  ) );
    }

    public void enterFrame() {
        this.stack.push( new DMNScope( this.stack.peek() ) );
    }

    public void exitFrame() {
        this.stack.pop();
    }

    public DMNType resolve( String name ) {
        return this.stack.peek().resolve( name );
    }

    public void setVariable( String name, DMNType type ) {
        this.stack.peek().setVariable( name, type );
    }

    public Map<String, DMNType> getVariables() {
        Map<String, DMNType> variables = new HashMap<>(  );
        for( DMNScope scope : stack ) {
            variables.putAll( scope.getVariables() );
        }
        return variables;
    }

    public CompilerContext toCompilerContext() {
        CompilerContext compilerContext = feelHelper.newCompilerContext();
        compilerContext.getListeners().clear();
        for ( Map.Entry<String, DMNType> entry : this.getVariables().entrySet() ) {
            compilerContext.addInputVariableType(entry.getKey(),
                                                 ((BaseDMNTypeImpl) entry.getValue()).getFeelType());
        }
        return compilerContext;
    }

    public DMNFEELHelper getFeelHelper() {
        return feelHelper;
    }

    public void setRelativeResolver(Function<String, Reader> relativeResolver) {
        this.relativeResolver = relativeResolver;
    }

    public Function<String, Reader> getRelativeResolver() {
        return relativeResolver;
    }

}
