package org.drools.core.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;

import org.kie.internal.security.KiePolicyHelper;
import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.integration.VariableResolverFactory;

public class MVELSafeHelper {

    private static final MVELEvaluator evaluator;

    static {
        evaluator = KiePolicyHelper.isPolicyEnabled() ? new SafeMVELEvaluator() : new RawMVELEvaluator();
    }

    private MVELSafeHelper() {
    }

    public static synchronized MVELEvaluator getEvaluator() {
        return evaluator;
    }

    public static interface MVELEvaluator {

        public Object eval(String expression);

        public Object eval(String expression, Object ctx);

        public Object eval(String expression, VariableResolverFactory resolverFactory);

        public Object eval(String expression, Object ctx, VariableResolverFactory resolverFactory);

        public Object eval(String expression, Map<String, Object> vars);

        public Object eval(String expression, Object ctx, Map<String, Object> vars);

        public <T> T eval(String expression, Class<T> toType);

        public <T> T eval(String expression, Object ctx, Class<T> toType);

        public <T> T eval(String expression, VariableResolverFactory vars, Class<T> toType);

        public <T> T eval(String expression, Map<String, Object> vars, Class<T> toType);

        public <T> T eval(String expression, Object ctx, VariableResolverFactory vars, Class<T> toType);

        public <T> T eval(String expression, Object ctx, Map<String, Object> vars, Class<T> toType);

        public String evalToString(String singleValue);

        public Object executeExpression(Object compiledExpression);

        public Object executeExpression(final Object compiledExpression, final Object ctx, final Map vars);

        public Object executeExpression(final Object compiledExpression, final Object ctx, final VariableResolverFactory resolverFactory);

        public Object executeExpression(final Object compiledExpression, final VariableResolverFactory factory);

        public Object executeExpression(final Object compiledExpression, final Object ctx);

        public Object executeExpression(final Object compiledExpression, final Map vars);

        public <T> T executeExpression(final Object compiledExpression, final Object ctx, final Map vars, Class<T> toType);

        public <T> T executeExpression(final Object compiledExpression, final Object ctx, final VariableResolverFactory vars, Class<T> toType);

        public <T> T executeExpression(final Object compiledExpression, Map vars, Class<T> toType);

        public <T> T executeExpression(final Object compiledExpression, final Object ctx, Class<T> toType);

        public void executeExpression(Iterable<CompiledExpression> compiledExpression);

        public void executeExpression(Iterable<CompiledExpression> compiledExpression, Object ctx);

        public void executeExpression(Iterable<CompiledExpression> compiledExpression, Map vars);

        public void executeExpression(Iterable<CompiledExpression> compiledExpression, Object ctx, Map vars);

        public void executeExpression(Iterable<CompiledExpression> compiledExpression, Object ctx, VariableResolverFactory vars);

    }

    public static class SafeMVELEvaluator implements MVELEvaluator {

