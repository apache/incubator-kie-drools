/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel.expr;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

import org.drools.mvel.MVELSafeHelper;
import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.debug.DebugTools;
import org.mvel2.integration.VariableResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MvelEvaluator<T> {
    private static final boolean THREAD_SAFE = true;

    private static final Logger logger = LoggerFactory.getLogger(MvelEvaluator.class);

    private final Serializable expr;

    private MvelEvaluator(Serializable expr) {
        this.expr = expr;
    }

    public static <T> MvelEvaluator<T> createMvelEvaluator(Serializable expr) {
        return THREAD_SAFE ? new MvelEvaluator.ThreadSafe(expr) : new MvelEvaluator(expr);
    }

    public T evaluate(Object ctx) {
        return evaluate(ctx, null);
    }

    public T evaluate(VariableResolverFactory factory) {
        return evaluate(null, factory);
    }

    public T evaluate(Object ctx, VariableResolverFactory factory) {
        return internalEvaluate(ctx, factory);
    }

    private <T> T internalEvaluate(Object ctx, VariableResolverFactory factory) {
        if (MVELDebugHandler.isDebugMode() && this.expr instanceof CompiledExpression) {
            CompiledExpression compexpr = (CompiledExpression) this.expr;
            if (MVELDebugHandler.verbose) {
                logger.info(DebugTools.decompile(compexpr));
            }
            return (T) MVEL.executeDebugger(compexpr, ctx, factory);
        }

        return (T) MVELSafeHelper.getEvaluator().executeExpression(this.expr, ctx, factory);
    }

    public Serializable getExpr() {
        return expr;
    }

    private static class ThreadSafe<T> extends MvelEvaluator<T> {
        private enum State {
            NEW, INITIALIZING, CONTENTED, INITIALIZED
        }

        private final AtomicReference<State> state = new AtomicReference(State.NEW);

        public ThreadSafe(Serializable expr) {
            super(expr);
        }

        @Override
        public T evaluate(Object ctx, VariableResolverFactory factory) {
            if (state.get() != State.INITIALIZED) {
                if (state.compareAndSet(State.NEW, State.INITIALIZING)) {
                    T result = super.evaluate(ctx, factory);
                    synchronized (state) {
                        boolean shouldNotify = state.get() == State.CONTENTED;
                        state.set(State.INITIALIZED);
                        if (shouldNotify) {
                            state.notifyAll();
                        }
                    }
                    return result;
                } else {
                    synchronized (state) {
                        if (state.get() != State.INITIALIZED) {
                            try {
                                state.set(State.CONTENTED);
                                state.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }

            return super.evaluate(ctx, factory);
        }
    }
}
