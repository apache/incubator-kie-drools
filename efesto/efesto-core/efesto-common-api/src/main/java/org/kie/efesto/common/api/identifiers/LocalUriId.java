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
package org.kie.efesto.common.api.identifiers;

import java.util.Objects;

/**
 * An abstract class for a {@link LocalId} that is represented as a Path.
 * <p>
 * Components should extend this class to get a default base implementation of their
 * LocalId.
 */
public abstract class LocalUriId implements LocalId {
    private LocalUri path;

    public LocalUriId() {
    }

    public LocalUriId(LocalUri path) {
        this.path = path;
    }

    @Override
    public LocalUri asLocalUri() {
        return path;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

    @Override
    public String toString() {
        return String.format("LocalUriId(%s)", path);
    }

    @Override
    public boolean equals(Object o) {
        return this == o ||
                o instanceof LocalId &&
                        Objects.equals(path, ((Id) o).toLocalId().asLocalUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
