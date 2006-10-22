package org.drools.util;

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
 * The idea behind <code>LinkedListNodeWrapper</code> is to be able to add
 * the same <code>LinkedListNode</code> to multiple <code>LinkedList</code>s 
 * where the node can have different previous and next nodes in each list.
 */
public class LinkedListEntry extends AbstractBaseLinkedListNode {

    private static final long serialVersionUID = 4631711292464775664L;
    private Object object;

    public LinkedListEntry(final Object object) {
        this.object = object;
    }

    public Object getObject() {
        return this.object;
    }

    public int hashCode() {
        return this.object.hashCode();
    }

    public boolean equals(final Object other) {
        return this.object.equals( other );
    }
}
