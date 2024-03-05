/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.model.v1_1;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.FunctionItem;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.api.UnaryTests;

public class TItemDefinition extends TNamedElement implements ItemDefinition {

    private QName typeRef;
    private UnaryTests allowedValues;
    private List<ItemDefinition> itemComponent = new ArrayList<>();
    private String typeLanguage;
    private Boolean isCollection;

    @Override
    public QName getTypeRef() {
        return typeRef;
    }

    @Override
    public void setTypeRef( final QName value ) {
        this.typeRef = value;
    }

    @Override
    public UnaryTests getAllowedValues() {
        return allowedValues;
    }

    @Override
    public void setAllowedValues(final UnaryTests value) {
        this.allowedValues = value;
    }

    @Override
    public List<ItemDefinition> getItemComponent() {
        return this.itemComponent;
    }

    @Override
    public String getTypeLanguage() {
        return typeLanguage;
    }

    @Override
    public void setTypeLanguage( final String value ) {
        this.typeLanguage = value;
    }

    @Override
    public boolean isIsCollection() {
        if ( isCollection == null ) {
            return false;
        } else {
            return isCollection;
        }
    }

    @Override
    public void setIsCollection( final Boolean value ) {
        this.isCollection = value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ")
                .add("name: " + getName())
                .add("typeRef: " + typeRef)
                .add("allowedValues: " + allowedValues)
                .add("itemComponent: " + itemComponent)
                .add("typeLanguage: " + typeLanguage)
                .add("isCollection: " + isCollection)
                .toString();
    }

    @Override
    public FunctionItem getFunctionItem() {
        throw new UnsupportedOperationException("Since DMNv1.3");
    }

    @Override
    public void setFunctionItem(FunctionItem value) {
        throw new UnsupportedOperationException("Since DMNv1.3");
    }
}
