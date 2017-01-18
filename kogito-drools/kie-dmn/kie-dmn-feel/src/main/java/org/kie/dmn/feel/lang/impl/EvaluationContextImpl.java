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

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.util.EvalHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class EvaluationContextImpl implements EvaluationContext {

    private final FEELEventListenersManager eventsManager;
    private       Stack<ExecutionFrame> stack;

    public EvaluationContextImpl(FEELEventListenersManager eventsManager) {
        this.eventsManager = eventsManager;
        this.stack = new Stack<>();
        // we create a rootFrame to hold all the built in functions
        push( RootExecutionFrame.INSTANCE );
        // and then create a global frame to be the starting frame
        // for function evaluation
        ExecutionFrameImpl global = new ExecutionFrameImpl( RootExecutionFrame.INSTANCE );
        push( global );
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

    @Override
    public void enterFrame() {
        push( new ExecutionFrameImpl( peek() /*, symbols, scope*/ ) );
    }

    @Override
    public void exitFrame() {
        pop();
    }

    @Override
    public void setValue(String name, Object value) {
        peek().setValue( name, EvalHelper.coerceNumber( value ) );
    }

    @Override
    public Object getValue(String name) {
        return peek().getValue( name );
    }

    @Override
    public Object getValue(String[] name) {
        throw new UnsupportedOperationException( "needs implementation?" );
    }

    @Override
    public boolean isDefined(String name) {
        return peek().isDefined( name );
    }

    @Override
    public boolean isDefined(String[] name) {
        throw new UnsupportedOperationException( "needs implementation?" );
    }

    @Override
    public Map<String, Object> getAllValues() {
        Map<String, Object> values = new HashMap<>(  );
        for( int i = 0; i < stack.size(); i++ ) {
            values.putAll( stack.get( i ).getAllValues() );
        }
        return values;
    }

    public FEELEventListenersManager getEventsManager() {
        return eventsManager;
    }
}
