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

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

import java.util.Objects;

/**
 * A <code>GeneratedResource</code> meant to be directly executed, with a <b>full reference name (frn)</b> identifier
 */
public final class GeneratedModelResource implements GeneratedResource {

    private static final long serialVersionUID = 6588314882989626752L;
    /**
     * the full reference identifier (e.g. "bar/resource/some_final_model")
     */
    private final ModelLocalUriId modelLocalUriId;


    private final String modelSource;

    public GeneratedModelResource() {
        this(null, null);
    }

    public GeneratedModelResource(ModelLocalUriId modelLocalUriId, String modelSource) {
        this.modelLocalUriId = modelLocalUriId;
        this.modelSource = modelSource;
    }

    public ModelLocalUriId getModelLocalUriId() {
        return modelLocalUriId;
    }

    public String getModelSource() {
        return modelSource;
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelLocalUriId);
    }

    /**
     * Two <code>GeneratedExecutableResource</code>s are equals if they have the same full path <b>OR</b>
     * if they have the same full reference name
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (super.equals(o)) return true;
        if (!(o instanceof GeneratedModelResource)) {
            return false;
        }
        GeneratedModelResource that = (GeneratedModelResource) o;
        return modelLocalUriId.equals(that.modelLocalUriId);
    }

    @Override
    public String toString() {
        return "GeneratedModelResource{" +
                "modelLocalUriId='" + modelLocalUriId + '\'' +
                "} " + super.toString();
    }
}
