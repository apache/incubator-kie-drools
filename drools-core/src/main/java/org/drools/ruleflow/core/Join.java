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

/**
 * Represents a join node in a RuleFlow. 
 * A join is a special kind of node with multiple incoming connections and
 * one outgoing connection.  The type of join decides when the outgoing
 * connections will be triggered (based on which incoming connections have
 * been triggered).
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface Join
    extends
    Node {

    int TYPE_UNDEFINED = 0;
    /**
     * The outgoing connection of a join of this type is triggered
     * when all its incoming connections have been triggered.
     */
    int TYPE_AND       = 1;
    /**
     * The outgoing connection of a join of this type is triggered
     * when one of its incoming connections has been triggered.
     */
    int TYPE_XOR       = 2;

    /**
     * Sets the type of the join.
     * 
     * @param type	The type of the join
     * @throws IllegalArgumentException if type is null
     */
    void setType(int type);

    /**
     * Returns the type of the join.
     * 
     * @return the type of the join.
     */
    int getType();

    /**
     * Convenience method for returning the outgoing connection of the join
     * 
     * @return the outgoing connection of the join
     */
    Connection getTo();

}
