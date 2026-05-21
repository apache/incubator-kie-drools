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
package org.kie.kogito.persistence.postgresql.reporting;

import java.util.Collection;

public class ComplexHierarchicalType {

    private String root;
    private BasicType nestedBasic;
    private Collection<ComplexHierarchicalType> nestedComplexCollection;

    ComplexHierarchicalType() {
    }

    public ComplexHierarchicalType(final String root,
            final BasicType nestedBasic,
            final Collection<ComplexHierarchicalType> nestedComplexCollection) {
        this.root = root;
        this.nestedBasic = nestedBasic;
        this.nestedComplexCollection = nestedComplexCollection;
    }

    public String getRoot() {
        return root;
    }

    public BasicType getNestedBasic() {
        return nestedBasic;
    }

    public Collection<ComplexHierarchicalType> getNestedComplexCollection() {
        return nestedComplexCollection;
    }
}
