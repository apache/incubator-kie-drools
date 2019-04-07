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

package org.jbpm.services.task.exception;

import org.kie.internal.task.exception.TaskException;

/**
 * Exception that is thrown when the parameters to a query are incorrect, with regard to either the number or type. 
 *
 * @see org.jbpm.services.task.service.TaskPersistenceManager#runQueryWithParametersInTx
 */
public class IncorrectParametersException extends TaskException {

    public IncorrectParametersException(String message) {
        super(message);
    }
    
    public IncorrectParametersException(String message, Throwable cause) {
        super(message, cause);
    }
}
