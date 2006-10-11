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

import org.drools.WorkingMemory;
import org.drools.common.InstanceNotEqualsConstraint.InstanceNotEqualsConstraintContextEntry;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Column;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.Tuple;

/**
 * InstanceEqualsConstraint
 *
 * Created: 21/06/2006
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a> 
 *
 * @version $Id$
 */

public class InstanceEqualsConstraint
    implements
    BetaNodeFieldConstraint { 

    private static final long serialVersionUID = 320L;

    private final Declaration[] declarations     = new Declaration[0];

    private Column                 otherColumn;

    public InstanceEqualsConstraint(final Column otherColumn) {
        this.otherColumn = otherColumn;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.declarations;
    }
    
    public Column getOtherColumn() {
        return this.otherColumn;
    }
    
    public ContextEntry getContextEntry() {
        return new InstanceEqualsConstraintContextEntry( this.otherColumn  );
    }
    
    public boolean isAllowedCachedLeft(ContextEntry context, Object object ) {
        return ((InstanceNotEqualsConstraintContextEntry)context).left == object;
    }    
    
    public boolean isAllowedCachedRight(ReteTuple tuple, ContextEntry context) {
        return tuple.get( this.otherColumn.getFactIndex()).getObject() == ((InstanceNotEqualsConstraintContextEntry)context).right ;        
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

        final InstanceEqualsConstraint other = (InstanceEqualsConstraint) object;
        return this.otherColumn.equals( other.otherColumn );
    }

    public static class InstanceEqualsConstraintContextEntry implements ContextEntry {
        public Object left;
        public Object right;        
        
        private Column column;
        private ContextEntry entry;
        
        
        public InstanceEqualsConstraintContextEntry(Column column) {
            this.column = column;
        }
        
        public ContextEntry getNext() {
            return this.entry;
        }
        
        public void setNext(ContextEntry  entry) {
            this.entry = entry;
        }
        
        public void updateFromTuple(ReteTuple tuple) {
            this.left =  tuple.get( this.column.getFactIndex() ).getObject();            
        }           
        
        public void updateFromFactHandle(InternalFactHandle handle) {
            this.right = handle.getObject();
            
        }                    
    }    
}
