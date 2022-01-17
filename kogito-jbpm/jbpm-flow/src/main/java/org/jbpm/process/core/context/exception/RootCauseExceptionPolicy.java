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

public class RootCauseExceptionPolicy implements ExceptionHandlerPolicy {
    @Override
    public boolean test(String className, Throwable exception) {
        boolean found = false;
        Throwable rootCause = exception.getCause();
        while (!found && rootCause != null) {
            found = className.equals(rootCause.getClass().getName());
            rootCause = rootCause.getCause();
        }
        return found;
    }

}
