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

import com.asyncapi.v3._0_0.model.AsyncAPI;
import com.asyncapi.v3._0_0.model.channel.Channel;
import com.asyncapi.v3._0_0.model.operation.Operation;
import com.asyncapi.v3._0_0.model.operation.OperationAction;

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
        Map<String, String> channelIdToAddress = buildChannelAddressMap(asyncApi);
        Map<String, AsyncChannelInfo> channelInfoByOperationId = buildOperationChannelInfoMap(asyncApi, channelIdToAddress);
        return new AsyncInfo(channelInfoByOperationId);
    }

    /**
     * Builds a mapping from channel ID to its address from the AsyncAPI channels.
     */
    private static Map<String, String> buildChannelAddressMap(AsyncAPI asyncApi) {
        Map<String, String> channelIdToAddress = new HashMap<>();
        if (asyncApi.getChannels() != null) {
            for (Entry<String, Object> ch : asyncApi.getChannels().entrySet()) {
                if (ch.getValue() instanceof Channel channel) {
                    String channelId = ch.getKey();
                    String address = Optional.ofNullable(channel.getAddress()).orElse(channelId);
                    channelIdToAddress.put(channelId, address);
                }
            }
        }
        return channelIdToAddress;
    }

    /**
     * Builds a mapping from operation ID to {@link AsyncChannelInfo} by resolving channel references.
     */
    private static Map<String, AsyncChannelInfo> buildOperationChannelInfoMap(AsyncAPI asyncApi, Map<String, String> channelIdToAddress) {
        Map<String, AsyncChannelInfo> channelInfoByOperationId = new HashMap<>();
        if (asyncApi.getOperations() != null) {
            for (Entry<String, Object> opEntry : asyncApi.getOperations().entrySet()) {
                if (opEntry.getValue() instanceof Operation op && op.getChannel() != null) {
                    String operationId = opEntry.getKey();
                    String ref = op.getChannel().getRef();
                    if (ref != null && ref.contains("/")) {
                        String channelId = ref.substring(ref.lastIndexOf('/') + 1);
                        String address = channelIdToAddress.get(channelId);
                        OperationAction action = op.getAction();
                        if (address != null && action != null) {
                            boolean publish = action == OperationAction.SEND;
                            String channelName = publish ? address + "_out" : address;
                            channelInfoByOperationId.putIfAbsent(operationId, new AsyncChannelInfo(channelName, publish));
                        }
                    }
                }
            }
        }
        return channelInfoByOperationId;
    }
}
