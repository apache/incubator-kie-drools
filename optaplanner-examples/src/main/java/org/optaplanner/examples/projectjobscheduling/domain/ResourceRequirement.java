package org.optaplanner.examples.projectjobscheduling.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.projectjobscheduling.domain.resource.Resource;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("PjsResourceRequirement")
public class ResourceRequirement extends AbstractPersistable {

    private ExecutionMode executionMode;
    private Resource resource;
    private int requirement;

    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    public void setExecutionMode(ExecutionMode executionMode) {
        this.executionMode = executionMode;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public int getRequirement() {
        return requirement;
    }

    public void setRequirement(int requirement) {
        this.requirement = requirement;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public boolean isResourceRenewable() {
        return resource.isRenewable();
    }

}
