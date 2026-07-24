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

public class IndexDescriptor {

    String name;

    List<String> indexAttributes;

    public IndexDescriptor(String name, List<String> indexAttributes) {
        this.name = name;
        this.indexAttributes = indexAttributes;
    }

    public String getName() {
        return name;
    }

    public List<String> getIndexAttributes() {
        return indexAttributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IndexDescriptor that = (IndexDescriptor) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(indexAttributes, that.indexAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, indexAttributes);
    }
}
