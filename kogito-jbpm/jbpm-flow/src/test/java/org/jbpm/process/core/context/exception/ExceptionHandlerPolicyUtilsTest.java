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

import org.junit.jupiter.api.Test;

import static org.jbpm.process.core.context.exception.ExceptionHandlerPolicyUtils.isException;
import static org.jbpm.process.core.context.exception.ExceptionHandlerPolicyUtils.isExceptionErrorCode;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExceptionHandlerPolicyUtilsTest {

    @Test
    void testIsException() {
        assertTrue(isException("java.lang.RuntimeException", RuntimeException.class));
        assertFalse(isException("java.lang.Exception", RuntimeException.class));
        assertFalse(isException("java.lang.runtimeException", RuntimeException.class));
    }

    @Test
    void testIsExceptionClass() {
        assertTrue(isExceptionErrorCode("java.lang.RuntimeException"));
        assertTrue(isExceptionErrorCode("java.lang.Exception"));
        assertFalse(isExceptionErrorCode("pepe@hotmail.com"));
        assertFalse(isExceptionErrorCode("java.lang.runtimeException pepe"));
    }
}
