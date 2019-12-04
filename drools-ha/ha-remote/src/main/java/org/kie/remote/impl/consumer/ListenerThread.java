/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.remote.impl.consumer;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.kie.remote.TopicsConfig;
import org.kie.remote.impl.ClientUtils;
import org.kie.remote.message.ResultMessage;
import org.slf4j.Logger;

import static org.kie.remote.CommonConfig.LOCAL_MESSAGE_SYSTEM_CONF;
import static org.kie.remote.util.ConfigurationUtil.readBoolean;

public interface ListenerThread extends Runnable {

    void stop();

    static ListenerThread get(TopicsConfig topicsConfig, Map<String, CompletableFuture<Object>> requestsStore, Properties configuration) {
        return get(topicsConfig, requestsStore, readBoolean(configuration, LOCAL_MESSAGE_SYSTEM_CONF), configuration);
    }

    static ListenerThread get(TopicsConfig topicsConfig, Map<String, CompletableFuture<Object>> requestsStore, boolean isLocal, Properties configuration) {
        return isLocal ?
                new LocalListenerThread(topicsConfig, requestsStore) :
                new KafkaListenerThread(getMergedConf(configuration), topicsConfig, requestsStore);
    }

    static Properties getMergedConf(Properties configuration){
        Properties conf = ClientUtils.getConfiguration(ClientUtils.CONSUMER_CONF);
        conf.putAll(configuration);
        return conf;
    }

    default void complete(Map<String, CompletableFuture<Object>> requestsStore, ResultMessage message, Logger logger) {
        CompletableFuture<Object> completableFuture = requestsStore.get(message.getId());
        if(completableFuture!= null) {
            completableFuture.complete(message.getResult());
            if(logger.isDebugEnabled()){
                logger.debug("completed msg with key {}",message.getId());
            }
        }
    }
}
