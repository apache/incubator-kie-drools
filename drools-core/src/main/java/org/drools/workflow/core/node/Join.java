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

package org.drools.workflow.core.node;

import java.util.List;

import org.drools.definition.process.Connection;
import org.drools.workflow.core.impl.NodeImpl;

/**
 * Default implementation of a join.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class Join extends NodeImpl {

    public static final int TYPE_UNDEFINED     = 0;
    /**
     * The outgoing connection of a join of this type is triggered
     * when all its incoming connections have been triggered.
     */
    public static final int TYPE_AND           = 1;
    /**
     * The outgoing connection of a join of this type is triggered
     * when one of its incoming connections has been triggered.
     */
    public static final int TYPE_XOR           = 2;
    /**
     * The outgoing connection of a join of this type is triggered
     * when one of its incoming connections has been triggered. It then
     * waits until all other incoming connections have been triggered
     * before allowing 
     */
    public static final int TYPE_DISCRIMINATOR = 3;
    /**
     * The outgoing connection of a join of this type is triggered
     * when n of its incoming connections have been triggered.
     */
    public static final int TYPE_N_OF_M = 4;
    
    private static final long serialVersionUID = 510l;

    private int type;
    private String n;

    public Join() {
        this.type = TYPE_UNDEFINED;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
    
    public void setN(String n) {
    	this.n = n;
    }
    
    public String getN() {
    	return n;
    }

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default incoming connection type!");
        }
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default outgoing connection type!");
        }
        if (getTo() != null) {
            throw new IllegalArgumentException(
                "This type of node cannot have more than one outgoing connection");
        }
    }
    
}
