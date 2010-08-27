/**
 * Copyright 2010 JBoss Inc
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

package org.drools.runtime.process;

import org.drools.definition.process.Node;

/**
 * A node instance represents the execution of one specific node
 * in a process instance.  Whenever a node is reached during the
 * execution of a process instance, a node instance will be created.
 * A node instance contains all the runtime state related to the
 * execution of that node.
 * Multiple node instances for the same node can coexist in the same
 * process instance (if that node is to be executed multiple times
 * in that process instance).
 * 
 * A node instance is uniquely identified (within its node instance
 * container!) by an id.
 * 
 * Node instances can be nested, meaning that a node instance can
 * be created as part of another node instance.
 */
public interface NodeInstance {

	/**
	 * The id of the node instance.  This is unique within the
	 * node instance container this node instance lives in.
	 * 
	 * @return the id of the node instance
	 */
    long getId();

    /**
     * The id of the node this node instance refers to.  The node
     * represents the definition that this node instance was based
     * on.
     * 
     * @return the id of the node this node instance refers to
     */
    long getNodeId();
    
    /**
     * Return the node this node instance refers to.  The node
     * represents the definition that this node instance was based
     * on.
     * 
     * @return the node this node instance refers to
     */
    Node getNode();

    /**
     * The name of the node this node instance refers to.
     * @return the name of the node this node instance refers to
     */
    String getNodeName();

    /**
     * The process instance that this node instance is executing in.
     * @return the process instance that this node instance is executing in
     */
    WorkflowProcessInstance getProcessInstance();

    /**
     * The node instance container that this node instance is part of.
     * If the node was defined in the top-level process scope, this is
     * the same as the process instance.  If not, it is the node instance
     * container this node instance is executing in. 
     * 
     * @return the process instance that this node instance is executing in
     */
    NodeInstanceContainer getNodeInstanceContainer();
    
    Object getVariable(String variableName);
    
    void setVariable(String variableName, Object value);

}
