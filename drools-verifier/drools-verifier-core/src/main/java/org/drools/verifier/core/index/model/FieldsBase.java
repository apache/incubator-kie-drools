/**
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
package org.drools.verifier.core.index.model;

import java.util.Collection;

import org.drools.verifier.core.maps.KeyTreeMap;

public class FieldsBase<T extends FieldBase> {

    public final KeyTreeMap<T> map = new KeyTreeMap<>(Field.keyDefinitions());

    public FieldsBase() {

    }

    public void merge(final FieldsBase fields) {
        map.merge(fields.map);
    }

    public FieldsBase(final Collection<T> fields) {
        for (final T field : fields) {
            add(field);
        }
    }

    public void add(final T field) {
        map.put(field);
    }
}
