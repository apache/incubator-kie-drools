package org.optaplanner.examples.nurserostering.domain.contract;

import java.util.List;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;
import org.optaplanner.examples.nurserostering.domain.WeekendDefinition;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class Contract extends AbstractPersistable {

    private String code;
    private String description;
    private WeekendDefinition weekendDefinition;
    private List<ContractLine> contractLineList;

    public Contract() {
    }

    public Contract(long id) {
        super(id);
    }

    public Contract(long id, String code, String description) {
        super(id);
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WeekendDefinition getWeekendDefinition() {
        return weekendDefinition;
    }

    public void setWeekendDefinition(WeekendDefinition weekendDefinition) {
        this.weekendDefinition = weekendDefinition;
    }

    public List<ContractLine> getContractLineList() {
        return contractLineList;
    }

    public void setContractLineList(List<ContractLine> contractLineList) {
        this.contractLineList = contractLineList;
    }

    @Override
    public String toString() {
        return code;
    }

    @JsonIgnore
    public int getWeekendLength() {
        return weekendDefinition.getWeekendLength();
    }

    @JsonIgnore
    public ContractLine getFirstConstractLine() {
        return contractLineList.get(0);
    }

}
