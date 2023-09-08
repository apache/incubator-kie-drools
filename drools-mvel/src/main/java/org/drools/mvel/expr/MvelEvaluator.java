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
package org.drools.mvel.expr;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.drools.mvel.MVELSafeHelper;
import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.debug.DebugTools;
import org.mvel2.integration.VariableResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.mvel.MVELConditionEvaluator.isFullyEvaluated;

public class MvelEvaluator<T> {

    public static final String THREAD_SAFETY_PROPERTY = "drools.mvel.thread.safety";

    private static final EvaluatorType DEFAULT_EVALUATOR_TYPE = EvaluatorType.THREAD_UNSAFE;

    private static EvaluatorType EVALUATOR_TYPE = EvaluatorType.decode( System.getProperty(THREAD_SAFETY_PROPERTY, DEFAULT_EVALUATOR_TYPE.id) );

    private static final Logger logger = LoggerFactory.getLogger(MvelEvaluator.class);

    protected final Serializable expr;

    public enum EvaluatorType {
        THREAD_UNSAFE("unsafe"),
        THREAD_SAFE_ON_FIRST_EVAL("safe_on_first"),
        SYNCHRONIZED_TILL_EVALUATED("synced_till_eval"),
        FULLY_SYNCHRONIZED("fully_synced");

        private final String id;

        EvaluatorType(String id) {
            this.id = id;
        }

        public <T> MvelEvaluator<T> createMvelEvaluator(Serializable expr) {
            switch (this) {
                case THREAD_UNSAFE: return new MvelEvaluator<>(expr);
                case THREAD_SAFE_ON_FIRST_EVAL: return new MvelEvaluator.ThreadSafe<>(expr);
                case SYNCHRONIZED_TILL_EVALUATED: return new MvelEvaluator.SynchronizedTillEvaluated<>(expr);
                case FULLY_SYNCHRONIZED: return new MvelEvaluator.FullySynchronized<>(expr);
            }
            throw new UnsupportedOperationException();
        }

        public <T> MvelEvaluator<T> createMvelEvaluator(MvelEvaluator<T> syncedWith, Serializable expr) {
            switch (this) {
                case THREAD_UNSAFE: return new MvelEvaluator<>(expr);
                case THREAD_SAFE_ON_FIRST_EVAL: return new MvelEvaluator.ThreadSafe<>(expr);
                case SYNCHRONIZED_TILL_EVALUATED: return new MvelEvaluator.SynchronizedTillEvaluated<>(syncedWith, expr);
                case FULLY_SYNCHRONIZED: return new MvelEvaluator.FullySynchronized<>(syncedWith, expr);
            }
            throw new UnsupportedOperationException();
        }

        static EvaluatorType decode(String id) {
            for (EvaluatorType evaluatorType : EvaluatorType.class.getEnumConstants()) {
                if (evaluatorType.id.equalsIgnoreCase(id)) {
                    return evaluatorType;
                }
            }
            throw new UnsupportedOperationException("Unknown evaluator type: " + id);
        }
    }

    // only for testing purposes
    public static void setEvaluatorType(EvaluatorType evaluatorType) {
        EVALUATOR_TYPE = evaluatorType;
    }
    public static void resetEvaluatorType() {
        EVALUATOR_TYPE = DEFAULT_EVALUATOR_TYPE;
    }

    private MvelEvaluator(Serializable expr) {
        this.expr = expr;
    }

    public static <T> MvelEvaluator<T> createMvelEvaluator(Serializable expr) {
        return EVALUATOR_TYPE.createMvelEvaluator(expr);
    }

    public static <T> MvelEvaluator<T> createMvelEvaluator(MvelEvaluator<T> syncedWith, Serializable expr) {
        return EVALUATOR_TYPE.createMvelEvaluator(syncedWith, expr);
    }

    public T evaluate(Object ctx) {
        return evaluate(ctx, (VariableResolverFactory) null);
    }

    public T evaluate(VariableResolverFactory factory) {
        return evaluate(null, factory);
    }

    public T evaluate(Object ctx, VariableResolverFactory factory) {
        return internalEvaluate(ctx, factory);
    }

    public T evaluate(Object ctx, Map<String, Object> vars) {
        return (T) MVELSafeHelper.getEvaluator().executeExpression(this.expr, ctx, vars);
    }

    protected <T> T internalEvaluate(Object ctx, VariableResolverFactory factory) {
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
        public synchronized T evaluate(Object ctx, Map<String, Object> vars) {
            if (state.get() != State.INITIALIZED && isFirstEvaluation()) {
                T result = super.evaluate(ctx, vars);
                notifyFirstEvaluationDone();
                return result;
            }

            return super.evaluate(ctx, vars);
        }

        @Override
        public T evaluate(Object ctx, VariableResolverFactory factory) {
            if (state.get() != State.INITIALIZED && isFirstEvaluation()) {
                T result = internalEvaluate(ctx, factory);
                notifyFirstEvaluationDone();
                return result;
            }

            return internalEvaluate(ctx, factory);
        }

        private void notifyFirstEvaluationDone() {
            synchronized (state) {
                boolean shouldNotify = state.get() == State.CONTENTED;
                state.set(State.INITIALIZED);
                if (shouldNotify) {
                    state.notifyAll();
                }
            }
        }

        private boolean isFirstEvaluation() {
            if (state.compareAndSet(State.NEW, State.INITIALIZING)) {
                return true;
            }
            waitForFirstEvaluation();
            return false;
        }

        private void waitForFirstEvaluation() {
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

    private static class SynchronizedTillEvaluated<T> extends MvelEvaluator<T> {

        private final MvelEvaluator<T> monitor;

        private volatile boolean fullyEvaluated;

        public SynchronizedTillEvaluated(Serializable expr) {
            super(expr);
            this.monitor = this;
        }

        public SynchronizedTillEvaluated(MvelEvaluator<T> monitor, Serializable expr) {
            super(expr);
            this.monitor = monitor;
        }

        @Override
        public T evaluate(Object ctx, VariableResolverFactory factory) {
            if (fullyEvaluated) {
                return internalEvaluate(ctx, factory);
            }

            synchronized (monitor) {
                T result = internalEvaluate(ctx, factory);
                fullyEvaluated = isFullyEvaluated(expr);
                return result;
            }
        }

        @Override
        public T evaluate(Object ctx, Map<String, Object> vars) {
            if (fullyEvaluated) {
                return super.evaluate(ctx, vars);
            }

            synchronized (monitor) {
                T result = super.evaluate(ctx, vars);
                fullyEvaluated = isFullyEvaluated(expr);
                return result;
            }
        }
    }

    private static class FullySynchronized<T> extends MvelEvaluator<T> {

        private final MvelEvaluator<T> monitor;

        public FullySynchronized(Serializable expr) {
            super(expr);
            this.monitor = this;
        }

        public FullySynchronized(MvelEvaluator<T> monitor, Serializable expr) {
            super(expr);
            this.monitor = monitor;
        }

        @Override
        public T evaluate(Object ctx, VariableResolverFactory factory) {
            synchronized (monitor) {
                return internalEvaluate(ctx, factory);
            }
        }

        @Override
        public T evaluate(Object ctx, Map<String, Object> vars) {
            synchronized (monitor) {
                return super.evaluate(ctx, vars);
            }
        }
    }
}
