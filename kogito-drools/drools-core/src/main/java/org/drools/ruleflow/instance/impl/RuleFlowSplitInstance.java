package org.drools.ruleflow.instance.impl;

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

import org.drools.ruleflow.core.IConnection;
import org.drools.ruleflow.core.ISplit;
import org.drools.ruleflow.instance.IRuleFlowNodeInstance;

/**
 * Runtime counterpart of a split node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleFlowSplitInstance extends RuleFlowNodeInstance
    implements
    IRuleFlowNodeInstance {

    protected ISplit getSplitNode() {
        return (ISplit) getNode();
    }

    public void trigger(final IRuleFlowNodeInstance from) {
        final ISplit split = getSplitNode();
        switch ( split.getType() ) {
            case ISplit.TYPE_AND :
                final List outgoing = split.getOutgoingConnections();
                for ( final Iterator iterator = outgoing.iterator(); iterator.hasNext(); ) {
                    final IConnection connection = (IConnection) iterator.next();
                    getProcessInstance().getNodeInstance( connection.getTo() ).trigger( this );
                }
                break;
            default :
                throw new IllegalArgumentException( "Illegal split type " + split.getType() );
        }
    }

    public void triggerCompleted() {
        // TODO Auto-generated method stub

    }

}
