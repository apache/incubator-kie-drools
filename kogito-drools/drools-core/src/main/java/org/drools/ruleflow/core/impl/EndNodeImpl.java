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

import java.util.List;

import org.drools.ruleflow.core.Connection;
import org.drools.ruleflow.core.EndNode;

/**
 * Default implementation of an end node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class EndNodeImpl extends NodeImpl
    implements
    EndNode {

    private static final long serialVersionUID = 400L;

    public Connection getFrom() {
        final List list = getIncomingConnections();
        if ( list.size() > 0 ) {
            return (Connection) list.get( 0 );
        }
        return null;
    }

    protected void validateAddIncomingConnection(final Connection connection) {
        super.validateAddIncomingConnection( connection );
        if ( getIncomingConnections().size() > 0 ) {
            throw new IllegalArgumentException( "An end node cannot have more than one incoming connection" );
        }
    }

    protected void validateAddOutgoingConnection(final Connection connection) {
        throw new UnsupportedOperationException( "An end node does not have an outgoing connection" );
    }

    protected void validateRemoveOutgoingConnection(final Connection connection) {
        throw new UnsupportedOperationException( "An end node does not have an outgoing connection" );
    }
}
