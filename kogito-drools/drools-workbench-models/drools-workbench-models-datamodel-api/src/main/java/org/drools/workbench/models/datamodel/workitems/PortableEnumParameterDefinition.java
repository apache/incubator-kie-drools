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
 * An Enum parameter
 */

public class PortableEnumParameterDefinition
        extends PortableObjectParameterDefinition
        implements HasValue<String> {

    private String[] values;
    private String value;

    public PortableEnumParameterDefinition() {

    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues( String[] values ) {
        this.values = values;
    }

    @Override
    public String asString() {
        if ( !( this.getBinding() == null || "".equals( this.getBinding() ) ) ) {
            return this.getBinding();
        }
        if ( this.value == null ) {
            return "null";
        }
        return this.getClassName() + "." + this.value;
    }

}
