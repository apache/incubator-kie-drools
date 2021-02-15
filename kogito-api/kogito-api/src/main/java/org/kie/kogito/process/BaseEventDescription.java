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

import java.util.Map;

public class BaseEventDescription extends AbstractEventDescription<NamedDataType> {

    public BaseEventDescription(String event, String nodeId, String nodeName, String eventType, String nodeInstanceId, String processInstanceId, NamedDataType dataType, Map<String, String> properties) {
        super(event, nodeId, nodeName, eventType, nodeInstanceId, processInstanceId, dataType, properties);
    }

    public BaseEventDescription(String event, String nodeId, String nodeName, String eventType, String nodeInstanceId, String processInstanceId, NamedDataType dataType) {
        super(event, nodeId, nodeName, eventType, nodeInstanceId, processInstanceId, dataType);
    }

    
}
