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

public class AttributeSort {

    private String attribute;

    private SortDirection sort;

    protected AttributeSort(String attribute, SortDirection sort) {
        this.attribute = attribute;
        this.sort = sort;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public SortDirection getSort() {
        return sort;
    }

    public void setSort(SortDirection sort) {
        this.sort = sort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttributeSort)) {
            return false;
        }

        AttributeSort that = (AttributeSort) o;

        if (getAttribute() != null ? !getAttribute().equals(that.getAttribute()) : that.getAttribute() != null) {
            return false;
        }
        return getSort() == that.getSort();
    }

    @Override
    public int hashCode() {
        int result = getAttribute() != null ? getAttribute().hashCode() : 0;
        result = 31 * result + (getSort() != null ? getSort().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AttributeSort{" +
                "attribute='" + attribute + '\'' +
                ", sort=" + sort +
                '}';
    }
}
