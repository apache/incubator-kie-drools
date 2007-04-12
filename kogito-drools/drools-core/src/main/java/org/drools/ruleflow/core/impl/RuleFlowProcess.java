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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.ruleflow.common.core.impl.Process;
import org.drools.ruleflow.core.IEndNode;
import org.drools.ruleflow.core.INode;
import org.drools.ruleflow.core.IRuleFlowProcess;
import org.drools.ruleflow.core.IStartNode;
import org.drools.ruleflow.core.IVariable;

/**
 * Default implementation of a RuleFlow process.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleFlowProcess extends Process
    implements
    IRuleFlowProcess {

    public static final String RULEFLOW_TYPE    = "RuleFlow";

    private static final long  serialVersionUID = 3257005445309609272L;

    private Map                nodes;
    private List               variables;
    private long               lastNodeId;

    public RuleFlowProcess() {
        super();
        setType( RULEFLOW_TYPE );
        this.nodes = new HashMap();
        this.variables = new ArrayList();
    }

    public IStartNode getStart() {
        for ( final Iterator it = this.nodes.values().iterator(); it.hasNext(); ) {
            final INode node = (INode) it.next();
            if ( node instanceof IStartNode ) {
                return (IStartNode) node;
            }
        }
        return null;
    }

    public INode[] getNodes() {
        return (INode[]) this.nodes.values().toArray( new INode[this.nodes.size()] );
    }

    public INode getNode(final long id) {
        final Long idLong = new Long( id );
        if ( !this.nodes.containsKey( idLong ) ) {
            throw new IllegalArgumentException( "Unknown node id: " + id );
        }
        return (INode) this.nodes.get( idLong );
    }

    private IEndNode getEnd() {
        for ( final Iterator it = this.nodes.values().iterator(); it.hasNext(); ) {
            final INode node = (INode) it.next();
            if ( node instanceof IEndNode ) {
                return (IEndNode) node;
            }
        }
        return null;
    }

    public void removeNode(final INode node) {
        if ( node == null ) {
            throw new IllegalArgumentException( "Node is null" );
        }
        final INode n = (INode) this.nodes.remove( new Long( node.getId() ) );
        if ( n == null ) {
            throw new IllegalArgumentException( "Unknown node: " + node );
        }
    }

    public List getVariables() {
        return this.variables;
    }

    public void setVariables(final List variables) {
        if ( variables == null ) {
            throw new IllegalArgumentException( "Variables is null" );
        }
        this.variables = variables;
    }

    public String[] getVariableNames() {
        final String[] result = new String[this.variables.size()];
        for ( int i = 0; i < this.variables.size(); i++ ) {
            result[i] = ((IVariable) this.variables.get( i )).getName();
        }
        return result;
    }

    public void addNode(final INode node) {
        validateAddNode( node );
        if ( !this.nodes.containsValue( node ) ) {
            node.setId( ++this.lastNodeId );
            this.nodes.put( new Long( node.getId() ),
                            node );
        }
    }

    private void validateAddNode(final INode node) {
        if ( (node instanceof IStartNode) && (getStart() != null) ) {
            throw new IllegalArgumentException( "A ruleflow process cannot have more than one start node!" );
        }
        if ( (node instanceof IEndNode) && (getEnd() != null) ) {
            throw new IllegalArgumentException( "A ruleflow process cannot have more than one end node!" );
        }
    }
}
