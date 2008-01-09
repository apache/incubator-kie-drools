package org.drools.workflow.core.node;

import java.util.List;

import org.drools.workflow.core.Connection;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.NodeImpl;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public abstract class SequenceNode extends NodeImpl {

    public Connection getFrom() {
        final List<Connection> list =
            getIncomingConnections(Node.CONNECTION_DEFAULT_TYPE);
        if (list.size() > 0) {
            return (Connection) list.get(0);
        }
        return null;
    }

    public Connection getTo() {
        final List<Connection> list =
            getOutgoingConnections(Node.CONNECTION_DEFAULT_TYPE);
        if (list.size() > 0) {
            return (Connection) list.get(0);
        }
        return null;
    }

    public void validateAddIncomingConnection(final String type,
            final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default incoming connection type!");
        }
        if (getIncomingConnections(Node.CONNECTION_DEFAULT_TYPE) != null
                && !getIncomingConnections(Node.CONNECTION_DEFAULT_TYPE).isEmpty()) {
            throw new IllegalArgumentException(
                "This type of node cannot have more than one incoming connection!");
        }
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default outgoing connection type!");
        }
        if (getOutgoingConnections(Node.CONNECTION_DEFAULT_TYPE) != null
                && !getOutgoingConnections(Node.CONNECTION_DEFAULT_TYPE).isEmpty()) {
            throw new IllegalArgumentException(
                "This type of node cannot have more than one outgoing connection!");
        }
    }
}
