/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.model.v1_2;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.FunctionItem;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.api.UnaryTests;

public class TItemDefinition extends TNamedElement implements ItemDefinition {

    /**
     * align to internal model
     */
    protected QName typeRef;
    protected UnaryTests allowedValues;
    protected List<ItemDefinition> itemComponent;
    protected String typeLanguage;
    protected Boolean isCollection;

    @Override
    public QName getTypeRef() {
        return typeRef;
    }

    @Override
    public void setTypeRef(final QName value) {
        this.typeRef = value;
    }

    @Override
    public UnaryTests getAllowedValues() {
        return allowedValues;
    }

    @Override
    public void setAllowedValues(UnaryTests value) {
        this.allowedValues = value;
    }

    @Override
    public List<ItemDefinition> getItemComponent() {
        if (itemComponent == null) {
            itemComponent = new ArrayList<ItemDefinition>();
        }
        return this.itemComponent;
    }

    @Override
    public String getTypeLanguage() {
        return typeLanguage;
    }

    @Override
    public void setTypeLanguage(String value) {
        this.typeLanguage = value;
    }

    @Override
    public boolean isIsCollection() {
        if (isCollection == null) {
            return false;
        } else {
            return isCollection;
        }
    }

    @Override
    public void setIsCollection(Boolean value) {
        this.isCollection = value;
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
