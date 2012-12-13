package org.kie.builder;

import org.kie.builder.Message.Level;

import java.util.List;

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
