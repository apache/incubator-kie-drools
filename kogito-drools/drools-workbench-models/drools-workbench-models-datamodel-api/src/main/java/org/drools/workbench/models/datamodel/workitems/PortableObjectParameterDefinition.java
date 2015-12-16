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
 * An Object parameter. These can only be bound to Facts of the same data-type
 */
public class PortableObjectParameterDefinition
        extends PortableParameterDefinition
        implements HasBinding {

    private String className;

    private String binding;

    public PortableObjectParameterDefinition() {

    }

    public String getClassName() {
        return className;
    }

    public void setClassName( String className ) {
        this.className = className;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding( String binding ) {
        this.binding = binding;
    }

    @Override
    public String asString() {
        if ( isBound() ) {
            return this.getBinding();
        }
        return "null";
    }

    public boolean isBound() {
        return ( this.getBinding() != null && !"".equals( this.getBinding() ) );
    }

}
