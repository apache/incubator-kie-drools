package org.kie.api.event.process;

/**
 * An event when a message is sent
 */
public interface MessageEvent extends ProcessNodeEvent {

    /**
     * The name of the message
     *
     * @return message name
     */
    String getMessageName();

    /**
     * Object associated with this message
     *
     * @return message object
     */
    Object getMessage();
}
