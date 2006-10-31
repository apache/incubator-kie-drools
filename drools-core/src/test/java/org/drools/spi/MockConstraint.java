package org.drools.spi;

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

import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;

public class MockConstraint
    implements
    BetaNodeFieldConstraint {

    /**
     * 
     */
    private static final long serialVersionUID = 2137215908326401659L;

    public Declaration[]      declarations;

    public boolean            isAllowed        = true;

    public boolean isAllowed(final Object object,
                             final Tuple tuple,
                             final WorkingMemory workingMemory) {
        return this.isAllowed;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.declarations;
    }

    public ContextEntry getContextEntry() {
        return new ContextEntry() {
            private static final long serialVersionUID = -6597931500771088767L;
            private ContextEntry next;

            public ContextEntry getNext() {
                return next;
            }

            public void setNext(ContextEntry entry) {
                this.next = entry;
            }

            public void updateFromFactHandle(InternalWorkingMemory workingMemory,
                                             InternalFactHandle handle) {
            }

            public void updateFromTuple(InternalWorkingMemory workingMemory,
                                        ReteTuple tuple) {
            }
        };
    }

    public boolean isAllowedCachedLeft(ContextEntry context,
                                       Object object) {
        return this.isAllowed;
    }

    public boolean isAllowedCachedRight(ReteTuple tuple,
                                        ContextEntry context) {
        return this.isAllowed;
    }

}