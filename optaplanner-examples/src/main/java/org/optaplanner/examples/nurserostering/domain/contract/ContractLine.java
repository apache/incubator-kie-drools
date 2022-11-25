package org.optaplanner.examples.nurserostering.domain.contract;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BooleanContractLine.class, name = "boolean"),
        @JsonSubTypes.Type(value = MinMaxContractLine.class, name = "minMax"),
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public abstract class ContractLine extends AbstractPersistable {

    private Contract contract;
    private ContractLineType contractLineType;

    protected ContractLine() {
    }

    protected ContractLine(long id, Contract contract, ContractLineType contractLineType) {
        super(id);
        this.contract = contract;
        this.contractLineType = contractLineType;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public ContractLineType getContractLineType() {
        return contractLineType;
    }

    public void setContractLineType(ContractLineType contractLineType) {
        this.contractLineType = contractLineType;
    }

    public abstract boolean isEnabled();

    @Override
    public String toString() {
        return contract + "-" + contractLineType;
    }
}
