package org.drools.workflow.instance.node;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.workflow.core.Connection;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.impl.ConstraintEvaluator;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * Runtime counterpart of a split node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class SplitInstance extends NodeInstanceImpl {

    private static final long serialVersionUID = 400L;

    protected Split getSplit() {
        return (Split) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "A Split only accepts default incoming connections!");
        }
        final Split split = getSplit();
        // TODO make different strategies for each type
        switch ( split.getType() ) {
            case Split.TYPE_AND :
                getNodeInstanceContainer().removeNodeInstance(this);
                List<Connection> outgoing = split.getDefaultOutgoingConnections();
                for ( final Iterator<Connection> iterator = outgoing.iterator(); iterator.hasNext(); ) {
                    final Connection connection = (Connection) iterator.next();
                    getNodeInstanceContainer().getNodeInstance(connection.getTo()).trigger(this, connection.getToType());
                }
                break;
            case Split.TYPE_XOR :
                outgoing = split.getDefaultOutgoingConnections();
                int priority = Integer.MAX_VALUE;
                Connection selected = null;
                for ( final Iterator<Connection> iterator = outgoing.iterator(); iterator.hasNext(); ) {
                    final Connection connection = (Connection) iterator.next();
                    ConstraintEvaluator constraint = (ConstraintEvaluator) split.getConstraint( connection );
                    if ( constraint != null && constraint.getPriority() < priority ) {
                        if ( constraint.evaluate( this,
                                                  connection,
                                                  constraint ) ) {
                            selected = connection;
                            priority = constraint.getPriority();
                            break;
                        }
                    }
                }
                getNodeInstanceContainer().removeNodeInstance(this);
                if ( selected == null ) {
                    throw new IllegalArgumentException( "XOR split could not find at least one valid outgoing connection for split " + getSplit().getName() );
                }
                getNodeInstanceContainer().getNodeInstance( selected.getTo() ).trigger( this, selected.getToType() );
                break;
            case Split.TYPE_OR :
                getNodeInstanceContainer().removeNodeInstance(this);
                outgoing = split.getDefaultOutgoingConnections();
                boolean found = false;
                List<Connection> outgoingCopy = new ArrayList<Connection>(outgoing);
                while (!outgoingCopy.isEmpty()) {
                    priority = Integer.MAX_VALUE;
                    Connection selectedConnection = null;
                    ConstraintEvaluator selectedConstraint = null;
                    for ( final Iterator<Connection> iterator = outgoingCopy.iterator(); iterator.hasNext(); ) {
                        final Connection connection = (Connection) iterator.next();
                        ConstraintEvaluator constraint = (ConstraintEvaluator) split.getConstraint( connection );
    
                        if ( constraint != null  
                                && constraint.getPriority() < priority ) {
                            priority = constraint.getPriority();
                            selectedConnection = connection;
                            selectedConstraint = constraint;
                        }
                    }
                    if (selectedConstraint.evaluate( this,
                                                     selectedConnection,
                                                     selectedConstraint ) ) {
                            getNodeInstanceContainer().getNodeInstance(selectedConnection.getTo()).trigger(this, selectedConnection.getToType());
                            found = true;
                    }
                    outgoingCopy.remove(selectedConnection);
                }
                if ( !found ) {
                    throw new IllegalArgumentException( "OR split could not find at least one valid outgoing connection for split " + getSplit().getName() );
                }                
                break;
            default :
                throw new IllegalArgumentException( "Illegal split type " + split.getType() );
        }
    }
}
