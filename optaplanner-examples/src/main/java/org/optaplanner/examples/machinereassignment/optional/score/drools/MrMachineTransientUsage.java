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

package org.optaplanner.examples.machinereassignment.optional.score.drools;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingLong;

import java.util.Comparator;
import java.util.Objects;

import org.optaplanner.examples.machinereassignment.domain.MrMachine;
import org.optaplanner.examples.machinereassignment.domain.MrMachineCapacity;
import org.optaplanner.examples.machinereassignment.domain.MrResource;

public class MrMachineTransientUsage implements Comparable<MrMachineTransientUsage> {

    private static final Comparator<MrMachineTransientUsage> COMPARATOR = comparing(
            (MrMachineTransientUsage transientUsage) -> transientUsage.getClass().getName())
                    .thenComparing(transientUsage -> transientUsage.machineCapacity, comparingLong(MrMachineCapacity::getId))
                    .thenComparingLong(transientUsage -> transientUsage.usage);

    private MrMachineCapacity machineCapacity;
    private long usage;

    public MrMachineTransientUsage(MrMachineCapacity machineCapacity, long usage) {
        this.machineCapacity = machineCapacity;
        this.usage = usage;
    }

    public MrMachineCapacity getMachineCapacity() {
        return machineCapacity;
    }

    public long getUsage() {
        return usage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MrMachineTransientUsage other = (MrMachineTransientUsage) o;
        return Objects.equals(machineCapacity, other.machineCapacity) &&
                usage == other.usage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(machineCapacity, usage);
    }

    public MrMachine getMachine() {
        return machineCapacity.getMachine();
    }

    public MrResource getResource() {
        return machineCapacity.getResource();
    }

    @Override
    public String toString() {
        return getMachine() + "-" + getResource() + "=" + usage;
    }

    @Override
    public int compareTo(MrMachineTransientUsage o) {
        return COMPARATOR.compare(this, o);
    }
}
