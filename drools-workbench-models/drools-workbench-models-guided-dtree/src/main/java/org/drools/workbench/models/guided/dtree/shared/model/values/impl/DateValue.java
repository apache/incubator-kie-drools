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
package org.drools.workbench.models.guided.dtree.shared.model.values.impl;

import java.util.Date;

import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.drools.workbench.models.guided.dtree.shared.model.values.Value;

public class DateValue implements Value<Date> {

    private Date value;

    public DateValue() {
        //Errai marshalling
    }

    public DateValue( final Date value ) {
        setValue( value );
    }

    public DateValue( final DateValue value ) {
        setValue( value.getValue() );
    }

    @Override
    public void setValue( final String value ) {
        //This class can be used server or client side where we have to use different String->Date conversion classes
        throw new UnsupportedOperationException( "Please use DateValue.setValue(Date)" );
    }

    @Override
    public void setValue( final Date value ) {
        this.value = PortablePreconditions.checkNotNull( "value",
                                                         value );
    }

    @Override
    public Date getValue() {
        return this.value;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof DateValue ) ) {
            return false;
        }

        DateValue dateValue = (DateValue) o;

        if ( !value.equals( dateValue.value ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
