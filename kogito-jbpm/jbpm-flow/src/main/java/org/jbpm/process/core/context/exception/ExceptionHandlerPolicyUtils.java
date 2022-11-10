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

import java.util.regex.Pattern;

class ExceptionHandlerPolicyUtils {

    static boolean isException(String errorCode, Class<?> exceptionClass) {
        return exceptionClass.getName().equals(errorCode);
    }

    private static final Pattern classNamePattern = Pattern.compile("([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*");

    static boolean isExceptionErrorCode(String errorCode) {
        return classNamePattern.matcher(errorCode).matches();
    }

    private ExceptionHandlerPolicyUtils() {
    }
}
