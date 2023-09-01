package org.kie.api.builder;

import java.util.List;

import org.kie.api.builder.Message.Level;

/**
 * The Results of the building process of a KieModule
 */
public interface Results {

    /**
     * Returns true if these Results contains at least one Message of one of the given levels
     */
    boolean hasMessages(Level... levels);

    /**
     * Returns all the Messages of the given levels in these Results
     */
    List<Message>  getMessages(Level... levels);

    /**
     * Returns all the Messages in these Results
     */
    List<Message>  getMessages();
}
