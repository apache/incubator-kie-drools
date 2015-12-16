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
package org.drools.workbench.models.datamodel.workitems;

/**
 * An Integer parameter
 */
public class PortableIntegerParameterDefinition
        extends PortableParameterDefinition
        implements HasValue<Integer>,
                   HasBinding {

    private String binding;

    private Integer value;

    public PortableIntegerParameterDefinition() {

    }

    public Integer getValue() {
        return this.value;
    }

    public void setValue( Integer value ) {
        this.value = value;
    }

    public String getBinding() {
        return this.binding;
    }

    public void setBinding( String binding ) {
        this.binding = binding;
    }

    @Override
    public String asString() {
        if ( isBound() ) {
            return this.getBinding();
        }
        if ( this.value == null ) {
            return "null";
        }
        return Integer.toString( this.value );
    }

    @Override
    public String getClassName() {
        return Integer.class.getName();
    }

    public boolean isBound() {
        return ( this.getBinding() != null && !"".equals( this.getBinding() ) );
    }

}
