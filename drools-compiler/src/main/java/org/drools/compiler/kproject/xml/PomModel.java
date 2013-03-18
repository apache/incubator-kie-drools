package org.drools.compiler.kproject.xml;

import org.kie.api.builder.ReleaseId;

import java.util.ArrayList;
import java.util.List;

public class PomModel {

    private ReleaseId releaseId;
    private ReleaseId parentReleaseId;
    private List<ReleaseId> dependencies = new ArrayList<ReleaseId>();


    public ReleaseId getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(ReleaseId releaseId) {
        this.releaseId = releaseId;
    }

    public ReleaseId getParentReleaseId() {
        return parentReleaseId;
    }

    public void setParentReleaseId(ReleaseId parentReleaseId) {
        this.parentReleaseId = parentReleaseId;
    }

    public List<ReleaseId> getDependencies() {
        return dependencies;
    }

    public void addDependency(ReleaseId dependency) {
        this.dependencies.add(dependency);
    }
}
