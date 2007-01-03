package org.drools.rule;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.spi.ColumnExtractor;
import org.drools.spi.Constraint;
import org.drools.spi.Extractor;
import org.drools.spi.ObjectType;

public class Column
    implements
    RuleConditionElement {
    /**
     * 
     */
    private static final long serialVersionUID = 320;
    private final ObjectType  objectType;
    private List              constraints      = Collections.EMPTY_LIST;
    final Declaration         declaration;
    private Map               declarations;
    private final int         index;

    // this is the negative offset of the related fact inside a tuple. i.e:
    // tuple_fact_index = column_index + offset; 
    private int               offset;

    public Column(final int index,
                  final ObjectType objectType) {
        this( index,
              0,
              objectType,
              null );
    }

    public Column(final int index,
                  final ObjectType objectType,
                  final String identifier) {
        this( index,
              0,
              objectType,
              identifier );
    }

    public Column(final int index,
                  final int offset,
                  final ObjectType objectType,
                  final String identifier) {
        this.index = index;
        this.offset = offset;
        this.objectType = objectType;
        if ( identifier != null ) {
            this.declaration = new Declaration( identifier,
                                                new ColumnExtractor( objectType ),
                                                this );
            this.declarations = new HashMap(2); // default to avoid immediate resize
            this.declarations.put( this.declaration.getIdentifier(), this.declaration );
        } else {
            this.declaration = null;
        }
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }

    public List getConstraints() {
        return Collections.unmodifiableList( this.constraints );
    }

    public void addConstraint(final Constraint constraint) {
        if ( this.constraints == Collections.EMPTY_LIST ) {
            this.constraints = new ArrayList( 1 );
        }
        this.constraints.add( constraint );
    }

    public Declaration addDeclaration(final String identifier,
                                      final Extractor extractor) {
        if ( this.constraints == Collections.EMPTY_LIST ) {
            this.constraints = new ArrayList( 1 );
        }
        final Declaration declaration = new Declaration( identifier,
                                                         extractor,
                                                         this );
        this.constraints.add( declaration );
        if( this.declarations == null ) {
            this.declarations = new HashMap(2); // default to avoid immediate resize
        }
        this.declarations.put( declaration.getIdentifier(), declaration );
        return declaration;

    }

    public boolean isBound() {
        return (this.declaration != null);
    }

    public Declaration getDeclaration() {
        return this.declaration;
    }

    public int getIndex() {
        return this.index;
    }

    /**
     * @return the offset
     */
    public int getOffset() {
        return this.offset;
    }

    public int getFactIndex() {
        return this.index + this.offset;
    }

    /**
     * A simple method to adjust offset of all declarations using the specified value
     * @param adjust
     */
    public void adjustOffset(final int adjust) {
        this.offset += adjust;

        if ( this.declaration != null ) {
            this.declaration.setColumn( this );
        }
        for ( final Iterator i = this.constraints.iterator(); i.hasNext(); ) {
            final Object constr = i.next();
            if ( constr instanceof Declaration ) {
                ((Declaration) constr).setColumn( this );
            }
        }
    }

    public Map getInnerDeclarations() {
        return ( this.declarations != null ) ? this.declarations : Collections.EMPTY_MAP;
    }

    public Map getOuterDeclarations() {
        return ( this.declarations != null ) ? this.declarations : Collections.EMPTY_MAP;
    }

    public String toString() {
        return "Column type='" + ((this.objectType == null) ? "null" : this.objectType.toString()) + "', index='" + this.index + "', offset='" + this.getOffset() + "', identifer='" + ((this.declaration == null) ? "" : this.declaration.toString()) + "'";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.constraints.hashCode();
        result = PRIME * result + ((this.declaration == null) ? 0 : this.declaration.hashCode());
        result = PRIME * result + this.index;
        result = PRIME * result + ((this.objectType == null) ? 0 : this.objectType.hashCode());
        result = PRIME * result + this.offset;
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        final Column other = (Column) object;

        if ( !this.constraints.equals( other.constraints ) ) {
            return false;
        }

        if ( this.declaration == null ) {
            if ( other.declaration != null ) {
                return false;
            }
        } else if ( !this.declaration.equals( other.declaration ) ) {
            return false;
        }

        if ( this.index != other.index ) {
            return false;
        }

        if ( !this.objectType.equals( other.objectType ) ) {
            return false;
        }
        if ( this.offset != other.offset ) {
            return false;
        }
        return true;
    }

}