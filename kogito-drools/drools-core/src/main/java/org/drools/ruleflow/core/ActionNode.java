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
 * Represents an actino in a RuleFlow.
 * An action represents the task that should be performed
 * when executing this node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface ActionNode
    extends
    Node {

    /**
     * Returns the incoming connection of the ActionNode.
     * 
     * @return the incoming connection of the ActionNode.
     */
    Connection getFrom();

    /**
     * Returns the outgoing connection of the ActionNode.
     * 
     * @return the outgoing connection of the ActionNode.
     */
    Connection getTo();

    /**
     * Returns the action of the ActionNode.
     * 
     * @return the action of the ActionNode.
     */
    Object getAction();

    /**
     * Sets the action of the ActionNode.
     * 
     * @param constraint	The action of the ActionNode
     */
    void setAction(Object action);
}
