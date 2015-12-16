/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.guided.dtree.shared.model.nodes.impl;

import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ConstraintNode;
import org.drools.workbench.models.guided.dtree.shared.model.values.Value;

public class ConstraintNodeImpl extends BaseBoundNodeImpl implements ConstraintNode {

    private String className;
    private String fieldName;
    private String operator;
    private Value value;

    public ConstraintNodeImpl() {
        //Errai marshalling
    }

    public ConstraintNodeImpl( final String className,
                               final String fieldName ) {
        setClassName( className );
        setFieldName( fieldName );
    }

    public ConstraintNodeImpl( final String className,
                               final String fieldName,
                               final String operator,
                               final Value value ) {
        setClassName( className );
        setFieldName( fieldName );
        setOperator( operator );
        setValue( value );
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public void setClassName( final String className ) {
        this.className = PortablePreconditions.checkNotNull( "className",
                                                             className );
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public void setFieldName( final String fieldName ) {
        this.fieldName = PortablePreconditions.checkNotNull( "fieldName",
                                                             fieldName );
    }

    @Override
    public String getOperator() {
        return this.operator;
    }

    @Override
    public void setOperator( final String operator ) {
        this.operator = operator;
    }

    @Override
    public Value getValue() {
        return this.value;
    }

    @Override
    public void setValue( final Value value ) {
        this.value = value;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ConstraintNodeImpl ) ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        ConstraintNodeImpl nodes = (ConstraintNodeImpl) o;

        if ( !className.equals( nodes.className ) ) {
            return false;
        }
        if ( !fieldName.equals( nodes.fieldName ) ) {
            return false;
        }
        if ( operator != null ? !operator.equals( nodes.operator ) : nodes.operator != null ) {
            return false;
        }
        if ( value != null ? !value.equals( nodes.value ) : nodes.value != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + className.hashCode();
        result = 31 * result + fieldName.hashCode();
        result = 31 * result + ( operator != null ? operator.hashCode() : 0 );
        result = 31 * result + ( value != null ? value.hashCode() : 0 );
        return result;
    }
}
