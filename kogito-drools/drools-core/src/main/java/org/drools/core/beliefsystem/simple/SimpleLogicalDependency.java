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

package org.drools.core.beliefsystem.simple;

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.common.LogicalDependency;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.spi.Activation;
import org.kie.internal.runtime.beliefs.Mode;

/**
 * LogicalDependency is a special node for LinkedLists that maintains
 * references for the Activation justifier and the justified FactHandle.
 */
public class SimpleLogicalDependency<T extends ModedAssertion<T>> extends AbstractBaseLinkedListNode<LogicalDependency<T>>
        implements
        LogicalDependency<T> {
    private final Activation<T>     justifier;
    private final Object            justified;
    private       Object            object;
    private       T                 mode;

    public SimpleLogicalDependency(final Activation<T> justifier,
                                   final Object justified,
                                   final T mode) {
        super();
        this.justifier = justifier;
        this.justified = justified;
        this.mode = mode;
    }

    public SimpleLogicalDependency(final Activation<T> justifier,
                                   final Object justified,
                                   final Object object,
                                   final T mode) {
        super();
        this.justifier = justifier;
        this.justified = justified;
        this.object = object;
        this.mode = mode;
    }

    public T getMode() {
        return (T) mode;
    }

    public void setMode(T mode) {
        this.mode = mode;
    }


    /* (non-Javadoc)
     * @see org.kie.common.LogicalDependency#getJustified()
     */
    public Object getJustified() {
        return this.justified;
    }

    /* (non-Javadoc)
     * @see org.kie.common.LogicalDependency#getJustifier()
     */
    public Activation<T> getJustifier() {
        return this.justifier;
    }

    /* (non-Javadoc)
     * @see org.kie.common.LogicalDependency#getValue()
     */
    public Object getObject() {
        return this.object;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( !(object instanceof SimpleLogicalDependency) ) {
            return false;
        }

        final SimpleLogicalDependency other = (SimpleLogicalDependency) object;
        return (this.getJustifier() == other.getJustifier() && this.justified == other.justified);
    }

    public int hashCode() {
        return this.justifier.hashCode();
    }

    @Override
    public String toString() {
        return "SimpleLogicalDependency [justifier=" + justifier.getRule().getName() + ",\n justified=" + justified + ",\n object=" + object + ", mode=" + mode  + "]";
    }
    
    
}
