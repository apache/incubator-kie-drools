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
 * Represents a node containing a set of rules in a RuleFlow. 
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface IRuleSetNode
    extends
    INode {

    /**
     * Returns the incoming connection of the RuleSetNode.
     * 
     * @return the incoming connection of the RuleSetNode.
     */
    IConnection getFrom();

    /**
     * Returns the outgoing connection of the RuleSetNode.
     * 
     * @return the outgoing connection of the RuleSetNode.
     */
    IConnection getTo();

    /**
     * Returns the ruleflow-group of the RuleSetNode.
     * 
     * @return the ruleflow-group of the RuleSetNode.
     */
    String getRuleFlowGroup();

    /**
     * Sets the ruleflow-group of the RuleSetNode.
     * 
     * @param ruleFlowGroup	The ruleflow-group of the RuleSetNode
     * @throws IllegalArgumentException if type is null
     */
    void setRuleFlowGroup(String ruleFlowGroup);
}
