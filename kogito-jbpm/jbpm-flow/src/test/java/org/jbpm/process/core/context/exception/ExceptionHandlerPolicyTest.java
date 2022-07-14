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
package org.jbpm.process.core.context.exception;

import java.util.Collection;
import java.util.Iterator;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExceptionHandlerPolicyTest {

    static private Collection<ExceptionHandlerPolicy> policies;

    @BeforeAll
    static void setup() {
        policies = ExceptionHandlerPolicyFactory.getHandlerPolicies();
    }

    @Test
    void testExceptionHandlerPolicies() {
        assertEquals(4, policies.size());
    }

    @ParameterizedTest
    @ValueSource(strings = { "java.lang.RuntimeException", "Unknown error", "(?i)Status code 400", "(.*)code 4[0-9]{2}", "code 4[0-9]{2}" })
    void testExceptionHandlerPolicyFactory(String errorString) {
        Throwable exception = new IllegalStateException("Unknown error, status code 400");
        assertTrue(test(policies, errorString, exception));
    }

    @ParameterizedTest
    @ValueSource(strings = { "[" })
    void testInvalidRegexExceptionHandlerPolicyFactory(String errorString) {
        Throwable exception = new IllegalStateException("Unknown error, status code 400");
        assertFalse(test(policies, errorString, exception));
    }

    @ParameterizedTest
    @ValueSource(strings = { "HTTP:500", "500" })
    void testWebExceptionHandlerPolicyFactory(String errorString) {
        Throwable exception = new javax.ws.rs.WebApplicationException(Response.status(500).entity("Unknown error").build());
        assertTrue(test(policies, errorString, exception));
    }

    @ParameterizedTest
    @ValueSource(strings = { "HTTP:xyz", "xyz" })
    void testWebExceptionHandlerPolicyFactoryIncorrectFormat(String errorString) {
        Throwable exception = new javax.ws.rs.WebApplicationException(Response.status(500).entity("Unknown error").build());
        assertFalse(test(policies, errorString, exception));
    }

    private boolean test(Collection<ExceptionHandlerPolicy> policies, String className, Throwable exception) {
        if (className == null)
            return false;
        Iterator<ExceptionHandlerPolicy> iter = policies.iterator();
        boolean found = false;
        while (!found && iter.hasNext()) {
            found = iter.next().test(className, exception);
        }
        return found;
    }
}
