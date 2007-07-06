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
 * Represents a milestone in a RuleFlow.
 * A milestone has an associated constraint.
 * Flow will only continue if this constraint has been satisfied.  
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface MilestoneNode
    extends
    Node {

    /**
     * Returns the incoming connection of the MilestoneNode.
     * 
     * @return the incoming connection of the MilestoneNode.
     */
    Connection getFrom();

    /**
     * Returns the outgoing connection of the MilestoneNode.
     * 
     * @return the outgoing connection of the MilestoneNode.
     */
    Connection getTo();

    /**
     * Returns the constraint of the MilestoneNode.
     * 
     * @return the constraint of the MilestoneNode.
     */
    String getConstraint();

    /**
     * Sets the ruleflow-group of the MilestoneNode.
     * 
     * @param constraint	The constraint of the MilestoneNode
     * @throws IllegalArgumentException if constraint is null
     */
    void setConstraint(String constraint);
}
