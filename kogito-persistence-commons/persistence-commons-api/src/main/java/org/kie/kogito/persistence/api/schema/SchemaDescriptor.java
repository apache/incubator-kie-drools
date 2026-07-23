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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class SchemaDescriptor {

    String name;

    String schemaContent;

    Map<String, EntityIndexDescriptor> entityIndexDescriptors;

    ProcessDescriptor processDescriptor;

    public SchemaDescriptor(String name, String schemaContent, Map<String, EntityIndexDescriptor> entityIndexDescriptors, ProcessDescriptor processDescriptor) {
        this.name = name;
        this.schemaContent = schemaContent;
        this.entityIndexDescriptors = entityIndexDescriptors;
        this.processDescriptor = processDescriptor;
    }

    public String getName() {
        return name;
    }

    public String getSchemaContent() {
        return schemaContent;
    }

    public Map<String, EntityIndexDescriptor> getEntityIndexDescriptors() {
        return entityIndexDescriptors;
    }

    public Optional<ProcessDescriptor> getProcessDescriptor() {
        return Optional.ofNullable(processDescriptor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SchemaDescriptor that = (SchemaDescriptor) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(schemaContent, that.schemaContent) &&
                Objects.equals(entityIndexDescriptors, that.entityIndexDescriptors) &&
                Objects.equals(processDescriptor, that.processDescriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, schemaContent, entityIndexDescriptors, processDescriptor);
    }

    @Override
    public String toString() {
        return "SchemaDescriptor{" +
                "name='" + name + '\'' +
                ", schemaContent='" + schemaContent + '\'' +
                ", entityIndexDescriptors=" + entityIndexDescriptors +
                ", processDescriptor=" + processDescriptor +
                '}';
    }
}
