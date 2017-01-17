/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.impl;

import org.kie.dmn.core.api.DMNType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class BaseDMNTypeImpl
        implements DMNType {

    private String  name;
    private String  id;
    private boolean collection;
    private List<?> allowedValues;

    public BaseDMNTypeImpl(String name, String id, boolean collection) {
        this.name = name;
        this.id = id;
        this.collection = collection;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isCollection() {
        return collection;
    }

    public void setCollection(boolean collection) {
        this.collection = collection;
    }

    @Override
    public Map<String, DMNType> getFields() {
        return Collections.emptyMap();
    }

    @Override
    public DMNType getField(String typeName) {
        return null;
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    public List<?> getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(List<?> allowedValues) {
        this.allowedValues = allowedValues;
    }

    public abstract BaseDMNTypeImpl clone();

}
