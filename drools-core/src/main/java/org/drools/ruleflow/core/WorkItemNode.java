package org.drools.ruleflow.core;

import org.drools.ruleflow.common.core.Work;

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
 * Represents a task in a RuleFlow.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface WorkItemNode
    extends
    Node {

    /**
     * Returns the incoming connection of the TaskNode.
     * 
     * @return the incoming connection of the TaskNode.
     */
    Connection getFrom();

    /**
     * Returns the outgoing connection of the TaskNode.
     * 
     * @return the outgoing connection of the TaskNode.
     */
    Connection getTo();

    /**
     * Returns the work of the WorkItemNode.
     * 
     * @return the work of the WorkItemNode.
     */
    Work getWork();

    /**
     * Sets the work of the WorkItemNode.
     * 
     * @param constraint	The work of the WorkItemNode
     */
    void setWork(Work work);
}
