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
package org.kie.kogito.process;

public class NodeNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 8031225233775014572L;

    private final String processInstanceId;
    private final String nodeId;
    
    public NodeNotFoundException(String processInstanceId, String nodeId) {
        super("Node with id " + nodeId + " not found within process instance " + processInstanceId);
        this.processInstanceId = processInstanceId;
        this.nodeId = nodeId;
    }

    public NodeNotFoundException(String processInstanceId, String nodeId, Throwable cause) {
        super("Node with id " + nodeId + " not found within process instance " + processInstanceId, cause);
        this.processInstanceId = processInstanceId;
        this.nodeId = nodeId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }
    
    public String getNodeId() {
        return nodeId;
    }

}
