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

import java.util.HashMap;
import java.util.Map;

import org.drools.knowledge.definitions.process.Connection;
import org.drools.process.instance.NodeInstance;
import org.drools.workflow.core.node.Join;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * Runtime counterpart of a join node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class JoinInstance extends NodeInstanceImpl {

    private static final long serialVersionUID = 400L;
    
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
