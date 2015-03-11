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

import java.util.Arrays;

/**
 * This represents a constraint on a fact - involving a SINGLE FIELD. Can also
 * include optional "connective constraints" that extend the options for
 * matches.
 */
public class SingleFieldConstraint extends BaseSingleFieldConstraint
        implements
        FieldConstraint {

    private String fieldBinding;
    private String factType;
    private String fieldName;
    private String fieldType;
    private FieldConstraint parent;

    /**
     * Used with "value" when using custom forms.
     */
    private String id;
    private ConnectiveConstraint[] connectives;

    public SingleFieldConstraint( final String factType,
                                  final String fieldName,
                                  final String fieldType,
                                  final FieldConstraint parent ) {
        this.factType = factType;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.parent = parent;
    }

    public SingleFieldConstraint( final String fieldName ) {
        this.factType = null;
        this.fieldName = fieldName;
        this.fieldType = "";
        this.parent = null;
    }

    public SingleFieldConstraint() {
        this.factType = null;
        this.fieldName = null;
        this.fieldType = "";
        this.parent = null;
    }

    public void setFieldBinding( String fieldBinding ) {
        this.fieldBinding = fieldBinding;
    }

    public String getFieldBinding() {
        return fieldBinding;
    }

    /**
     * This adds a new connective.
     */
    public void addNewConnective() {

        String fieldName = this.fieldName;
        String fieldType = this.fieldType;
        String factType = this.factType;

        if ( this.connectives == null ) {
            this.connectives = new ConnectiveConstraint[]{ new ConnectiveConstraint( factType,
                                                                                     fieldName,
                                                                                     fieldType ) };
        } else {
            final ConnectiveConstraint[] newList = new ConnectiveConstraint[ this.connectives.length + 1 ];
            for ( int i = 0; i < this.connectives.length; i++ ) {
                newList[ i ] = this.connectives[ i ];
            }
            newList[ this.connectives.length ] = new ConnectiveConstraint( factType,
                                                                           fieldName,
                                                                           fieldType );
            this.connectives = newList;
        }
    }

    /**
     * This adds a new connective.
     */
    public void removeConnective( int index ) {

        if ( this.connectives == null ) {
            return;
        }
        if ( index < 0 || index > connectives.length ) {
            throw new IndexOutOfBoundsException();
        }
        int newIndex = 0;
        final ConnectiveConstraint[] newList = new ConnectiveConstraint[ this.connectives.length - 1 ];
        for ( int i = 0; i < this.connectives.length; i++ ) {
            if ( i != index ) {
                newList[ newIndex++ ] = this.connectives[ i ];
            }
        }
        this.connectives = newList;
    }

    /**
     * Returns true of there is a field binding.
     */
    public boolean isBound() {
        return this.getFieldBinding() != null && this.getFieldBinding().length() > 0;
    }

    public String getFactType() {
        return factType;
    }

    public void setFactType( String factType ) {
        this.factType = factType;
    }

    public void setFieldName( String fieldName ) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldType( String fieldType ) {
        this.fieldType = fieldType;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setParent( FieldConstraint parent ) {
        this.parent = parent;
    }

    public FieldConstraint getParent() {
        return parent;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public ConnectiveConstraint[] getConnectives() {
        return connectives;
    }

    public void setConnectives( ConnectiveConstraint[] connectives ) {
        this.connectives = connectives;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SingleFieldConstraint that = (SingleFieldConstraint) o;

        if (!Arrays.equals(connectives, that.connectives)) return false;
        if (factType != null ? !factType.equals(that.factType) : that.factType != null) return false;
        if (fieldBinding != null ? !fieldBinding.equals(that.fieldBinding) : that.fieldBinding != null) return false;
        if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) return false;
        if (fieldType != null ? !fieldType.equals(that.fieldType) : that.fieldType != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + (fieldBinding != null ? fieldBinding.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (factType != null ? factType.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (fieldName != null ? fieldName.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (fieldType != null ? fieldType.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (connectives != null ? Arrays.hashCode(connectives) : 0);
        result = ~~result;
        return result;
    }
}
