package org.optaplanner.examples.cheaptime.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("CtMachineCapacity")
public class MachineCapacity extends AbstractPersistable {

    private Resource resource;
    private int capacity;

    public MachineCapacity() {

    }

    public MachineCapacity(long id, Resource resource, int capacity) {
        super(id);
        this.resource = resource;
        this.capacity = capacity;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

}
