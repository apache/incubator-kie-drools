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

package org.drools.util;

/**
 * MultiLinkedListNode
 * This is a specialization of the LinkedListNode that also keeps reference to 
 * a child node (that might me member of another LinkedList) and the containing
 * LinkedList.
 *
 * @author <a href="mailto:edson.tirelli@auster.com.br">Edson Tirelli</a>
 *
 * Created: 12/02/2006
 */
public interface MultiLinkedListNode
    extends
    LinkedListNode {

    /**
     * Returns the child node
     * @return
     *      The child LinkedListNode
     */
    public MultiLinkedListNode getChild();

    /**
     * Sets the child node 
     * @param child
     *      The child LinkedListNode
     */
    public void setChild(MultiLinkedListNode child);

    /**
     * Returns the containing LinkedList
     * @return
     *      The containing LinkedList
     */
    public LinkedList getLinkedList();

    /**
     * Sets the containing LinkedList 
     * @param list
     *      The containing LinkedListNode
     */
    public void setLinkedList(LinkedList list);

}
