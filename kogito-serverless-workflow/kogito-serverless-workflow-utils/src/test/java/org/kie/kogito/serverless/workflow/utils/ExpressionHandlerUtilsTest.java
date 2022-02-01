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
package org.kie.kogito.serverless.workflow.utils;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils.prepareExpr;
import static org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils.trimExpr;

public class ExpressionHandlerUtilsTest {

    @Test
    void testTrimExpression() {
        assertEquals(".pepe", trimExpr("${ .pepe }"));
        assertEquals("{name:.pepe}", trimExpr("${ {name:.pepe} }"));
    }

    @Test
    void testPrepareString() {
        Map<String, String> map = Collections.singletonMap("name", "javierito");
        SecretResolverFactory.setSecretResolver(k -> map.get(k));
        assertEquals("My secret name is javierito", prepareExpr(trimExpr("${ My secret name is $SECRET.name }")));
    }
}
