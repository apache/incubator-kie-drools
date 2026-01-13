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
package org.kie.kogito.quarkus.serverless.workflow.asyncapi;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.kie.kogito.serverless.workflow.asyncapi.AsyncChannelInfo;
import org.kie.kogito.serverless.workflow.asyncapi.AsyncInfo;
import org.kie.kogito.serverless.workflow.asyncapi.AsyncInfoConverter;

import com.asyncapi.v2._6_0.model.AsyncAPI;
import com.asyncapi.v2._6_0.model.channel.ChannelItem;
import com.asyncapi.v2._6_0.model.channel.operation.Operation;

import io.quarkiverse.asyncapi.config.AsyncAPIRegistry;

public class AsyncAPIInfoConverter implements AsyncInfoConverter {

    private final AsyncAPIRegistry registry;

    public AsyncAPIInfoConverter(AsyncAPIRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Optional<AsyncInfo> apply(String id) {
        return registry.getAsyncAPI(id).map(AsyncAPIInfoConverter::from);
    }

    private static AsyncInfo from(AsyncAPI asyncApi) {
        Map<String, AsyncChannelInfo> map = new HashMap<>();
        for (Entry<String, ChannelItem> entry : asyncApi.getChannels().entrySet()) {
            addChannel(map, entry.getValue().getPublish(), entry.getKey() + "_out", true);
            addChannel(map, entry.getValue().getSubscribe(), entry.getKey(), false);
        }
        return new AsyncInfo(map);
    }

    private static void addChannel(Map<String, AsyncChannelInfo> map, Operation operation, String channelName, boolean publish) {
        if (operation != null) {
            String operationId = operation.getOperationId();
            if (operationId != null) {
                map.putIfAbsent(operationId, new AsyncChannelInfo(channelName, publish));
            }
        }
    }
}
