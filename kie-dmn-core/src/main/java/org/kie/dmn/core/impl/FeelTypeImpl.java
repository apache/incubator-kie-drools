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
import org.kie.dmn.feel.lang.Type;

import java.util.List;

public class FeelTypeImpl
        implements DMNType {

    private String  name;
    private String  id;
    private Type    feelType;
    private List<?> allowedValues;

    public FeelTypeImpl() {
        this( null, null, null, null );
    }

    public FeelTypeImpl(String name, String id) {
        this( name, id, null, null );
    }

    public FeelTypeImpl(String name, String id, Type feelType, List<?> allowedValues) {
        this.name = name;
        this.id = id;
        this.feelType = feelType;
        this.allowedValues = allowedValues;
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

    public Type getFeelType() {
        return feelType;
    }

    public void setFeelType(Type feelType) {
        this.feelType = feelType;
    }

    public List<?> getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(List<?> allowedValues) {
        this.allowedValues = allowedValues;
    }
}
