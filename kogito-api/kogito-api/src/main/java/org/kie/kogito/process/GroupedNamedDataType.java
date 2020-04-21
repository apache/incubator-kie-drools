/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.process;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroupedNamedDataType {

    private final Map<String, Set<NamedDataType>> groupedDataTypes = new HashMap<>();

    public void add(String name, List<NamedDataType> types) {
        Set<NamedDataType> dataTypes = this.groupedDataTypes.getOrDefault(name, new LinkedHashSet<>());
        dataTypes.addAll(types);
        this.groupedDataTypes.put(name, dataTypes);
    }

    public Set<NamedDataType> getTypesByName(String name) {
        return this.groupedDataTypes.getOrDefault(name, new LinkedHashSet<>());
    }

    @Override
    public String toString() {
        return "GroupedNamedDataType [groupedDataTypes=" + groupedDataTypes + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + groupedDataTypes.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GroupedNamedDataType other = (GroupedNamedDataType) obj;
         if (!groupedDataTypes.equals(other.groupedDataTypes))
            return false;
        return true;
    }
}
