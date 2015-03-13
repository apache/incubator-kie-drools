/*
 * Copyright 2014 JBoss Inc
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
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionFieldValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.Value;

public class ActionFieldValueImpl implements ActionFieldValue {

    private String fieldName;
    private Value value;

    public ActionFieldValueImpl() {
        //Errai marshalling
    }

    public ActionFieldValueImpl( final String fieldName,
                                 final Value value ) {
        setFieldName( fieldName );
        setValue( value );
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
        if ( !( o instanceof ActionFieldValueImpl ) ) {
            return false;
        }

        ActionFieldValueImpl that = (ActionFieldValueImpl) o;

        if ( !fieldName.equals( that.fieldName ) ) {
            return false;
        }
        if ( !value.equals( that.value ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = fieldName.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
