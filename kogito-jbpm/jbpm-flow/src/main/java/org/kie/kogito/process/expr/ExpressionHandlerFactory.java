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
package org.kie.kogito.process.expr;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Function;

public class ExpressionHandlerFactory {

    private ExpressionHandlerFactory() {
    }

    private static final ServiceLoader<ExpressionHandler> serviceLoader = ServiceLoader.load(ExpressionHandler.class);

    public static Expression get(String lang, String expr) {
        return getExpressionHandler(lang).orElseThrow(
                () -> new IllegalArgumentException("Unsupported language " + lang)).get(expr);
    }

    public static boolean isSupported(String lang) {
        return serviceLoader.stream().anyMatch(p -> p.get().lang().equals(lang));
    }

    public static Function<Object, String> getValueInjector(String lang) {
        return getExpressionHandler(lang).map(p -> p.getValueInjector()).orElse(Object::toString);
    }

    private static Optional<ExpressionHandler> getExpressionHandler(String lang) {
        return serviceLoader.stream().filter(p -> p.get().lang().equals(lang)).findFirst().map(p -> p.get());
    }
}
