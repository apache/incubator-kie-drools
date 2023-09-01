package org.kie.api.definition.process;

import java.util.Map;

/**
 * A connection is a link from one Node to another.
 */
public interface Connection {

    /**
     * The Node the connection starts from.
     */
    Node getFrom();

    /**
     * The Node the connection goes to.
     */
    Node getTo();

    /**
     * The type of exit point of the from Node.  Defaults to "DROOLS_DEFAULT".
     */
    String getFromType();

    /**
     * The type of entry point of the to Node.  Defaults to "DROOLS_DEFAULT".
     */
    String getToType();

    /**
     * Meta data associated with this connection.
     */
    Map<String, Object> getMetaData();

}