        @Override
        public Object eval(final String expression) {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    return MVEL.eval(expression);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public Object eval(final String expression, final Object ctx) {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    return MVEL.eval(expression, ctx);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public Object eval(final String expression, final VariableResolverFactory resolverFactory) {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    return MVEL.eval(expression, resolverFactory);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public Object eval(final String expression, final Object ctx, final VariableResolverFactory resolverFactory) {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    return MVEL.eval(expression, ctx, resolverFactory);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public Object eval(final String expression, final Map<String, Object> vars) {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    return MVEL.eval(expression, vars);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public Object eval(final String expression, final Object ctx, final Map<String, Object> vars) {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    return MVEL.eval(expression, ctx, vars);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public <T> T eval(final String expression, final Class<T> toType) {
            return AccessController.doPrivileged(new PrivilegedAction<T>() {

                @Override
                public T run() {
                    return MVEL.eval(expression, toType);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public <T> T eval(final String expression, final Object ctx, final Class<T> toType) {
            return AccessController.doPrivileged(new PrivilegedAction<T>() {

                @Override
                public T run() {
                    return MVEL.eval(expression, ctx, toType);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public <T> T eval(final String expression, final VariableResolverFactory vars, final Class<T> toType) {
            return AccessController.doPrivileged(new PrivilegedAction<T>() {

                @Override
                public T run() {
                    return MVEL.eval(expression, vars, toType);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public <T> T eval(final String expression, final Map<String, Object> vars, final Class<T> toType) {
            return AccessController.doPrivileged(new PrivilegedAction<T>() {

                @Override
                public T run() {
                    return MVEL.eval(expression, vars, toType);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public <T> T eval(final String expression, final Object ctx, final VariableResolverFactory vars, final Class<T> toType) {
            return AccessController.doPrivileged(new PrivilegedAction<T>() {

                @Override
                public T run() {
                    return MVEL.eval(expression, ctx, vars, toType);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public <T> T eval(final String expression, final Object ctx, final Map<String, Object> vars, final Class<T> toType) {
            return AccessController.doPrivileged(new PrivilegedAction<T>() {

                @Override
                public T run() {
                    return MVEL.eval(expression, ctx, vars, toType);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public String evalToString(final String expression) {
            return AccessController.doPrivileged(new PrivilegedAction<String>() {
                @Override
                public String run() {
                    return MVEL.evalToString(expression);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public Object executeExpression(final Object compiledExpression) {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    return MVEL.executeExpression(compiledExpression);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public Object executeExpression(final Object compiledExpression, final Object ctx, final Map vars) {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    return MVEL.executeExpression(compiledExpression, ctx, vars);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public Object executeExpression(final Object compiledExpression, final Object ctx, final VariableResolverFactory resolverFactory) {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    return MVEL.executeExpression(compiledExpression, ctx, resolverFactory);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public Object executeExpression(final Object compiledExpression, final VariableResolverFactory factory) {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    return MVEL.executeExpression(compiledExpression, factory);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public Object executeExpression(final Object compiledExpression, final Object ctx) {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    return MVEL.executeExpression(compiledExpression, ctx);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public Object executeExpression(final Object compiledExpression, final Map vars) {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    return MVEL.executeExpression(compiledExpression, vars);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public <T> T executeExpression(final Object compiledExpression, final Object ctx, final Map vars, final Class<T> toType) {
            return AccessController.doPrivileged(new PrivilegedAction<T>() {

                @Override
                public T run() {
                    return MVEL.executeExpression(compiledExpression, ctx, vars, toType);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public <T> T executeExpression(final Object compiledExpression, final Object ctx, final VariableResolverFactory vars, final Class<T> toType) {
            return AccessController.doPrivileged(new PrivilegedAction<T>() {

                @Override
                public T run() {
                    return MVEL.executeExpression(compiledExpression, ctx, vars, toType);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public <T> T executeExpression(final Object compiledExpression, final Map vars, final Class<T> toType) {
            return AccessController.doPrivileged(new PrivilegedAction<T>() {

                @Override
                public T run() {
                    return MVEL.executeExpression(compiledExpression, vars, toType);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public <T> T executeExpression(final Object compiledExpression, final Object ctx, final Class<T> toType) {
            return AccessController.doPrivileged(new PrivilegedAction<T>() {

                @Override
                public T run() {
                    return MVEL.executeExpression(compiledExpression, ctx, toType);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public void executeExpression(final Iterable<CompiledExpression> compiledExpression) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    MVEL.executeExpression(compiledExpression);
                    return null;
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public void executeExpression(final Iterable<CompiledExpression> compiledExpression, final Object ctx) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    MVEL.executeExpression(compiledExpression, ctx);
                    return null;
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public void executeExpression(final Iterable<CompiledExpression> compiledExpression, final Map vars) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    MVEL.executeExpression(compiledExpression, vars);
                    return null;
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public void executeExpression(final Iterable<CompiledExpression> compiledExpression, final Object ctx, final Map vars) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    MVEL.executeExpression(compiledExpression, ctx, vars);
                    return null;
                }
            }, KiePolicyHelper.getAccessContext());
        }

        @Override
        public void executeExpression(final Iterable<CompiledExpression> compiledExpression, final Object ctx, final VariableResolverFactory vars) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    MVEL.executeExpression(compiledExpression, ctx, vars);
                    return null;
                }
            }, KiePolicyHelper.getAccessContext());
        }
    }

    public static class RawMVELEvaluator implements MVELEvaluator {

        @Override
        public Object eval(final String expression) {
            return MVEL.eval(expression);
        }

        @Override
        public Object eval(final String expression, final Object ctx) {
            return MVEL.eval(expression, ctx);
        }

        @Override
        public Object eval(final String expression, final VariableResolverFactory resolverFactory) {
            return MVEL.eval(expression, resolverFactory);
        }

        @Override
        public Object eval(final String expression, final Object ctx, final VariableResolverFactory resolverFactory) {
            return MVEL.eval(expression, ctx, resolverFactory);
        }

        @Override
        public Object eval(final String expression, final Map<String, Object> vars) {
            return MVEL.eval(expression, vars);
        }

        @Override
        public Object eval(final String expression, final Object ctx, final Map<String, Object> vars) {
            return MVEL.eval(expression, ctx, vars);
        }

        @Override
        public <T> T eval(final String expression, final Class<T> toType) {
            return MVEL.eval(expression, toType);
        }

        @Override
        public <T> T eval(final String expression, final Object ctx, final Class<T> toType) {
            return MVEL.eval(expression, ctx, toType);
        }

        @Override
        public <T> T eval(final String expression, final VariableResolverFactory vars, final Class<T> toType) {
            return MVEL.eval(expression, vars, toType);
        }

        @Override
        public <T> T eval(final String expression, final Map<String, Object> vars, final Class<T> toType) {
            return MVEL.eval(expression, vars, toType);
        }

        @Override
        public <T> T eval(final String expression, final Object ctx, final VariableResolverFactory vars, final Class<T> toType) {
            return MVEL.eval(expression, ctx, vars, toType);
        }

        @Override
        public <T> T eval(final String expression, final Object ctx, final Map<String, Object> vars, final Class<T> toType) {
            return MVEL.eval(expression, ctx, vars, toType);
        }

        @Override
        public String evalToString(final String expression) {
             return MVEL.evalToString(expression);
        }

        @Override
        public Object executeExpression(final Object compiledExpression) {
            return MVEL.executeExpression(compiledExpression);
        }

        @Override
        public Object executeExpression(final Object compiledExpression, final Object ctx, final Map vars) {
            return MVEL.executeExpression(compiledExpression, ctx, vars);
        }

        @Override
        public Object executeExpression(final Object compiledExpression, final Object ctx, final VariableResolverFactory resolverFactory) {
            return MVEL.executeExpression(compiledExpression, ctx, resolverFactory);
        }

        @Override
        public Object executeExpression(final Object compiledExpression, final VariableResolverFactory factory) {
            return MVEL.executeExpression(compiledExpression, factory);
        }

        @Override
        public Object executeExpression(final Object compiledExpression, final Object ctx) {
            return MVEL.executeExpression(compiledExpression, ctx);
        }

        @Override
        public Object executeExpression(final Object compiledExpression, final Map vars) {
            return MVEL.executeExpression(compiledExpression, vars);
        }

        @Override
        public <T> T executeExpression(final Object compiledExpression, final Object ctx, final Map vars, final Class<T> toType) {
            return MVEL.executeExpression(compiledExpression, ctx, vars, toType);
        }

        @Override
        public <T> T executeExpression(final Object compiledExpression, final Object ctx, final VariableResolverFactory vars, final Class<T> toType) {
            return MVEL.executeExpression(compiledExpression, ctx, vars, toType);
        }

        @Override
        public <T> T executeExpression(final Object compiledExpression, final Map vars, final Class<T> toType) {
            return MVEL.executeExpression(compiledExpression, vars, toType);
        }

        @Override
        public <T> T executeExpression(final Object compiledExpression, final Object ctx, final Class<T> toType) {
            return MVEL.executeExpression(compiledExpression, ctx, toType);
        }

        @Override
        public void executeExpression(final Iterable<CompiledExpression> compiledExpression) {
            MVEL.executeExpression(compiledExpression);
        }

        @Override
        public void executeExpression(final Iterable<CompiledExpression> compiledExpression, final Object ctx) {
            MVEL.executeExpression(compiledExpression, ctx);
        }

        @Override
        public void executeExpression(final Iterable<CompiledExpression> compiledExpression, final Map vars) {
            MVEL.executeExpression(compiledExpression, vars);
        }

        @Override
        public void executeExpression(final Iterable<CompiledExpression> compiledExpression, final Object ctx, final Map vars) {
            MVEL.executeExpression(compiledExpression, ctx, vars);
        }

        @Override
        public void executeExpression(final Iterable<CompiledExpression> compiledExpression, final Object ctx, final VariableResolverFactory vars) {
            MVEL.executeExpression(compiledExpression, ctx, vars);
        }

    }

}
