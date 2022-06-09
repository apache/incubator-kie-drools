package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("MrProcessRequirement")
public class MrProcessRequirement extends AbstractPersistable {

    private MrProcess process;
    private MrResource resource;

    private long usage;

    public MrProcessRequirement() {
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

    public void setProcess(MrProcess process) {
        this.process = process;
    }

    public MrResource getResource() {
        return resource;
    }

    public void setResource(MrResource resource) {
        this.resource = resource;
    }

    public long getUsage() {
        return usage;
    }

    public void setUsage(long usage) {
        this.usage = usage;
    }

}
