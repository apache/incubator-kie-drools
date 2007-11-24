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
 * Represents a sub-flow in a RuleFlow.
 * The node will continue if the sub-flow has ended.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface SubFlowNode
    extends
    Node {

    /**
     * Returns the incoming connection of the SubFlowNode.
     * 
     * @return the incoming connection of the SubFlowNode.
     */
    Connection getFrom();

    /**
     * Returns the outgoing connection of the SubFlowNode.
     * 
     * @return the outgoing connection of the SubFlowNode.
     */
    Connection getTo();

    /**
     * Returns the process id of the SubFlowNode.
     * 
     * @return the process id of the SubFlowNode.
     */
    String getProcessId();

    /**
     * Sets the process id of the SubFlowNode.
     * 
     * @param processId	The process id of the SubFlowNode
     */
    void setProcessId(String processId);
    
    /**
     * Sets whether this node should wait until the sub-flow has been
     * completed.
     * 
     * @param waitForCompletion  whether this node should wait until the sub-flow has been completed
     */
    void setWaitForCompletion(boolean waitForCompletion);

    /**
     * Returns whether this node should wait until the sub-flow has been
     * completed.
     * 
     * @return whether this node should wait until the sub-flow has been
     * completed.
     */
    boolean isWaitForCompletion();

}
