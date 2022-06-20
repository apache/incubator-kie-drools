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

import java.util.List;
import java.util.Objects;

/**
 * A <code>GeneratedResource</code> meant to be directly executed, with a <b>full reference name (frn)</b> identifier
 */
public final class GeneratedExecutableResource implements GeneratedResource {

    private static final long serialVersionUID = 6588314882989626752L;
    /**
     * the full reference identifier (e.g. "bar/resource/some_final_model")
     */
    private final FRI fri;


    private final List<String> fullClassNames;

    public GeneratedExecutableResource() {
        this(null, null);
    }

    public GeneratedExecutableResource(FRI fri, List<String> fullClassNames) {
        this.fri = fri;
        this.fullClassNames = fullClassNames;
    }

    public FRI getFri() {
        return fri;
    }

    public List<String> getFullClassNames() {
        return fullClassNames;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fri);
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
        if (!(o instanceof GeneratedExecutableResource)) {
            return false;
        }
        GeneratedExecutableResource that = (GeneratedExecutableResource) o;
        return fri.equals(that.fri);
    }

    @Override
    public String toString() {
        return "GeneratedExecutableResource{" +
                "fri='" + fri + '\'' +
                "} " + super.toString();
    }
}
