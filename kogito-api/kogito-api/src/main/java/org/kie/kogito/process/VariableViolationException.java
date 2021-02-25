/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.process;

/**
 * Thrown when there is any kind of variable violation such as missing required variable
 * or attempt to set already defined readonly variable.
 * 
 */
public class VariableViolationException extends RuntimeException {

    private static final long serialVersionUID = 8031225233775014572L;

    private final String processInstanceId;
    private final String variableName;
    private final String errorMessage;

    public VariableViolationException(String processInstanceId, String variableName, String errorMessage) {
        super("Variable '" + variableName + "' in process instance '" + (processInstanceId == null ? "unknown" : processInstanceId) + "' violated");
        this.processInstanceId = processInstanceId;
        this.variableName = variableName;
        this.errorMessage = errorMessage;
    }

    /**
     * Returns process instance id of the instance that failed.
     * 
     * @return process instance id
     */
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    /**
     * Returns variable name that was violated
     * 
     * @return variable name
     */
    public String getVariableName() {
        return variableName;
    }

    /**
     * Returns error message associated with this failure.
     * 
     * @return error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

}
