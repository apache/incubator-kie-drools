package org.drools.compiler.kproject.xml;

import org.kie.api.builder.ReleaseId;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PomModel {

    private ReleaseId releaseId;
    private ReleaseId parentReleaseId;
    private Set<ReleaseId> dependencies = new HashSet<ReleaseId>();


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

    public Collection<ReleaseId> getDependencies() {
        return dependencies;
    }

    public void addDependency(ReleaseId dependency) {
        this.dependencies.add(dependency);
    }
}
