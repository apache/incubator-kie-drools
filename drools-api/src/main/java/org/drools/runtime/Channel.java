package org.drools.runtime;

/**
 * <p>
 * A channel provides a mechanism to send objects from the working memory to some external process 
 * or function.  For instance, a channel can be used to inform some piece of code that an object 
 * matches a rule.  
 * </p>
 *
 * <p>
 * To create a channel, implement the interface and register it with the KnowledgeRuntime:
 * </p>
 * <pre>
 * ...
 * ksession.registerChannel("my-channel", new MyChannelImpl());
 * </pre>
 * 
 * <p>
 * Channels are invoked from the consequence side of a rule:
 * </p>
 * <pre>
 * when
 *   ...
 * then
 *   channels["my-channel"].send(...);
 * </pre>
 */
public interface Channel {

    /**
     * Sends the given object to this channel.
     * 
     * @param object
     */
    void send(Object object);
}
