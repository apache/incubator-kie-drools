package org.drools.common;
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



import org.drools.FactHandle;
import org.drools.spi.Activation;
import org.drools.util.AbstractBaseLinkedListNode;

public class LogicalDependency extends AbstractBaseLinkedListNode {
    private Activation justifier;
    private FactHandle factHandle;

    public LogicalDependency(Activation justifier,
                             FactHandle factHandle) {
        super();
        this.justifier = justifier;
        this.factHandle = factHandle;
    }

    public FactHandle getFactHandle() {
        return factHandle;
    }

    public Activation getJustifier() {
        return justifier;
    }

    public boolean equals(Object object) {
        if ( object == null || !(object.getClass() != this.getClass()) ) {
            return false;
        }

        if ( this == object ) {
            return true;
        }

        LogicalDependency other = (LogicalDependency) object;
        return (this.getJustifier() == other.getJustifier() && this.getFactHandle() == other.getFactHandle());
    }

    public int hashCode() {
        return this.justifier.hashCode();
    }
}