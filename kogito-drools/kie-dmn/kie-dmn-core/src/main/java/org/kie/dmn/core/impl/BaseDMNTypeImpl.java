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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.UnaryTest;

public abstract class BaseDMNTypeImpl
        implements DMNType {

    private String          namespace;
    private String          name;
    private String          id;
    private boolean         collection;
    private List<UnaryTest> allowedValues;
    private DMNType         baseType;
    private Type            feelType;

    public BaseDMNTypeImpl(String namespace, String name, String id, boolean collection, DMNType baseType, Type feelType) {
        this.namespace = namespace;
        this.name = name;
        this.id = id;
        this.collection = collection;
        this.feelType = feelType;
        this.baseType = baseType;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
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
    public boolean isComposite() {
        return false;
    }

    public List<UnaryTest> getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(List<UnaryTest> allowedValues) {
        this.allowedValues = allowedValues;
    }

    @Override
    public DMNType getBaseType() {
        return baseType;
    }

    public void setBaseType(DMNType baseType) {
        this.baseType = baseType;
    }

    public abstract BaseDMNTypeImpl clone();

    public void setFeelType(Type feelType) {
        this.feelType = feelType;
    }

    public Type getFeelType() {
        return feelType;
    }

    @Override
    public String toString() {
        return "DMNType{ "+getNamespace()+" : "+getName()+" }";
    }

    @Override
    public boolean isInstanceOf(Object o) {
        if ( o == null ) {
            return true; // null is instance of any type
        }
        // try first to recurse in case of Collection..
        if ( isCollection() && o instanceof Collection ) {
            Collection<Object> elements = (Collection) o;
            for ( Object e : elements ) {
                if ( !internalIsInstanceOf(e) ) {
                    return false;
                }
            }
            return true;
        } 
        // .. normal case, or collection of 1 element:
        return internalIsInstanceOf(o);
    }
    
    protected abstract boolean internalIsInstanceOf(Object o);
}
