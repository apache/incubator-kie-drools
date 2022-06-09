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
