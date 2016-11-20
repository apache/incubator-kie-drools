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

import java.util.HashMap;
import java.util.Map;

public class CompositeTypeImpl
        implements DMNType {

    private String               name;
    private String               id;
    private boolean              collection;
    private Map<String, DMNType> fields;

    public CompositeTypeImpl() {
        this( null, null, new HashMap<>(  ) );
    }

    public CompositeTypeImpl(String name, String id) {
        this( name, id, new HashMap<>(  ) );
    }

    public CompositeTypeImpl(String name, String id, Map<String, DMNType> fields) {
        this.name = name;
        this.id = id;
        this.fields = fields;
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

    public boolean isCollection() {
        return collection;
    }

    public void setCollection(boolean collection) {
        this.collection = collection;
    }

    public Map<String, DMNType> getFields() {
        return fields;
    }

    @Override
    public DMNType getField(String fieldName) {
        return fields.get( fieldName );
    }

    public void setFields(Map<String, DMNType> fields) {
        this.fields = fields;
    }

    @Override
    public Object parseValue(String value) {
        return null;
    }

    @Override
    public String toString(Object value) {
        return null;
    }

    @Override
    public boolean isComposite() {
        return true;
    }
}
