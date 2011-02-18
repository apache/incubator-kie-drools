/**
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

import org.drools.FactHandle;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.spi.Activation;

/**
 * LogicalDependency is a special node for LinkedLists that maintains
 * references for the Activation justifier and the justified FactHandle.
 *   
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 *
 */
public class LogicalDependency extends AbstractBaseLinkedListNode {
    private Activation justifier;
    private FactHandle factHandle;

    public LogicalDependency(final Activation justifier,
                             final FactHandle factHandle) {
        super();
        this.justifier = justifier;
        this.factHandle = factHandle;
    }

    public FactHandle getFactHandle() {
        return this.factHandle;
    }

    public Activation getJustifier() {
        return this.justifier;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof LogicalDependency) ) {
            return false;
        }

        final LogicalDependency other = (LogicalDependency) object;
        return (this.getJustifier() == other.getJustifier() && this.getFactHandle() == other.getFactHandle());
    }

    public int hashCode() {
        return this.justifier.hashCode();
    }
}
