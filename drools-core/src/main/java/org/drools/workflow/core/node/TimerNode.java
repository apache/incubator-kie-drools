package org.drools.workflow.core.node;

import org.drools.definition.process.Connection;
import org.drools.process.core.timer.Timer;
import org.drools.workflow.core.impl.ExtendedNodeImpl;

public class TimerNode extends ExtendedNodeImpl {

    private static final long serialVersionUID = 400L;
    
    private Timer timer;
    
    public void setTimer(Timer timer) {
        this.timer = timer;
    }
    
    public Timer getTimer() {
        return this.timer;
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default incoming connection type!");
        }
        if (getFrom() != null) {
            throw new IllegalArgumentException(
                "This type of node cannot have more than one incoming connection!");
        }
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default outgoing connection type!");
        }
        if (getTo() != null) {
            throw new IllegalArgumentException(
                "This type of node cannot have more than one outgoing connection!");
        }
    }
    
}
