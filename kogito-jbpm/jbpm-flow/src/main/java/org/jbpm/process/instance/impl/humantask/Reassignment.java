/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.instance.impl.humantask;

import java.util.Objects;
import java.util.Set;

public class Reassignment {

    private Set<String> potentialUsers;
    private Set<String> potentialGroups;

    public Reassignment(Set<String> potentialUsers, Set<String> potentialGroups) {
        this.potentialUsers = potentialUsers;
        this.potentialGroups = potentialGroups;
    }

    public Set<String> getPotentialUsers() {
        return potentialUsers;
    }

    public Set<String> getPotentialGroups() {
        return potentialGroups;
    }

    @Override
    public String toString() {
        return "Reassigment [potentialUsers=" + potentialUsers + ", potentialGroups=" + potentialGroups + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(potentialGroups, potentialUsers);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Reassignment))
            return false;
        Reassignment other = (Reassignment) obj;
        return Objects.equals(potentialGroups, other.potentialGroups) && Objects.equals(potentialUsers,
                other.potentialUsers);
    }
}
