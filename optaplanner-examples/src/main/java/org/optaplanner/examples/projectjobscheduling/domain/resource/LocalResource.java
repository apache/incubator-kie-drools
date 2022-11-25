package org.optaplanner.examples.projectjobscheduling.domain.resource;

import org.optaplanner.examples.projectjobscheduling.domain.Project;

public class LocalResource extends Resource {

    private Project project;
    private boolean renewable;

    public LocalResource() {
    }

    public LocalResource(long id, Project project, boolean renewable) {
        super(id, 0);
        this.project = project;
        this.renewable = renewable;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean isRenewable() {
        return renewable;
    }

    public void setRenewable(boolean renewable) {
        this.renewable = renewable;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
