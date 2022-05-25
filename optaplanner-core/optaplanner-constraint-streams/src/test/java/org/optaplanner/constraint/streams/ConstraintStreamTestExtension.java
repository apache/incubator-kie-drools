/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.constraint.streams;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;

/**
 * This extension helps implement parameterized {@link ConstraintStream} tests. It provides 4 invocation contexts
 * representing the cartesian product of {true, false} ⨯ {DROOLS, BAVET} for a test matrix with
 * {@code constraintMatchEnabled} and {@link ConstraintStreamImplType} axes.
 * <p>
 * Each invocation context includes two additional extensions being {@link ParameterResolver parameter resolvers} that
 * populate the test class constructor with the test data. Since each CS test class has dozens of test methods
 * this is a more practical approach than using {@code @ParameterizedTest} where test data are consumed through method
 * parameters.
 */
public class ConstraintStreamTestExtension implements TestTemplateInvocationContextProvider {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        return Stream.of(true, false)
                .map(ConstraintStreamTestExtension::invocationContext);
    }

    private static TestTemplateInvocationContext invocationContext(Boolean constraintMatchEnabled) {
        return new TestTemplateInvocationContext() {

            @Override
            public String getDisplayName(int invocationIndex) {
                return "constraintMatchEnabled=" + constraintMatchEnabled;
            }

            @Override
            public List<Extension> getAdditionalExtensions() {
                return List.of(parameterResolver(boolean.class, constraintMatchEnabled));
            }
        };
    }

    private static <T> ParameterResolver parameterResolver(Class<T> type, T value) {
        return new ParameterResolver() {
            @Override
            public boolean supportsParameter(
                    ParameterContext parameterContext,
                    ExtensionContext extensionContext) throws ParameterResolutionException {
                return parameterContext.getParameter().getType().equals(type);
            }

            @Override
            public Object resolveParameter(
                    ParameterContext parameterContext,
                    ExtensionContext extensionContext) throws ParameterResolutionException {
                return value;
            }
        };
    }
}
