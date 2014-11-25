/*
 * Copyright 2012 JBoss Inc
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

import java.util.List;

/**
 * A fact pattern is a declaration of a fact type, and its constraint, and
 * perhaps a variable that is it bound to It is the equivalent of a "pattern" in
 * drools terms.
 */
public class FactPattern
        implements
        IFactPattern,
        HasCEPWindow,
        HasConstraints {

    private CompositeFieldConstraint constraintList;
    private String factType;
    private String boundName;
    private boolean isNegated;
    private CEPWindow window;

    public FactPattern() {
        //this.constraints = new CompositeFieldConstraint();
    }

    public FactPattern( final String factType ) {
        this.factType = factType;
        //this.constraints = new CompositeFieldConstraint();
    }

    public String getBoundName() {
        return boundName;
    }

    public void setBoundName( String boundName ) {
        this.boundName = boundName;
    }

    public boolean isNegated() {
        return isNegated;
    }

    public void setNegated( boolean isNegated ) {
        this.isNegated = isNegated;
    }

    /**
     * This will add a top level constraint.
     */
    public void addConstraint( final FieldConstraint constraint ) {
        if ( constraintList == null ) {
            constraintList = new CompositeFieldConstraint();
        }
        this.constraintList.addConstraint( constraint );
    }

    public void removeConstraint( final int idx ) {
        this.constraintList.removeConstraint( idx );
    }

    /**
     * Returns true if there is a variable bound to this fact.
     */
    public boolean isBound() {
        return this.boundName != null && !"".equals( this.boundName );
    }

    /**
     * This will return the list of field constraints that are in the root
     * CompositeFieldConstraint object. If there is no root, then an empty array
     * will be returned.
     * @return an empty array, or the list of constraints (which may be
     *         composites).
     */
    public FieldConstraint[] getFieldConstraints() {
        if ( this.constraintList == null ) {
            return new FieldConstraint[ 0 ];
        }
        return this.constraintList.getConstraints();
    }

    public void setFieldConstraints( final List<FieldConstraint> sortedConstraints ) {
        if ( sortedConstraints != null ) {
            if ( this.constraintList != null ) {
                this.constraintList.setConstraints( new FieldConstraint[ sortedConstraints.size() ] );
                for ( int i = 0; i < sortedConstraints.size(); i++ ) {
                    this.constraintList.getConstraints()[ i ] = (FieldConstraint) sortedConstraints.get( i );
                }
            } else if ( sortedConstraints.size() > 0 ) {
                throw new IllegalStateException( "Cannot have constraints if constraint list is null." );
            }
        } else {
            this.constraintList.setConstraints( null );
        }
    }

    public String getFactType() {
        return this.factType;
    }

    /**
     * WARNING! This method should only be used for fixtures purposes!
     * @param factType
     */
    public void setFactType( String factType ) {
        this.factType = factType;
    }

    public void setWindow( CEPWindow window ) {
        this.window = window;
    }

    public CEPWindow getWindow() {
        if ( this.window == null ) {
            this.window = new CEPWindow();
        }
        return this.window;
    }

    public FieldConstraint getConstraint( int index ) {
        if ( this.constraintList == null ) {
            return null;
        }
        return this.constraintList.getConstraint( index );
    }

    public int getNumberOfConstraints() {
        if ( this.constraintList == null ) {
            return 0;
        }
        return this.constraintList.getNumberOfConstraints();
    }

    public CompositeFieldConstraint getConstraintList() {
        return constraintList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FactPattern that = (FactPattern) o;

        if (isNegated != that.isNegated) return false;
        if (boundName != null ? !boundName.equals(that.boundName) : that.boundName != null) return false;
        if (constraintList != null ? !constraintList.equals(that.constraintList) : that.constraintList != null)
            return false;
        if (factType != null ? !factType.equals(that.factType) : that.factType != null) return false;
        if (window != null ? !window.equals(that.window) : that.window != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = constraintList != null ? constraintList.hashCode() : 0;
        result = 31 * result + (factType != null ? factType.hashCode() : 0);
        result = 31 * result + (boundName != null ? boundName.hashCode() : 0);
        result = 31 * result + (isNegated ? 1 : 0);
        result = 31 * result + (window != null ? window.hashCode() : 0);
        return result;
    }
}
