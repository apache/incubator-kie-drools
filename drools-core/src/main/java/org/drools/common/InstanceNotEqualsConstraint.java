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

import org.drools.reteoo.ReteTuple;
import org.drools.rule.Column;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;
import org.drools.spi.BetaNodeFieldConstraint;

public class InstanceNotEqualsConstraint
    implements
    BetaNodeFieldConstraint {

    private static final long          serialVersionUID = 320L;

    private static final Declaration[] declarations     = new Declaration[0];

    private Column                     otherColumn;

    public InstanceNotEqualsConstraint(final Column otherColumn) {
        this.otherColumn = otherColumn;
    }

    public Declaration[] getRequiredDeclarations() {
        return InstanceNotEqualsConstraint.declarations;
    }

    public Column getOtherColumn() {
        return this.otherColumn;
    }

    public ContextEntry getContextEntry() {
        return new InstanceNotEqualsConstraintContextEntry( this.otherColumn );
    }

    public boolean isAllowed(final ContextEntry entry) {
        final InstanceNotEqualsConstraintContextEntry context = (InstanceNotEqualsConstraintContextEntry) entry;
        return context.left != context.right;
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final Object object) {
        return ((InstanceNotEqualsConstraintContextEntry) context).left != object;
    }

    public boolean isAllowedCachedRight(final ReteTuple tuple,
                                        final ContextEntry context) {
        return tuple.get( this.otherColumn.getFactIndex() ).getObject() != ((InstanceNotEqualsConstraintContextEntry) context).right;
    }

    public String toString() {
        return "[InstanceEqualsConstraint otherColumn=" + this.otherColumn + " ]";
    }

    public int hashCode() {
        return this.otherColumn.hashCode();
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        final InstanceNotEqualsConstraint other = (InstanceNotEqualsConstraint) object;
        return this.otherColumn.equals( other.otherColumn );
    }

    public static class InstanceNotEqualsConstraintContextEntry
        implements
        ContextEntry {
        public Object        left;
        public Object        right;

        private Column       column;
        private ContextEntry entry;

        public InstanceNotEqualsConstraintContextEntry(final Column column) {
            this.column = column;
        }

        public ContextEntry getNext() {
            return this.entry;
        }

        public void setNext(final ContextEntry entry) {
            this.entry = entry;
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory, final ReteTuple tuple) {
            this.left = tuple.get( this.column.getFactIndex() ).getObject();
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory, final InternalFactHandle handle) {
            this.right = handle.getObject();

        }
    }

}