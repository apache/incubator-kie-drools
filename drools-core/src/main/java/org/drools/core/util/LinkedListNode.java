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

package org.drools.core.util;

/**
 * Items placed in a <code>LinkedList<code> must implement this interface .
 *
 * @see LinkedList
 */
public interface LinkedListNode<T extends LinkedListNode> extends Entry<T> {

    /**
     * Returns the previous node
     * @return
     *      The previous LinkedListNode
     */
    public T getPrevious();

    /**
     * Sets the previous node
     * @param previous
     *      The previous LinkedListNode
     */
    public void setPrevious(T previous);

    void nullPrevNext();

}
