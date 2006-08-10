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
import org.drools.rule.Declaration;
import org.drools.spi.FieldConstraint;
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
    FieldConstraint { 

    private static final long serialVersionUID = 2986814365490743953L;

    private final Declaration[] declarations     = new Declaration[0];

    private int                 otherColumn;

    public InstanceEqualsConstraint(final int otherColumn) {
        this.otherColumn = otherColumn;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.declarations;
    }

    public boolean isAllowed(final Object object,
                             final Tuple tuple,
                             final WorkingMemory workingMemory) {
        return (tuple.get( this.otherColumn ).getObject() == object);
    }

    public String toString() {
        return "[InstanceEqualsConstraint otherColumn=" + this.otherColumn + " ]";
    }

    public int hashCode() {
        return this.otherColumn;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        final InstanceEqualsConstraint other = (InstanceEqualsConstraint) object;
        return this.otherColumn == other.otherColumn;
    }

}
