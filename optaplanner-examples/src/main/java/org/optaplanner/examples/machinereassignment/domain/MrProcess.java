package org.optaplanner.examples.machinereassignment.domain;

import java.util.List;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class MrProcess extends AbstractPersistable {

    private MrService service;
    private int moveCost;

    // Order is equal to resourceList so resource.getIndex() can be used
    private List<MrProcessRequirement> processRequirementList;

    public MrProcess() {
    }

    public MrProcess(int moveCost) {
        this.moveCost = moveCost;
    }

    public MrProcess(MrService service) {
        this.service = service;
    }

    public MrProcess(long id, MrService service, int moveCost) {
        super(id);
        this.service = service;
        this.moveCost = moveCost;
    }

    public MrService getService() {
        return service;
    }

    public int getMoveCost() {
        return moveCost;
    }

    public List<MrProcessRequirement> getProcessRequirementList() {
        return processRequirementList;
    }

    public void setProcessRequirementList(List<MrProcessRequirement> processRequirementList) {
        this.processRequirementList = processRequirementList;
    }

    public MrProcessRequirement getProcessRequirement(MrResource resource) {
        return processRequirementList.get(resource.getIndex());
    }

    public long getUsage(MrResource resource) {
        return resource.getIndex() >= processRequirementList.size() ? 0L
                : processRequirementList.get(resource.getIndex()).getUsage();
    }

    @JsonIgnore
    public int getUsageMultiplicand() {
        int multiplicand = 1;
        for (MrProcessRequirement processRequirement : processRequirementList) {
            multiplicand *= processRequirement.getUsage();
        }
        return multiplicand;
    }

}
