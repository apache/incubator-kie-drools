/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.persistence.jpa.marshaller;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

@MappedSuperclass
public abstract class VariableEntity implements Serializable {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "MAP_VAR_ID", nullable = true)
    private Set<MappedVariable> mappedVariables;

    public Set<MappedVariable> getMappedVariables() {
        return mappedVariables;
    }

    public void setMappedVariables(Set<MappedVariable> mappedVariables) {
        this.mappedVariables = mappedVariables;
    }

    public void addMappedVariables(MappedVariable mappedVariable) {
        if (this.mappedVariables == null) {
            this.mappedVariables = new HashSet<MappedVariable>();
        }
        this.mappedVariables.add(mappedVariable);
    }

    public void removeMappedVariables(MappedVariable mappedVariable) {
        if (this.mappedVariables == null) {
            return;
        }
        this.mappedVariables.remove(mappedVariable);
    }

    public MappedVariable findMappedVariables(MappedVariable mappedVariable) {
        if (this.mappedVariables == null) {
            return null;
        }

        MappedVariable found = null;

        for (MappedVariable variable : mappedVariables) {
            if (variable.equals(mappedVariable)) {
                found = variable;
                break;
            }
        }

        return found;
    }
}
