/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.dmn.feel.parser.feel11.profiles.DoCompileFEELProfile;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.UnaryTest;

/**
 * Language runtime entry point
 */
public class FEELImpl implements FEEL {

    private static final Map<String, Object> EMPTY_INPUT = Collections.emptyMap();

    private Set<FEELEventListener> instanceEventListeners = new HashSet<>();

    private final List<FEELProfile> profiles;
    // pre-cached results from the above profiles...
    private final Optional<ExecutionFrameImpl> customFrame;
    private final Collection<FEELFunction> customFunctions;
    private final boolean doCompile;

    public FEELImpl() {
        this(Collections.emptyList());
    }

    public FEELImpl(final List<FEELProfile> profiles) {
        this.profiles = Collections.unmodifiableList(profiles);
        ExecutionFrameImpl frame = null;
        final Map<String, FEELFunction> functions = new HashMap<>();
        for (FEELProfile p : profiles) {
            for (FEELFunction f : p.getFEELFunctions()) {
                if (frame == null) {
                    frame = new ExecutionFrameImpl(null);
                }
                frame.setValue(f.getName(), f);
                functions.put(f.getName(), f);
            }
        }
        doCompile = profiles.stream().anyMatch(x -> x instanceof DoCompileFEELProfile);
        customFrame = Optional.ofNullable(frame);
        customFunctions = Collections.unmodifiableCollection(functions.values());
    }

    @Override
    public CompilerContext newCompilerContext() {
        return newCompilerContext(Collections.emptySet());
    }

    public CompilerContext newCompilerContext(final Collection<FEELEventListener> contextListeners) {
        return new CompilerContextImpl(getEventsManager(contextListeners)).addFEELFunctions(customFunctions);
    }

    @Override
    public CompiledExpression compile(final String expression,
                                      final CompilerContext ctx) {
        throw new UnsupportedOperationException("Not supported in GWT (yet)");
    }

    @Override
    public CompiledExpression compileUnaryTests(final String expression,
                                                final CompilerContext ctx) {
        throw new UnsupportedOperationException("Not supported in GWT (yet)");
    }

    @Override
    public Object evaluate(String expression) {
        return evaluate(expression, FEELImpl.EMPTY_INPUT);
    }

    @Override
    public String parseTest(final String value) {
        final FEEL_1_1Parser parser = FEELParser.parse(null, value, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyList(), null);

        final ParseTree tree = parser.expression();

        final ASTBuilderVisitor v = new ASTBuilderVisitor(Collections.emptyMap(), null);
        final BaseNode expr = v.visit(tree);

        return expr.getText() + " - " + expr.getResultType() + " - " + expr.toString();
    }

    @Override
    public Object evaluate(final String expression,
                           final EvaluationContext ctx) {
        final CompilerContext compilerCtx = newCompilerContext(ctx.getListeners());
        final Map<String, Object> inputVariables = ctx.getAllValues();
        if (inputVariables != null) {
            inputVariables.entrySet().stream().forEach(e -> compilerCtx.addInputVariable(e.getKey(), e.getValue()));
        }
        final CompiledExpression expr = compile(expression, compilerCtx);
        return evaluate(expr, ctx);
    }

    @Override
    public Object evaluate(final String expression,
                           final Map<String, Object> inputVariables) {
        final CompilerContext ctx = newCompilerContext();
        if (inputVariables != null) {
            inputVariables.entrySet().stream().forEach(e -> ctx.addInputVariable(e.getKey(), e.getValue()));
        }
        final CompiledExpression expr = compile(expression, ctx);
        if (inputVariables == null) {
            return evaluate(expr, EMPTY_INPUT);
        } else {
            return evaluate(expr, inputVariables);
        }
    }

    @Override
    public Object evaluate(final CompiledExpression expr,
                           final Map<String, Object> inputVariables) {
        throw new UnsupportedOperationException("Not supported in GWT (yet)");
    }

    @Override
    public Object evaluate(final CompiledExpression expr,
                           final EvaluationContext ctx) {
        throw new UnsupportedOperationException("Not supported in GWT (yet)");
    }

    public EvaluationContext newEvaluationContext(final Collection<FEELEventListener> listeners,
                                                  final Map<String, Object> inputVariables) {
        final FEELEventListenersManager eventsManager = getEventsManager(listeners);
        final EvaluationContextImpl ctx = new EvaluationContextImpl(eventsManager, inputVariables.size());
        if (customFrame.isPresent()) {
            final ExecutionFrameImpl globalFrame = (ExecutionFrameImpl) ctx.pop();
            final ExecutionFrameImpl interveawedFrame = customFrame.get();
            interveawedFrame.setParentFrame(ctx.peek());
            globalFrame.setParentFrame(interveawedFrame);
            ctx.push(interveawedFrame);
            ctx.push(globalFrame);
        }
        ctx.setValues(inputVariables);
        return ctx;
    }

    @Override
    public List<UnaryTest> evaluateUnaryTests(final String expression) {
        return evaluateUnaryTests(expression, Collections.emptyMap());
    }

    @Override
    public List<UnaryTest> evaluateUnaryTests(final String expression,
                                              final Map<String, Type> variableTypes) {
        throw new UnsupportedOperationException("Not supported in GWT (yet)");
    }

    @Override
    public void addListener(final FEELEventListener listener) {
        instanceEventListeners.add(listener);
    }

    @Override
    public void removeListener(final FEELEventListener listener) {
        instanceEventListeners.remove(listener);
    }

    @Override
    public Set<FEELEventListener> getListeners() {
        return Collections.unmodifiableSet(instanceEventListeners);
    }

    public FEELEventListenersManager getEventsManager(final Collection<FEELEventListener> contextListeners) {
        final FEELEventListenersManager listenerMgr = new FEELEventListenersManager();
        listenerMgr.addListeners(instanceEventListeners);
        listenerMgr.addListeners(contextListeners);
        return listenerMgr;
    }
}
