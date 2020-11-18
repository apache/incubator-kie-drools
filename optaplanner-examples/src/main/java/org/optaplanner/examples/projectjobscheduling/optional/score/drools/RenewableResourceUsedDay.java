/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.projectjobscheduling.optional.score.drools;

import java.util.Objects;

import org.optaplanner.examples.projectjobscheduling.domain.resource.Resource;

public class RenewableResourceUsedDay {

    private final Resource resource;
    private final int usedDay;

    public RenewableResourceUsedDay(Resource resource, int usedDay) {
        this.resource = resource;
        this.usedDay = usedDay;
    }

    public Resource getResource() {
        return resource;
    }

    public int getUsedDay() {
        return usedDay;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RenewableResourceUsedDay other = (RenewableResourceUsedDay) o;
        return usedDay == other.usedDay &&
                Objects.equals(resource, other.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, usedDay);
    }

    @Override
    public String toString() {
        return resource + " on " + usedDay;
    }

    public int getResourceCapacity() {
        return resource.getCapacity();
    }

}
