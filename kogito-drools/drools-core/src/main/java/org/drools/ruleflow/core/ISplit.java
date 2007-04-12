package org.drools.ruleflow.core;

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

import java.util.Map;

/**
 * Represents a split node in a RuleFlow. 
 * A split is a special kind of node with one incoming connection and
 * multiple outgoing connections.  The type of split decides which of the
 * outgoing connections will be triggered when the incoming connection 
 * has been triggered.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface ISplit
    extends
    INode {

    int TYPE_UNDEFINED = 0;
    /**
     * All outgoing connections of a split of this type are triggered
     * when its incoming connection has been triggered.  A split of this
     * type should have no constraints linked to any of its outgoing
     * connections.
     */
    int TYPE_AND       = 1;
    /**
     * Exactly one outgoing connection of a split of this type is triggered
     * when its incoming connection has been triggered.  Which connection
     * is based on the constraints associated with each of the connections:
     * the connection with the highest priority whose constraint is satisfied
     * is triggered.  
     */
    int TYPE_XOR       = 2;
    /**
     * One or multiple outgoing connections of a split of this type are
     * triggered when its incoming connection has been triggered.  Which
     * connections is based on the constraints associated with each of the
     * connections: all connections whose constraint is satisfied are
     * triggered.  
     */
    int TYPE_OR        = 3;

    /**
     * Sets the type of the split.
     * 
     * @param type	The type of the split
     * @throws IllegalArgumentException if type is null
     */
    void setType(int type);

    /**
     * Returns the type of the split.
     * 
     * @return the type of the split.
     */
    int getType();

    /**
     * Returns the corresponding constraint of the given outgoing connection
     * 
     * @param connection	the outgoing connection
     * @return	the corresponding constraint of the given outgoing connection
     * @throws IllegalArgumentException if <code>connection</code> is
     * not a valid outgoing connection for this split
     * @throws UnsupportedOperationException if this method is called
     * on a split with split type of something else than XOR or OR
     */
    IConstraint getConstraint(IConnection connection);

    /**
     * Method for setting a constraint corresponding to the given
     * outgoing connection
     * 
     * @param connection	the outgoing connection
     * @param constraint	the constraint 
     * @throws IllegalArgumentException if <code>connection</code> is
     * not a valid outgoing connection for this split
     * @throws UnsupportedOperationException if the split type is 
     * something else than XOR or OR
     */
    void setConstraint(IConnection connection,
                       IConstraint constraint);

    /**
     * Returns the constraints of the split.
     * 
     * @return a map containing for each connection the constraint associated with
     * that connection, or null if no constraint has been specified yet for that connection
     * @throws UnsupportedOperationException if this method is called
     * on a split with split type of something else than XOR or OR
     */
    Map getConstraints();

    /**
     * Convenience method for returning the incoming connection of the split.
     * 
     * @return the incoming connection ot the split.
     */
    IConnection getFrom();

}
