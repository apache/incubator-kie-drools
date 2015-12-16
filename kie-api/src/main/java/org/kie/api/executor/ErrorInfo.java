/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.executor;

import java.util.Date;

/**
 * Represents Error information of the executor service data model.
 *
 */
public interface ErrorInfo {

    /**
     * Returns unique identifier of the error instance.
     * @return
     */
    Long getId();
    
    /**
     * Returns error message for the error instance.
     * @return
     */
    String getMessage();
    
    /**
     * Returns complete stack trace of the exception that generated this error instance
     * @return
     */
    String getStacktrace();
    
    /**
     * Returns exact time when the exception happened.
     * @return
     */
    Date getTime();
}
