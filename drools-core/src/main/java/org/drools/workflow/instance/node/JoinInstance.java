/**
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

package org.drools.workflow.instance.node;

import java.util.HashMap;
import java.util.Map;

import org.drools.definition.process.Connection;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.runtime.process.NodeInstance;
import org.drools.workflow.core.node.Join;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * Runtime counterpart of a join node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class JoinInstance extends NodeInstanceImpl {

    private static final long serialVersionUID = 510l;
    
    private Map<Long, Integer> triggers = new HashMap<Long, Integer>();
    
    protected Join getJoin() {
        return (Join) getNode();
    }

    public void internalTrigger(final NodeInstance from, String type) {
        if (!org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "An ActionNode only accepts default incoming connections!");
        }
        final Join join = getJoin();
        switch ( join.getType() ) {
            case Join.TYPE_XOR :
                triggerCompleted();
                break;
            case Join.TYPE_AND :
                Integer count = (Integer) this.triggers.get( from.getNodeId() );
                if ( count == null ) {
                    this.triggers.put( from.getNodeId(),
                                       1 );
                } else {
                    this.triggers.put( from.getNodeId(),
                                       count.intValue() + 1 );
                }
                if (checkAllActivated()) {
                    decreaseAllTriggers();
                    triggerCompleted();
                }
                break;
            case Join.TYPE_DISCRIMINATOR :
                boolean triggerCompleted = triggers.isEmpty();
                triggers.put( from.getNodeId(), new Integer( 1 ) );
                if (checkAllActivated()) {
                    resetAllTriggers();
                }
                if (triggerCompleted) {
                    triggerCompleted();
                }
                break;
            case Join.TYPE_N_OF_M :
                count = (Integer) this.triggers.get( from.getNodeId() );
                if ( count == null ) {
                    this.triggers.put( from.getNodeId(),
                                       1 );
                } else {
                    this.triggers.put( from.getNodeId(),
                                       count.intValue() + 1 );
                }
                int counter = 0;
                for (final Connection connection: getJoin().getDefaultIncomingConnections()) {
                    if ( this.triggers.get( connection.getFrom().getId() ) != null ) {
                        counter++;
                    }
                }
                String n = join.getN();
                Integer number = null;
                if (n.startsWith("#{") && n.endsWith("}")) {
                	n = n.substring(2, n.length() - 1);
                	VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
                		resolveContextInstance(VariableScope.VARIABLE_SCOPE, n);
                	if (variableScopeInstance == null) {
                		throw new IllegalArgumentException(
            				"Could not find variable " + n + " when executing join.");
                	}
                	Object value = variableScopeInstance.getVariable(n);
                	if (value instanceof Number) {
                		number = ((Number) value).intValue();
                	} else {
                		throw new IllegalArgumentException(
            				"Variable " + n + " did not return a number when executing join: " + value);
                	}
                } else {
	            	number = new Integer(n);
                }
                if (counter >= number) {
                    resetAllTriggers();
                    triggerCompleted();
                }
                break;
            default :
                throw new IllegalArgumentException( "Illegal join type " + join.getType() );
        }
    }

    private boolean checkAllActivated() {
        // check whether all parent nodes have been triggered 
        for (final Connection connection: getJoin().getDefaultIncomingConnections()) {
            if ( this.triggers.get( connection.getFrom().getId() ) == null ) {
                return false;
            }
        }
        return true;
    }
    
    private void decreaseAllTriggers() {
        // decrease trigger count for all incoming connections
        for (final Connection connection: getJoin().getDefaultIncomingConnections()) {
            final Integer count = (Integer) this.triggers.get( connection.getFrom().getId() );
            if ( count.intValue() == 1 ) {
                this.triggers.remove( connection.getFrom().getId() );
            } else {
                this.triggers.put( connection.getFrom().getId(),
                                   count.intValue() - 1 );
            }
        }
    }

    private void resetAllTriggers() {
        triggers.clear();
    }

    public void triggerCompleted() {
        // join nodes are only removed from the container when they contain no more state
        triggerCompleted(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE, triggers.isEmpty());
    }
    
    public Map<Long, Integer> getTriggers() {
        return triggers;
    }
    
    public void internalSetTriggers(Map<Long, Integer> triggers) {
        this.triggers = triggers;
    }
}
