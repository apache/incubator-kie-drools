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

import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.Symbol;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.types.SymbolTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class EvaluationContextImpl {

    private final SymbolTable   symbols;
    private       Stack<ExecutionFrame> stack;

    public EvaluationContextImpl(SymbolTable symbols) {
        this.symbols = symbols;
        this.stack = new Stack<>();
        ExecutionFrame rootFrame = new ExecutionFrame( null, symbols, symbols.getGlobalScope() );
        push( rootFrame );
    }

    public SymbolTable getSymbols() {
        return symbols;
    }

    public void push(ExecutionFrame obj) {
        stack.push( obj );
    }

    public ExecutionFrame pop() {
        return stack.pop();
    }

    public ExecutionFrame peek() {
        return stack.peek();
    }

    public Stack<ExecutionFrame> getStack() {
        return this.stack;
    }

    public void enterScope( String scopeName ) {
        Scope scope = peek().getCurrentScope().getChildScopes().get( scopeName );
        push( new ExecutionFrame( peek(), symbols, scope ) );
    }

    public void exitScope() {
        pop();
    }

    public Symbol resolveSymbol( String name ) {
        return peek().getCurrentScope().resolve( name );
    }

    public Symbol resolveSymbol( String[] name ) {
        return peek().getCurrentScope().resolve( name );
    }

}
