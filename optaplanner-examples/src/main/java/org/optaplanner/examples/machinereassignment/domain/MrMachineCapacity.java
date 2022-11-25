package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class MrMachineCapacity extends AbstractPersistable {

    private MrMachine machine;
    private MrResource resource;

    private long maximumCapacity;
    private long safetyCapacity;

    @SuppressWarnings("unused")
    MrMachineCapacity() {
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
