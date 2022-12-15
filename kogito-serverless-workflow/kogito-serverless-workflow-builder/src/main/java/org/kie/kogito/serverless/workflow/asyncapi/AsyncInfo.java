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
package org.kie.kogito.serverless.workflow.asyncapi;

import java.util.Map;

public class AsyncInfo {

    private final Map<String, AsyncChannelInfo> operation2Channel;

    public AsyncInfo(Map<String, AsyncChannelInfo> operation2Channel) {
        super();
        this.operation2Channel = operation2Channel;
    }

    public Map<String, AsyncChannelInfo> getOperation2Channel() {
        return operation2Channel;
    }

    @Override
    public String toString() {
        return "AsyncInfo [operation2Channel=" + operation2Channel + "]";
    }
}
