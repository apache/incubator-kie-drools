/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.impl;

import java.util.Objects;

public class TupleIdentifier {

    private final String id;
    private final String name;

    static final String AUTO_GENERATED_ID_PREFIX = "auto-generated-id";
    static final String AUTO_GENERATED_NAME_PREFIX = "auto-generated-name";

    static TupleIdentifier createTupleIdentifier(String id, String name) {
        return new TupleIdentifier(id, name);
    }

    static TupleIdentifier createTupleIdentifierById(String id) {
        return new TupleIdentifier(id, generateNameFromId(id));
    }

    static TupleIdentifier createTupleIdentifierByName(String name) {
        return new TupleIdentifier(generateIdFromName(name), name);
    }

    static String generateIdFromName(String name) {
        return String.format("%s-%s", AUTO_GENERATED_ID_PREFIX, Objects.hash(name));
    }

    static String generateNameFromId(String id) {
        return String.format("%s-%s", AUTO_GENERATED_NAME_PREFIX, Objects.hash(id));
    }

    public TupleIdentifier(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TupleIdentifier that)) {
            return false;
        }
        // This "null" availability it is to allow for search based only on id or name
        if (id == null || that.id == null) {
            return Objects.equals(name, that.name);
        } else if (name == null || that.name == null) {
            return Objects.equals(id, that.id);
        } else {
            return Objects.equals(id, that.id) && Objects.equals(name, that.name);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(1); // we have to consider "null" comparisons, so everything should go in same "bucket"
    }
}