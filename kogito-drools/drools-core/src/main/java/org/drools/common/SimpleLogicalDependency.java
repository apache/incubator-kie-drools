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

package org.drools.common;

import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.LinkedListEntry;
import org.drools.spi.Activation;

/**
 * LogicalDependency is a special node for LinkedLists that maintains
 * references for the Activation justifier and the justified FactHandle.
 */
public class SimpleLogicalDependency extends AbstractBaseLinkedListNode implements LogicalDependency {
    private Activation justifier;
    private Object justified;
    private Object value;
    
    private LinkedListEntry justifierEntry = new LinkedListEntry( this );
    
    

    public SimpleLogicalDependency(final Activation justifier,
                             final Object object) {
        super();
        this.justifier = justifier;
        this.justified = object;
    }
    
    public SimpleLogicalDependency(final Activation justifier,
                             final Object object,
                             final Object value) {
        super();
        this.justifier = justifier;
        this.justified = object;
        this.value = value;
    }    
    
    /* (non-Javadoc)
     * @see org.drools.common.LogicalDependency#getJustifierEntry()
     */
    public LinkedListEntry getJustifierEntry() {
        return this.justifierEntry;
    }

    /* (non-Javadoc)
     * @see org.drools.common.LogicalDependency#getJustified()
     */
    public Object getJustified() {
        return this.justified;
    }

    /* (non-Javadoc)
     * @see org.drools.common.LogicalDependency#getJustifier()
     */
    public Activation getJustifier() {
        return this.justifier;
    }
    
    /* (non-Javadoc)
     * @see org.drools.common.LogicalDependency#getValue()
     */
    public Object getValue() {
        return this.value;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof SimpleLogicalDependency) ) {
            return false;
        }

        final SimpleLogicalDependency other = (SimpleLogicalDependency) object;
        return (this.getJustifier() == other.getJustifier() && this.justified == other.justified);
    }

    public int hashCode() {
        return this.justifier.hashCode();
    }
}
