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
package org.kie.hacep.core.infra;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.kie.api.KieServices;
import org.kie.api.marshalling.KieMarshallers;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.hacep.Config;
import org.kie.hacep.EnvConfig;
import org.kie.hacep.consumer.KieContainerUtils;
import org.kie.hacep.core.KieSessionContext;
import org.kie.hacep.message.SnapshotMessage;
import org.kie.remote.impl.producer.EventProducer;
import org.kie.remote.util.SerializationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSessionSnapShooter implements SessionSnapshooter {

    private final String key = "LAST-SNAPSHOT";
    private final Logger logger = LoggerFactory.getLogger(DefaultSessionSnapShooter.class);
    private EnvConfig envConfig;

    public DefaultSessionSnapShooter(EnvConfig envConfig) {
        this.envConfig = envConfig;
    }

    public void serialize(KieSessionContext kieSessionContext, String lastInsertedEventkey, long lastInsertedEventOffset) {
        KieMarshallers marshallers = KieServices.get().getMarshallers();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            EventProducer<byte[]> producer = new EventProducer<>();
            producer.start(Config.getSnapshotProducerConfig());
            marshallers.newMarshaller(kieSessionContext.getKieSession().getKieBase()).marshall(out,
                                                                                               kieSessionContext.getKieSession());
            /* We are storing the last inserted key and offset together with the session's bytes */
            byte[] bytes = out.toByteArray();
            SnapshotMessage message = new SnapshotMessage(UUID.randomUUID().toString(),
                                                          envConfig.getKJarGAV(),
                                                          bytes,
                                                          kieSessionContext.getFhManager(),
                                                          lastInsertedEventkey,
                                                          lastInsertedEventOffset,
                                                          LocalDateTime.now());
            producer.produceSync(envConfig.getSnapshotTopicName(),
                                 key,
                                 message);
            producer.stop();
        } catch (IOException e) {
            logger.error(e.getMessage(),
                         e);
        }
    }

    public SnapshotInfos deserialize() {
        KieServices srv = KieServices.get();
        if (srv != null) {
            KafkaConsumer<String, byte[]> consumer = getConfiguredSnapshotConsumer();
            ConsumerRecords<String, byte[]> records = consumer.poll(envConfig.getPollSnapshotDuration());
            byte[] bytes = null;
            for (ConsumerRecord record : records) {
                bytes = (byte[]) record.value();
            }
            consumer.close();

            SnapshotMessage snapshotMsg = bytes != null ? SerializationUtil.deserialize(bytes) : null;
            if (snapshotMsg != null) {
                KieContainer kieContainer = null;
                KieSession kSession = null;
                try (ByteArrayInputStream in = new ByteArrayInputStream(snapshotMsg.getSerializedSession())) {

                    KieSessionConfiguration conf = srv.newKieSessionConfiguration();
                    conf.setOption(ClockTypeOption.get("pseudo"));
                    kieContainer = KieContainerUtils.getKieContainer(envConfig, srv);
                    kSession = srv.getMarshallers().newMarshaller(kieContainer.getKieBase()).unmarshall(in, conf,null);

                } catch (IOException | ClassNotFoundException e) {
                    logger.error(e.getMessage(), e);
                }
                if(kSession == null) {//Snapshot topic empty
                    kSession = kieContainer.newKieSession();
                }
                return new SnapshotInfos(kSession,
                                         kieContainer,
                                         snapshotMsg.getFhManager(),
                                         snapshotMsg.getLastInsertedEventkey(),
                                         snapshotMsg.getLastInsertedEventOffset(),
                                         snapshotMsg.getTime(),
                                         snapshotMsg.getKjarGAV());
            }
        }else{
            throw new RuntimeException("KieServices is null");
        }
        return null;
    }

    private KafkaConsumer getConfiguredSnapshotConsumer() {
        KafkaConsumer<String, byte[]> consumer = new KafkaConsumer(Config.getSnapshotConsumerConfig());
        List<PartitionInfo> partitionsInfo = consumer.partitionsFor(envConfig.getSnapshotTopicName());
        List<TopicPartition> partitions = null;
        Collection<TopicPartition> partitionCollection = new ArrayList<>();

        if (partitionsInfo != null) {
            for (PartitionInfo partition : partitionsInfo) {
                TopicPartition topicPartition = new TopicPartition(partition.topic(), partition.partition());
                if (partitions == null || partitions.contains(topicPartition)) {
                    partitionCollection.add(topicPartition);
                }
            }
            if (!partitionCollection.isEmpty()) {
                consumer.assign(partitionCollection);
            }
        }
        consumer.assignment().forEach(topicPartition -> consumer.seekToBeginning(partitionCollection));
        return consumer;
    }

    @Override
    public LocalDateTime getLastSnapshotTime() {
        KafkaConsumer<String, byte[]> consumer = getConfiguredSnapshotConsumer();
        ConsumerRecords<String, byte[]> records = consumer.poll(envConfig.getPollSnapshotDuration());
        byte[] bytes = null;
        for (ConsumerRecord record : records) {
            bytes = (byte[]) record.value();
        }
        consumer.close();
        SnapshotMessage snapshotMsg = bytes != null ? SerializationUtil.deserialize(bytes) : null;
        if (snapshotMsg != null) {
            return snapshotMsg.getTime();
        } else {
            return null;
        }
    }
}
