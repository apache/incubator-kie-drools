/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

/**
 * Items placed in a <code>LinkedList<code> must implement this interface .
 * 
 * @see LeftTupleSinkNodeList
 */
public interface LeftTupleSinkNode
    extends
    LeftTupleSink {

    /**
     * Returns the next node
     * @return
     *      The next LinkedListNode
     */
    public LeftTupleSinkNode getNextLeftTupleSinkNode();

    /**
     * Sets the next node 
     * @param next
     *      The next LinkedListNode
     */
    public void setNextLeftTupleSinkNode(LeftTupleSinkNode next);

    /**
     * Returns the previous node
     * @return
     *      The previous LinkedListNode
     */
    public LeftTupleSinkNode getPreviousLeftTupleSinkNode();

    /**
     * Sets the previous node 
     * @param previous
     *      The previous LinkedListNode
     */
    public void setPreviousLeftTupleSinkNode(LeftTupleSinkNode previous);    

}
