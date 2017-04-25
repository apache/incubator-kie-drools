/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.runtime.error;

import java.util.stream.Stream;

/**
 * Responsible for implementing filter capability to decide how to deal with given error.
 * Up to concrete implementations to decide on logic how to read details from the cause.
 * It might simply check on the top level exception or it can drill down into bottom
 * of the cause.
 */
public interface ExecutionErrorFilter {

    boolean accept(ExecutionErrorContext errorContext);
    
    /**
     * Based on the cause filters the actual error and produces ExecutionError instance 
     * if applicable for given filter. In case filter is not finding anything relevant it shall return 
     * null instead of execution error instance to allow others to process it.
     * @param cause the root cause of the error
     * @return ExecutionError instance populated with details extracted from the cause, or null of not applicable to this filter
     */
    ExecutionError filter(ExecutionErrorContext errorContext);
    
    /**
     * Returns expected priority in regards to other filters. The lower value returned the higher priority it has.
     * @return priority for processing
     */
    Integer getPriority();
    
    default boolean isCausedBy(Throwable throwable, Class<?>... types) {
       
        while (throwable != null) {
            final Class<?> throwableClass = throwable.getClass();
            boolean matched = Stream.of(types)
                    .filter(clazz -> clazz != null)
                    .anyMatch(clazz -> clazz.isAssignableFrom(throwableClass));
            if (matched) {
                
                return true;
            } else {
                throwable = throwable.getCause();
            }
        }

        return false;
    }
    
    @SuppressWarnings("unchecked")
    default <T> T extract(Throwable throwable, Class<T> type) {
        
        while (throwable != null) {
            if (type.isAssignableFrom(throwable.getClass())) {
                
                return (T) throwable;
            } else {
                throwable = throwable.getCause();
            }
        }

        return null;
    }
}
