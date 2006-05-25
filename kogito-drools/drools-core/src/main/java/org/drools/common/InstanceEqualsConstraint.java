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



import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.FieldConstraint;
import org.drools.spi.Tuple;

public class InstanceEqualsConstraint
    implements
    FieldConstraint {
    private Declaration[] declarations = new Declaration[0];

    private int           otherColumn;

    public InstanceEqualsConstraint(int otherColumn) {
        this.otherColumn = otherColumn;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.declarations;
    }

    public boolean isAllowed(InternalFactHandle handle,
                             Tuple tuple,
                             WorkingMemory workingMemory) {
        return !(tuple.get( this.otherColumn ).getObject() == handle.getObject() );
    }

    public String toString() {
        return "[InstanceEqualsConstraint otherColumn=" + this.otherColumn + " ]";
    }
    
    public int hashCode() {
        return this.otherColumn;
    }
    
    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }
        
        InstanceEqualsConstraint other = ( InstanceEqualsConstraint ) object;
        return this.otherColumn == other.otherColumn ;
    }

}