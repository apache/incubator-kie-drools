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
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.kie.remote.message.Message;
import org.kie.remote.message.ResultMessage;

import static org.kie.remote.util.SerializationUtil.serialize;

public class EventProducer<T> implements Producer {

    protected org.apache.kafka.clients.producer.Producer<String, T> producer;

    @Override
    public void start(Properties properties) {
        producer = new KafkaProducer(properties);
    }

    @Override
    public void stop() {
        if (producer != null) {
            producer.flush();
            producer.close();
        }
    }

    @Override
    public <T> void produceSync(String topicName, String key, ResultMessage<T> object) {
        internalProduceSync(topicName, key, object);
    }

    @Override
    public void produceSync(String topicName, String key, Message object) {
        internalProduceSync(topicName, key, object);
    }

    protected void internalProduceSync(String topicName, String key, Object object) {
       try {
            producer.send(getFreshProducerRecord(topicName, key, object)).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    private ProducerRecord<String, T> getFreshProducerRecord(String topicName, String key, Object object) {
        return new ProducerRecord<>(topicName, key, (T) serialize(object));
    }
}
