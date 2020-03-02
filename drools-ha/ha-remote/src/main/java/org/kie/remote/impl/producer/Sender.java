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
package org.kie.remote.impl.producer;

import java.util.Properties;

import org.kie.remote.command.RemoteCommand;
import org.kie.remote.impl.ClientUtils;

public class Sender {

    private Producer producer;
    private Properties configuration;

    public Sender(Properties configuration, Producer producer) {
        this.configuration = configuration != null && !configuration.isEmpty() ?
                configuration :
                ClientUtils.getConfiguration(ClientUtils.PRODUCER_CONF);
        this.producer = producer;
    }

    public void start() {
        producer.start(configuration);
    }

    public void stop() {
        producer.stop();
    }

    public void sendCommand(RemoteCommand command, String topicName) {
        producer.produceSync(topicName, command.getId(), command);
    }
}
