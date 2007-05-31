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

import java.util.Iterator;
import java.util.List;

import org.drools.ruleflow.core.Connection;
import org.drools.ruleflow.core.RuleSetNode;

/**
 * Default implementation of a RuleSet node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleSetNodeImpl extends NodeImpl
    implements
    RuleSetNode {

    private static final long serialVersionUID = 3257285846544691769L;

    private String            ruleFlowGroup;

    public void setRuleFlowGroup(final String ruleFlowGroup) {
        this.ruleFlowGroup = ruleFlowGroup;
    }

    public String getRuleFlowGroup() {
        return this.ruleFlowGroup;
    }

    public Connection getFrom() {
        final List list = getIncomingConnections();
        if ( list.size() > 0 ) {
            return (Connection) list.get( 0 );
        }
        return null;
    }

    public Connection getTo() {
        final List list = getOutgoingConnections();
        if ( list.size() > 0 ) {
            return (Connection) list.get( 0 );
        }
        return null;
    }

    protected void validateAddIncomingConnection(final Connection connection) {
        super.validateAddIncomingConnection( connection );
        if ( getIncomingConnections().size() > 0 ) {
            throw new IllegalArgumentException( "A RuleSetNode cannot have more than one incoming node" );
        }
    }

    protected void validateAddOutgoingConnection(final Connection connection) {
        super.validateAddOutgoingConnection( connection );
        for ( final Iterator it = getOutgoingConnections().iterator(); it.hasNext(); ) {
            final Connection conn = (Connection) it.next();
            if ( conn.getType() == connection.getType() ) {
                throw new IllegalArgumentException( "A RuleSetNode can have at most one outgoing node" );
            }
        }
    }

}
