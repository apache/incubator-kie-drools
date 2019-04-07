/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.core.async;

/**
 * Allows different component to mark the execution as async (timer or jbpm executor)
 * to hint other parts about some limitation e.g. security checks based on authentication
 * -security context which might not be available.
 *
 */
public class AsyncExecutionMarker {

    private static ThreadLocal<Boolean> asyncExecution = new ThreadLocal<Boolean>() {

        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
        
    };
    
    public static void markAsync() {
        asyncExecution.set(Boolean.TRUE);
    }
    
    public static void reset() {
        asyncExecution.set(Boolean.FALSE);
    }
    
    public static boolean isAsync() {
        return asyncExecution.get();
    }
}
