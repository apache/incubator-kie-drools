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
package org.kie.kogito.persistence.api.schema;

import java.util.List;
import java.util.Objects;

public class EntityIndexDescriptor {

    String name;

    List<IndexDescriptor> indexDescriptors;

    List<AttributeDescriptor> attributeDescriptors;

    public EntityIndexDescriptor(String name, List<IndexDescriptor> indexDescriptors, List<AttributeDescriptor> attributeDescriptors) {
        this.name = name;
        this.indexDescriptors = indexDescriptors;
        this.attributeDescriptors = attributeDescriptors;
    }

    public String getName() {
        return name;
    }

    public List<IndexDescriptor> getIndexDescriptors() {
        return indexDescriptors;
    }

    public List<AttributeDescriptor> getAttributeDescriptors() {
        return attributeDescriptors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityIndexDescriptor that = (EntityIndexDescriptor) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(indexDescriptors, that.indexDescriptors) &&
                Objects.equals(attributeDescriptors, that.attributeDescriptors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, indexDescriptors, attributeDescriptors);
    }

    @Override
    public String toString() {
        return "EntityIndexDescriptor{" +
                "name='" + name + '\'' +
                ", indexDescriptors=" + indexDescriptors +
                ", attributeDescriptors=" + attributeDescriptors +
                '}';
    }
}
