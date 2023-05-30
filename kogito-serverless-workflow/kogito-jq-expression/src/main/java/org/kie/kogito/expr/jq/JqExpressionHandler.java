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
package org.kie.kogito.expr.jq;

import java.util.function.Supplier;

import org.kie.kogito.process.expr.Expression;
import org.kie.kogito.serverless.workflow.utils.CachedExpressionHandler;
import org.slf4j.LoggerFactory;

import net.thisptr.jackson.jq.BuiltinFunctionLoader;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Versions;

public class JqExpressionHandler extends CachedExpressionHandler {

    private static Supplier<Scope> scopeSupplier = new DefaultScopeSupplier();

    public static void setScopeSupplier(Supplier<Scope> scopeSupplier) {
        JqExpressionHandler.scopeSupplier = scopeSupplier;
    }

    private static class DefaultScopeSupplier implements Supplier<Scope> {
        private static class DefaultScope {
            private static Scope scope;
            static {
                LoggerFactory.getLogger(JqExpressionHandler.class).info("Using default scope");
                scope = Scope.newEmptyScope();
                BuiltinFunctionLoader.getInstance().loadFunctions(Versions.JQ_1_6, scope);
            }
        }

        @Override
        public Scope get() {
            return DefaultScope.scope;
        }
    }

    @Override
    public Expression buildExpression(String expr) {
        return new JqExpression(scopeSupplier, expr, Versions.JQ_1_6);
    }

    @Override
    public String lang() {
        return JqExpression.LANG;
    }
}
