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

import java.util.Map;

public class DMNTypeImpl {

    private String name;
    private String id;
    private boolean simple;
    private Class<?> javaType;
    private Map<String, DMNTypeImpl> fields;

    public DMNTypeImpl() {
    }

    public DMNTypeImpl(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public DMNTypeImpl(String name, String id, Class<?> javaType) {
        this.name = name;
        this.id = id;
        this.javaType = javaType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSimple() {
        return simple;
    }

    public void setSimple(boolean simple) {
        this.simple = simple;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public Map<String, DMNTypeImpl> getFields() {
        return fields;
    }

    public void setFields(Map<String, DMNTypeImpl> fields) {
        this.fields = fields;
    }
}
