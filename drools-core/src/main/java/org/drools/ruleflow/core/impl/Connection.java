package org.drools.ruleflow.core.impl;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.Serializable;

import org.drools.ruleflow.core.IConnection;
import org.drools.ruleflow.core.INode;

/**
 * Default implementation of a connection.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class Connection
    implements
    IConnection,
    Serializable {

    private static final long serialVersionUID = 320L;

    private int               type;
    private Node              from;
    private Node              to;

    private Connection() {
    }

    /**
     * Creates a new connection, given a from node, a to node 
     * and a type.
     * 
     * @param from		The from node
     * @param to		The to node
     * @param type		The connection type
     */
    public Connection(final INode from,
                      final INode to,
                      final int type) {
        if ( from == null ) {
            throw new IllegalArgumentException( "From node is null!" );
        }
        if ( to == null ) {
            throw new IllegalArgumentException( "To node is null!" );
        }
        if ( from.equals( to ) ) {
            throw new IllegalArgumentException( "To and from nodes are the same!" );
        }
        this.from = (Node) from;
        this.to = (Node) to;
        this.type = type;
        this.from.addOutgoingConnection( this );
        this.to.addIncomingConnection( this );
    }

    public synchronized void terminate() {
        this.from.removeOutgoingConnection( this );
        this.to.removeIncomingConnection( this );
        this.type = 0;
        this.from = null;
        this.to = null;
    }

    public INode getFrom() {
        return this.from;
    }

    public INode getTo() {
        return this.to;
    }

    public int getType() {
        return this.type;
    }

    public boolean equals(final Object object) {
        if ( object instanceof Connection ) {
            final Connection connection = (Connection) object;
            return this.type == connection.getType() && getFrom().equals( connection.getFrom() ) && getTo().equals( connection.getTo() );
        }
        return false;
    }

    public int hashCode() {
        return (getFrom() == null ? 0 : getFrom().hashCode()) + 7 * (getTo() == null ? 0 : getTo().hashCode()) + 19 * getType();
    }

    public String toString() {
        final StringBuffer sb = new StringBuffer( "Connection " );
        sb.append( getFrom() );
        sb.append( " - " );
        sb.append( getTo() );
        sb.append( " [type=" );
        sb.append( getType() );
        sb.append( "]" );
        return sb.toString();
    }
}
