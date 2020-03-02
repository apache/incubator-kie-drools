package org.kie.remote.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.kie.remote.TopicsConfig;
import org.kie.remote.impl.ClientUtils;
import org.kie.remote.impl.consumer.KafkaListenerThread;
import org.kie.remote.impl.consumer.Listener;
import org.kie.remote.impl.consumer.ListenerThread;
import org.kie.remote.impl.consumer.LocalListenerThread;
import org.kie.remote.impl.producer.EventProducer;
import org.kie.remote.impl.producer.LocalProducer;
import org.kie.remote.impl.producer.Producer;

public class KafkaRemoteUtil {

    private KafkaRemoteUtil(){}

    public static Listener getListener(Properties props, boolean isLocal){
        return new Listener(props, getListenerThread(TopicsConfig.getDefaultTopicsConfig(), isLocal, props));
    }

    public static ListenerThread getListenerThread(TopicsConfig topicsConfig,
                                                   boolean isLocal,
                                                   Properties configuration) {
        return isLocal ?
                new LocalListenerThread(topicsConfig) :
                new KafkaListenerThread(getMergedConf(configuration), topicsConfig);
    }

    public static Producer getProducer(boolean isLocal) {
        return isLocal ? new LocalProducer() : new EventProducer();
    }

    public static Properties getMergedConf(Properties configuration) {
        Properties conf = ClientUtils.getConfiguration(ClientUtils.CONSUMER_CONF);
        conf.putAll(configuration);
        return conf;
    }

    public static KafkaConsumer getConsumer(String topic, Properties properties) {
        KafkaConsumer consumer = new KafkaConsumer(properties);
        List<PartitionInfo> infos = consumer.partitionsFor(topic);
        List<TopicPartition> partitions = new ArrayList<>();
        if (infos != null) {
            for (PartitionInfo partition : infos) {
                partitions.add(new TopicPartition(topic, partition.partition()));
            }
        }
        consumer.assign(partitions);

        Map<TopicPartition, Long> offsets = consumer.endOffsets(partitions);
        Long lastOffset = 0l;
        for (Map.Entry<TopicPartition, Long> entry : offsets.entrySet()) {
            lastOffset = entry.getValue();
        }
        if (lastOffset == 0) {
            lastOffset = 1l;// this is to start the seek with offset -1 on empty topic
        }
        Set<TopicPartition> assignments = consumer.assignment();
        for (TopicPartition part : assignments) {
            consumer.seek(part, lastOffset - 1);
        }
        return consumer;
    }
}
