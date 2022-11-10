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

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExceptionScopeTest {
    @Test
    void testMatchingPolicies() {
        ExceptionScope exceptionScope = new ExceptionScope();
        ExceptionHandler handler1 = Mockito.mock(ExceptionHandler.class);
        ExceptionHandler handler2 = Mockito.mock(ExceptionHandler.class);
        ExceptionHandler handler3 = Mockito.mock(ExceptionHandler.class);
        exceptionScope.setExceptionHandler(IOException.class.getCanonicalName(), handler1);
        exceptionScope.setExceptionHandler(RuntimeException.class.getCanonicalName(), handler2);
        exceptionScope.setExceptionHandler(Exception.class.getCanonicalName(), handler3);
        assertEquals(handler1, exceptionScope.getHandlerFromPolicies(new IOException()));
        assertEquals(handler2, exceptionScope.getHandlerFromPolicies(new RuntimeException(new IOException())));
        assertEquals(handler3, exceptionScope.getHandlerFromPolicies(new Exception(new RuntimeException())));
    }
}
