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
package org.kie.hacep;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {

    public static final String BOOTSTRAP_SERVERS_KEY = "bootstrap.servers";
    public static final String DEFAULT_KAFKA_PORT = "9092";
    public static final String NAMESPACE = "namespace";
    public static final String DEFAULT_CONTROL_TOPIC = "control";
    public static final String DEFAULT_SNAPSHOT_TOPIC = "snapshot";
    public static final String ITERATION_BETWEEN_SNAPSHOT = "iteration.between.snapshot";
    public static final int DEFAULT_ITERATION_BETWEEN_SNAPSHOT = 10;
    public static final String DEFAULT_PRINTER_TYPE = "printer.type";
    public static final String MAX_SNAPSHOT_AGE = "max.snapshot.age";
    public static final String DEFAULT_MAX_SNAPSHOT_AGE_SEC = "600";
    public static final String MY_CLUSTER_KAFKA_BOOTSTRAP_SERVICE_HOST = "MY_CLUSTER_KAFKA_BOOTSTRAP_SERVICE_HOST";
    public static final String BROKER_URL = System.getenv(MY_CLUSTER_KAFKA_BOOTSTRAP_SERVICE_HOST);
    public static final int DEFAULT_POLL_TIMEOUT = 1000;
    public static final int DEFAULT_POLL_SNAPSHOT_TIMEOUT = 1;
    public static final String POLL_TIMEOUT = "poll.timeout";
    public static final String POLL_TIMEOUT_SNAPSHOT = "poll.timeout.snapshot";
    public static final String POLL_TIMEOUT_UNIT = "poll.timeout.unit";
    public static final String POLL_TIMEOUT_UNIT_SNAPSHOT = "poll.timeout.unit.snapshot";
    public static final PollUnit POLL_TIMEOUT_DEFAULT_UNIT = PollUnit.MILLISECOND;
    public static final PollUnit POLL_TIMEOUT_SNAPSHOT_DEFAULT_UNIT = PollUnit.SECOND;
    public static final String SKIP_ON_DEMAND_SNAPSHOT = "skip.ondemandsnapshoot";
    public static final String TEST = Boolean.FALSE.toString();
    public static final String UNDER_TEST = "undertest";
    public static final String MAX_SNAPSHOT_REQUEST_ATTEMPTS = "max.snapshot.request.attempts";
    public static final String UPDATABLE_KJAR = "UPDATABLEKJAR";
    public static final String KJAR_GAV = "KJARGAV";
    public static final String DEFAULT_MAX_SNAPSHOT_REQUEST_ATTEMPTS = "30";
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static final String CONSUMER_CONF = "consumer.properties";
    private static final String PRODUCER_CONF = "producer.properties";
    private static final String CONF = "infra.properties";
    private static Properties consumerConf;
    private static Properties producerConf;
    private static Properties snapshotConsumerConf;
    private static Properties snapshotProducerConf;

    private Config() { }

    public static String getBootStrapServers() {
        StringBuilder sb = new StringBuilder();
        sb.append(BROKER_URL).append(":").append(DEFAULT_KAFKA_PORT);
        //append("my-cluster-kafka-bootstrap.my-kafka-project.svc:9092");//plain
        //.append(",").append("my-cluster-kafka-brokers.my-kafka-project.svc").append(":9093");//tls
        return sb.toString();
    }

    public static Properties getDefaultConfig() {
        return getDefaultConfigFromProps(CONF);
    }

    public static Properties getConsumerConfig(String caller) {
        if (consumerConf == null) {
            consumerConf = getDefaultConfigFromProps(CONSUMER_CONF);
        }
        logConfig(caller, consumerConf);
        return consumerConf;
    }

    public static Properties getProducerConfig(String caller) {
        if (producerConf == null) {
            producerConf = getDefaultConfigFromProps(PRODUCER_CONF);
        }
        logConfig(caller, producerConf);
        return producerConf;
    }

    public static Properties getSnapshotConsumerConfig() {
        if (snapshotConsumerConf == null) {
            snapshotConsumerConf = getDefaultConfigFromProps(CONSUMER_CONF);
        }
        logConfig("SnapshotConsumer", snapshotConsumerConf);
        return snapshotConsumerConf;
    }

    public static Properties getSnapshotProducerConfig() {
        if (snapshotProducerConf == null) {
            snapshotProducerConf = getDefaultConfigFromProps(PRODUCER_CONF);
        }
        logConfig("SnapshotProducer", snapshotProducerConf);
        return snapshotProducerConf;
    }

    public static Properties getDefaultConfigFromProps(String fileName) {
        Properties config = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream(fileName)) {
            config.load(in);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
        config.put(BOOTSTRAP_SERVERS_KEY, config.getOrDefault(BOOTSTRAP_SERVERS_KEY, getBootStrapServers()));
        return config;
    }

    private static void logConfig(String subject,
                                  Properties producerProperties) {
        if (logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append(subject);
            sb.append("\n{\n");
            for (Map.Entry<Object, Object> entry : producerProperties.entrySet()) {
                sb.append(" ").append(entry.getKey().toString()).append(":").append(entry.getValue()).append("  \n");
            }
            sb.append("\n}\n");
            logger.debug(sb.toString());
        }
    }
}
