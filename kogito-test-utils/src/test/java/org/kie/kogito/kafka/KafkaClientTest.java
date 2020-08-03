/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaClientTest {

    private static final String TOPIC = "my-topic";
    private static final String MESSAGE_TO_CONSUME = "my-message-to-consume";
    private static final String MESSAGE_TO_PRODUCE = "my-message-to-produce";

    @Mock
    private KafkaProducer<String, String> producer;

    @Mock
    private KafkaConsumer<String, String> consumer;

    private KafkaClient client;
    private List<String> messages;
    private CountDownLatch waiter;

    @BeforeEach
    public void setup() {
        messages = new ArrayList<>();
        waiter = new CountDownLatch(1);
    }

    @Test
    public void shouldConsumeMessage() throws InterruptedException {
        givenKafkaClient();
        whenConsume();
        thenConsumerIsSubscribed();
        thenMessageIsReceived();
    }

    @Test
    public void shouldProduceMessage() {
        givenKafkaClient();
        whenProduceMessage();
        thenProducerIsInvoked();
    }

    @Test
    public void shouldCloseWhenShutdown() {
        givenKafkaClient();
        whenShutdown();
        thenProducerIsClosed();
        thenConsumerIsClosed();
    }

    @Test
    public void shouldNotConsumeMessageIfShutdown() {
        givenKafkaClient();
        whenShutdown();
        whenConsume();
        thenMessageIsNotReceived();
    }

    private void givenKafkaClient() {
        client = new KafkaClient(producer, consumer);
    }

    private void whenShutdown() {
        client.shutdown();
    }

    @SuppressWarnings("unchecked")
    private void whenConsume() {
        ConsumerRecord<String, String> record = mock(ConsumerRecord.class);
        ConsumerRecords<String, String> records = mock(ConsumerRecords.class);
        lenient().when(records.spliterator()).thenReturn(Collections.singleton(record).spliterator());
        lenient().when(record.value()).thenReturn(MESSAGE_TO_CONSUME);
        lenient().when(consumer.poll(any(Duration.class))).thenReturn(records);

        client.consume(TOPIC, message -> {
            messages.add(message);
            waiter.countDown();
        });
    }

    private void whenProduceMessage() {
        client.produce(MESSAGE_TO_PRODUCE, TOPIC);
    }

    private void thenProducerIsInvoked() {
        verify(producer).send(eq(new ProducerRecord<>(TOPIC, MESSAGE_TO_PRODUCE)), any());
    }

    private void thenConsumerIsSubscribed() {
        verify(consumer).subscribe(anyCollection());
    }

    private void thenProducerIsClosed() {
        verify(producer).close();
    }

    private void thenConsumerIsClosed() {
        verify(consumer).close();
    }

    private void thenMessageIsReceived() throws InterruptedException {
        waiter.await(5000, TimeUnit.MILLISECONDS);
        verify(consumer, Mockito.atLeastOnce()).commitSync();
        assertTrue(messages.contains(MESSAGE_TO_CONSUME));
    }

    private void thenMessageIsNotReceived() {
        verify(consumer, never()).commitSync();
    }

}
