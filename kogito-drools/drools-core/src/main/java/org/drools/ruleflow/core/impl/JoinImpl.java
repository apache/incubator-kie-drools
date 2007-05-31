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

import org.drools.ruleflow.core.Connection;
import org.drools.ruleflow.core.Join;

/**
 * Default implementation of a join.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class JoinImpl extends NodeImpl
    implements
    Join {

    private static final long serialVersionUID = 3257004367155573815L;

    private int               type;

    public JoinImpl() {
        this.type = TYPE_UNDEFINED;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public Connection getTo() {
        if ( getOutgoingConnections().size() > 0 ) {
            return (Connection) getOutgoingConnections().get( 0 );
        }
        return null;
    }

    protected void validateAddOutgoingConnection(final Connection connection) {
        super.validateAddOutgoingConnection( connection );
        if ( connection.getType() != Connection.TYPE_NORMAL ) {
            throw new IllegalArgumentException( "Unknown connection type :" + connection.getType() + ", only NORMAL is allowed as outgoing connection." );
        }
        if ( getOutgoingConnections().size() > 0 ) {
            throw new IllegalArgumentException( "A join cannot have more than one outgoing connection" );
        }
    }

}
