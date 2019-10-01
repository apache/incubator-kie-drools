/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.model;

public class ProcessInstanceError {

    private String nodeDefinitionId;
    private String errorMessage;

    public ProcessInstanceError() {
    }

    public ProcessInstanceError(String nodeDefinitionId, String errorMessage) {
        this.nodeDefinitionId = nodeDefinitionId;
        this.errorMessage = errorMessage;
    }

    
    public String getNodeDefinitionId() {
        return nodeDefinitionId;
    }

    
    public void setNodeDefinitionId(String nodeDefinitionId) {
        this.nodeDefinitionId = nodeDefinitionId;
    }

    
    public String getErrorMessage() {
        return errorMessage;
    }

    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ProcessInstanceError [nodeDefinitionId=" + nodeDefinitionId + ", errorMessage=" + errorMessage + "]";
    }

}
