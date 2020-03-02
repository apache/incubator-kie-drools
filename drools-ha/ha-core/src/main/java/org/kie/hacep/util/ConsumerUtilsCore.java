package org.kie.hacep.util;

import java.util.Properties;

import org.kie.remote.message.ControlMessage;

/**
 * Util class used to perform a single read and then close its consumer on the specific topic
 * */
public interface ConsumerUtilsCore {

    /**
     * Return the current last event in a specific topic
     *
     * @param topic name
     * @param pollTimeout
     * @return last ControlMessage inserted in the topic
     * */
    ControlMessage getLastEvent(String topic, Integer pollTimeout);

    /**
     * Return the current last event
     * @param topic name
     * @param configuration
     * @param pollTimeout
     * @return last ControlMessage inserted in the topic
     * */
    ControlMessage getLastEvent(String topic, Properties configuration, Integer pollTimeout) ;
}
