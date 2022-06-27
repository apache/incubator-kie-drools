/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.efesto.common.api.model;

import java.util.Objects;

/**
 * A <code>GeneratedResource</code> not meant to be directly executed, linking to another <code>GeneratedResource</code>
 */
public final class GeneratedRedirectResource implements GeneratedResource {

    private static final long serialVersionUID = 1356917578380378083L;
    /**
     * the full reference identifier (e.g. "bar/resource/some_final_model")
     */
    private final FRI fri;

    /**
     * the full reference identifier (e.g. "bar/resource/some_final_model")
     */
    private final String target;

    public GeneratedRedirectResource() {
        this(null, null);
    }

    public GeneratedRedirectResource(FRI fri, String target) {
        this.fri = fri;
        this.target = target;
    }

    public FRI getFri() {
        return fri;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "GeneratedRedirectResource{" +
                "fri='" + fri + '\'' +
                ", target='" + target + '\'' +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GeneratedRedirectResource that = (GeneratedRedirectResource) o;
        return Objects.equals(fri, that.fri) && Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fri, target);
    }
}
