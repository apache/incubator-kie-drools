/*
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
package org.kie.kogito.persistence.api.query;

public class AttributeFilter<T> {

    private String attribute;

    private FilterCondition condition;

    private T value;

    private transient boolean jsonFilter;

    protected AttributeFilter(String attribute, FilterCondition condition, T value) {
        this.attribute = attribute;
        this.condition = condition;
        this.value = value;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public FilterCondition getCondition() {
        return condition;
    }

    public void setCondition(FilterCondition condition) {
        this.condition = condition;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void setJson(boolean jsonFilter) {
        this.jsonFilter = jsonFilter;
    }

    public boolean isJson() {
        return jsonFilter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttributeFilter)) {
            return false;
        }

        AttributeFilter<?> filter = (AttributeFilter<?>) o;

        if (getAttribute() != null ? !getAttribute().equals(filter.getAttribute()) : filter.getAttribute() != null) {
            return false;
        }
        if (getCondition() != filter.getCondition()) {
            return false;
        }
        return getValue() != null ? getValue().equals(filter.getValue()) : filter.getValue() == null;
    }

    @Override
    public int hashCode() {
        int result = getAttribute() != null ? getAttribute().hashCode() : 0;
        result = 31 * result + (getCondition() != null ? getCondition().hashCode() : 0);
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AttributeFilter{" +
                "column='" + attribute + '\'' +
                ", condition=" + condition +
                ", value=" + value +
                '}';
    }
}
