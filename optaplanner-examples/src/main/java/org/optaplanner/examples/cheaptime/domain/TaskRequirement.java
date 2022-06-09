package org.optaplanner.examples.cheaptime.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("CtTaskRequirement")
public class TaskRequirement extends AbstractPersistable {

    private Resource resource;
    private int resourceUsage;

    public TaskRequirement() {

    }

    public TaskRequirement(long id, Resource resource, int resourceUsage) {
        super(id);
        this.resource = resource;
        this.resourceUsage = resourceUsage;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public int getResourceUsage() {
        return resourceUsage;
    }

    public void setResourceUsage(int resourceUsage) {
        this.resourceUsage = resourceUsage;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
