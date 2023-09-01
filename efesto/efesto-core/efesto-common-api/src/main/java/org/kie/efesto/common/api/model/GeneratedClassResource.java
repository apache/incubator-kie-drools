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
package org.kie.efesto.common.api.model;

import java.util.Objects;

/**
 * A <code>GeneratedResource</code> meant to map a <code>Class</code>
 */
public final class GeneratedClassResource implements GeneratedResource {

    private static final long serialVersionUID = 8140824908598306598L;
    /**
     * the full class name of generated class
     */
    private final String fullClassName;

    public GeneratedClassResource() {
        this(null);
    }

    public GeneratedClassResource(String fullClassName) {
        this.fullClassName = fullClassName;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    @Override
    public String toString() {
        return "GeneratedClassResource{" +
                "fullClassName='" + fullClassName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneratedClassResource that = (GeneratedClassResource) o;
        return Objects.equals(fullClassName, that.fullClassName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullClassName);
    }
}
