/*
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

package org.drools.definition.process;

/**
 * A NodeContainer contains a set of Nodes
 * There are different types of NodeContainers and NodeContainers may be nested.
 */
public interface NodeContainer {

	/**
	 * The Nodes of this NodeContainer.
	 * 
	 * @return the nodes
	 */
    Node[] getNodes();

	/**
	 * The node in this NodeContainer with the given id.
	 * 
	 * @return the node with the given id
	 */
    Node getNode(long id);

}
