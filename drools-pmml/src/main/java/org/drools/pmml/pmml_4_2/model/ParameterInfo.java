/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.pmml.pmml_4_2.model;

public class ParameterInfo<T> {
    private String name;
    private Class<T> type;
    private T value;

    public ParameterInfo(String name, Class<T> type, T value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getCapitalizedName() {
        return name.substring(0,1).toUpperCase()+name.substring(1);
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("ParameterInfo( ");
        stringBuilder.append("name=").append(name).append(", ");
        stringBuilder.append("type=").append(type.getName()).append(", ");
        if (type.getName().equals(String.class.getName())) {
            stringBuilder.append("value=").append(value).append(" )");
        } else {
            stringBuilder.append("value=").append(value).append(" )");
        }
        return stringBuilder.toString();
    }
}
