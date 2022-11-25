package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class MrProcessRequirement extends AbstractPersistable {

    private MrProcess process;
    private MrResource resource;

    private long usage;

    @SuppressWarnings("unused")
    MrProcessRequirement() {
    }

    public MrProcessRequirement(MrProcess process, MrResource resource, long usage) {
        this.process = process;
        this.resource = resource;
        this.usage = usage;
    }

    public MrProcessRequirement(long id, MrProcess process, MrResource resource, long usage) {
        super(id);
        this.process = process;
        this.resource = resource;
        this.usage = usage;
    }

    public MrProcess getProcess() {
        return process;
    }

    public MrResource getResource() {
        return resource;
    }

    public long getUsage() {
        return usage;
    }

}
