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

import org.kie.dmn.feel.lang.types.SymbolTable;

import java.util.Stack;

public class EvaluationContextImpl {

    private final SymbolTable   symbols;
    private       Stack<Object> stack;

    public EvaluationContextImpl(SymbolTable symbols) {
        this.symbols = symbols;
    }

    public void push(Object obj) {
        stack.push( obj );
    }

    public Object pop() {
        return stack.pop();
    }

    public Object peek() {
        return stack.peek();
    }

    public Stack<Object> getStack() {
        return this.stack;
    }

    public SymbolTable getSymbols() {
        return symbols;
    }
}
