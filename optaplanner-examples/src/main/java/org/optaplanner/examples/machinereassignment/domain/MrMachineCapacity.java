package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistableJackson;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = MrMachineCapacity.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MrMachineCapacity extends AbstractPersistableJackson {

    private MrMachine machine;
    private MrResource resource;

    private long maximumCapacity;
    private long safetyCapacity;

    @SuppressWarnings("unused")
    MrMachineCapacity() { // For Jackson.
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

    public MrResource getResource() {
        return resource;
    }

    public long getMaximumCapacity() {
        return maximumCapacity;
    }

    public long getSafetyCapacity() {
        return safetyCapacity;
    }

    @JsonIgnore
    public boolean isTransientlyConsumed() {
        return resource.isTransientlyConsumed();
    }

}
