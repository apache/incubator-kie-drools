/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("MrMachineCapacity")
public class MrMachineCapacity extends AbstractPersistable {

    private MrMachine machine;
    private MrResource resource;

    private long maximumCapacity;
    private long safetyCapacity;

    public MrMachineCapacity() {
    }

    public MrMachineCapacity(MrMachine machine, MrResource resource, long maximumCapacity, long safetyCapacity) {
        this.machine = machine;
        this.resource = resource;
        this.maximumCapacity = maximumCapacity;
        this.safetyCapacity = safetyCapacity;
    }

    public MrMachineCapacity(long id, MrMachine machine, MrResource resource, long maximumCapacity, long safetyCapacity) {
        super(id);
        this.machine = machine;
        this.resource = resource;
        this.maximumCapacity = maximumCapacity;
        this.safetyCapacity = safetyCapacity;
    }

    public MrMachine getMachine() {
        return machine;
    }

    public void setMachine(MrMachine machine) {
        this.machine = machine;
    }

    public MrResource getResource() {
        return resource;
    }

    public void setResource(MrResource resource) {
        this.resource = resource;
    }

    public long getMaximumCapacity() {
        return maximumCapacity;
    }

    public void setMaximumCapacity(long maximumCapacity) {
        this.maximumCapacity = maximumCapacity;
    }

    public long getSafetyCapacity() {
        return safetyCapacity;
    }

    public void setSafetyCapacity(long safetyCapacity) {
        this.safetyCapacity = safetyCapacity;
    }

    public boolean isTransientlyConsumed() {
        return resource.isTransientlyConsumed();
    }

}
