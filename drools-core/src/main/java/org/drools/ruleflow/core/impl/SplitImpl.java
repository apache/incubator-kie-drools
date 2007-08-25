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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.ruleflow.core.Connection;
import org.drools.ruleflow.core.Constraint;
import org.drools.ruleflow.core.Split;

/**
 * Default implementation of a split node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class SplitImpl extends NodeImpl
    implements
    Split {

    private static final long serialVersionUID = 400L;

    private int               type;
    private Map               constraints;

    public SplitImpl() {
        this.type = TYPE_UNDEFINED;
        this.constraints = new HashMap();
    }

    public SplitImpl(final int type) {
        this.type = type;
        this.constraints = new HashMap();
    }

    public void setType(final int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public Constraint getConstraint(final Connection connection) {
        if ( connection == null ) {
            throw new IllegalArgumentException( "connection is null" );
        }

        // dirty hack because keys were entered wrong
        // probably caused by xstreams
        // TODO xstream 1.3.0 should fix this by default; in 1.2.2 it's fixable: http://jira.codehaus.org/browse/XSTR-363
        final HashMap newMap = new HashMap();
        for ( final Iterator it = this.constraints.entrySet().iterator(); it.hasNext(); ) {
            final Entry entry = (Entry) it.next();
            newMap.put( entry.getKey(),
                        entry.getValue() );
        }
        this.constraints = newMap;

        if ( this.type == TYPE_OR || this.type == TYPE_XOR ) {
            return (Constraint) this.constraints.get( connection );
        }
        throw new UnsupportedOperationException( "Constraints are " + "only supported with XOR or OR split types, not with: " + getType() );
    }

    public void setConstraint(final Connection connection,
                              final Constraint constraint) {
        if ( this.type == TYPE_OR || this.type == TYPE_XOR ) {
            if ( connection == null ) {
                throw new IllegalArgumentException( "connection is null" );
            }
            if ( !getOutgoingConnections().contains( connection ) ) {
                throw new IllegalArgumentException( "connection is unknown:" + connection );
            }
            this.constraints.put( connection,
                                  constraint );
        } else {
            throw new UnsupportedOperationException( "Constraints are " + "only supported with XOR or OR split types, not with type:" + getType() );
        }
    }

    public Map getConstraints() {
        if ( this.type == TYPE_OR || this.type == TYPE_XOR ) {
            return Collections.unmodifiableMap( this.constraints );
        }
        throw new UnsupportedOperationException( "Constraints are " + "only supported with XOR or OR split types, not with: " + getType() );
    }

    public Connection getFrom() {
        if ( getIncomingConnections().size() > 0 ) {
            return (Connection) getIncomingConnections().get( 0 );
        }
        return null;
    }

    protected void validateAddIncomingConnection(final Connection connection) {
        super.validateAddIncomingConnection( connection );
        if ( getIncomingConnections().size() > 0 ) {
            throw new IllegalArgumentException( "A split cannot have more than one incoming connection" );
        }
    }

    protected void validateAddOutgoingConnection(final Connection connection) {
        super.validateAddOutgoingConnection( connection );
        if ( connection.getType() != Connection.TYPE_NORMAL ) {
            throw new IllegalArgumentException( "Unknown connection type :" + connection.getType() + ", only NORMAL is allowed as outgoing connection." );
        }
    }

    public void removeOutgoingConnection(final Connection connection) {
        super.removeOutgoingConnection( connection );
        this.constraints.remove( connection );
    }
}
