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

package org.drools.reteoo;

/**
 * Items placed in a <code>LinkedList<code> must implement this interface .
 * 
 * @see LeftTupleSinkNodeList
 * 
 */
public interface ObjectSinkNode
    extends
    ObjectSink {

    /**
     * Returns the next node
     * @return
     *      The next LinkedListNode
     */

    public ObjectSinkNode getNextObjectSinkNode();

    /**
     * Sets the next node 
     * @param next
     *      The next LinkedListNode
     */
    public void setNextObjectSinkNode(ObjectSinkNode next);

    /**
     * Returns the previous node
     * @return
     *      The previous LinkedListNode
     */
    public ObjectSinkNode getPreviousObjectSinkNode();

    /**
     * Sets the previous node 
     * @param previous
     *      The previous LinkedListNode
     */
    public void setPreviousObjectSinkNode(ObjectSinkNode previous);

}
