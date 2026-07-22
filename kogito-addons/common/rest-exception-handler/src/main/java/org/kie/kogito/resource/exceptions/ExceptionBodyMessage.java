/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.resource.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ExceptionBodyMessage {

    public static final String MESSAGE = "message";
    public static final String PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String TASK_ID = "taskId";
    public static final String VARIABLE = "variable";
    public static final String NODE_INSTANCE_ID = "nodeInstanceId";
    public static final String NODE_ID = "nodeId";
    public static final String FAILED_NODE_ID = "failedNodeId";
    public static final String ID = "id";
    public static final String ERROR_CODE = "errorCode";

    private Map<String, String> body;

    public ExceptionBodyMessage() {
        body = new HashMap<>();
    }

    public ExceptionBodyMessage(Map<String, String> body) {
        this.body = new HashMap<>(body);
    }

    public Map<String, String> getBody() {
        return body;
    }

    public String getErrorCode() {
        return body.getOrDefault(ERROR_CODE, "");
    }

    public void merge(ExceptionBodyMessage content) {
        this.body.putAll(content.body);
    }
}
