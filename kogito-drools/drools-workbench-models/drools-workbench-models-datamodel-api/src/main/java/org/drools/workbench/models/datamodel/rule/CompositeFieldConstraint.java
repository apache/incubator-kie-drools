/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.datamodel.rule;

import java.util.Arrays;

/**
 * This is a field constraint that may span multiple fields.
 */
public class CompositeFieldConstraint
        implements
        FieldConstraint,
        HasConstraints {

    /**
     * Means that any of the children can resolve to be true.
     */
    public static final String COMPOSITE_TYPE_OR = "||";

    /**
     * Means that ALL of the children constraints must resolve to be true.
     */
    public static final String COMPOSITE_TYPE_AND = "&&";

    /**
     * The type of composite that it is.
     */
    private String compositeJunctionType = null;

    /**
     * This is the child field constraints of the composite. They may be single
     * constraints, or composite themselves. If this composite is it at the
     * "top level" - then there is no need to look at the compositeType property
     * (as they are all children that are "anded" together anyway in the fact
     * pattern that contains it).
     */
    private FieldConstraint[] constraints = null;

    public CompositeFieldConstraint() {

    }

    //Note this is a bit ugly, GWT had some early limitations which required this to kind of work this way.
    //when generics are available, could probably switch to it, but remember this is persistent stuff
    //so don't want to break backwards compat (as XStream is used)
    public void addConstraint( final FieldConstraint constraint ) {
        if ( this.constraints == null ) {
            this.constraints = new FieldConstraint[ 1 ];
            this.constraints[ 0 ] = constraint;
        } else {
            final FieldConstraint[] newList = new FieldConstraint[ this.constraints.length + 1 ];
            for ( int i = 0; i < this.constraints.length; i++ ) {
                newList[ i ] = this.constraints[ i ];
            }
            newList[ this.constraints.length ] = constraint;
            this.constraints = newList;
        }
    }

    //Unfortunately, this is kinda duplicate code with other methods, but with 
    //typed arrays, and GWT, its not really possible to do anything "better"
    //at this point in time.
    public void removeConstraint( final int idx ) {
        //If the constraint being is a parent of another correct the other constraint's parent accordingly
        FieldConstraint constraintToRemove = this.constraints[ idx ];
        if ( constraintToRemove instanceof SingleFieldConstraint ) {
            final SingleFieldConstraint sfc = (SingleFieldConstraint) constraintToRemove;
            FieldConstraint parent = sfc.getParent();
            for ( FieldConstraint child : this.constraints ) {
                if ( child instanceof SingleFieldConstraint ) {
                    SingleFieldConstraint sfcChild = (SingleFieldConstraint) child;
                    if ( sfcChild.getParent() == constraintToRemove ) {
                        sfcChild.setParent( parent );
                        break;
                    }
                }
            }
        }
        final FieldConstraint[] newList = new FieldConstraint[ this.constraints.length - 1 ];
        int newIdx = 0;
        for ( int i = 0; i < this.constraints.length; i++ ) {

            if ( i != idx ) {
                newList[ newIdx ] = this.constraints[ i ];
                newIdx++;
            }

        }
        this.constraints = newList;

    }

    public FieldConstraint getConstraint( int index ) {
        if ( this.constraints == null ) {
            return null;
        }
        return this.constraints[ index ];
    }

    public int getNumberOfConstraints() {
        if ( this.constraints == null ) {
            return 0;
        }
        return this.constraints.length;
    }

    public FieldConstraint[] getConstraints() {
        return constraints;
    }

    public void setConstraints( FieldConstraint[] constraints ) {
        this.constraints = constraints;
    }

    public String getCompositeJunctionType() {
        return compositeJunctionType;
    }

    public void setCompositeJunctionType( String compositeJunctionType ) {
        this.compositeJunctionType = compositeJunctionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompositeFieldConstraint that = (CompositeFieldConstraint) o;

        if (compositeJunctionType != null ? !compositeJunctionType.equals(that.compositeJunctionType) : that.compositeJunctionType != null)
            return false;
        if (!Arrays.equals(constraints, that.constraints)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = compositeJunctionType != null ? compositeJunctionType.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (constraints != null ? Arrays.hashCode(constraints) : 0);
        result = ~~result;
        return result;
    }
}
