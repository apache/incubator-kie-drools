/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.utils;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuildEvaluatorTest {

    @BeforeEach
    void setup() {
        ConfigResolverHolder.setConfigResolver(new ConfigResolver() {

            @Override
            public <T> Optional<T> getConfigProperty(String name, Class<T> clazz) {
                return name.equals("key") ? (Optional<T>) Optional.of("value") : Optional.empty();
            }
        });
    }

    @Test
    void testSecretWithinExpression() {
        assertEquals("value", BuildEvaluator.eval(ExpressionHandlerUtils.trimExpr("${ $SECRET.key}")));
    }

    @Test
    void testSecret() {
        assertEquals("value", BuildEvaluator.eval("$SECRET.key"));
    }

    @Test
    void testPlain() {
        assertEquals("key", BuildEvaluator.eval("key"));
    }
}
