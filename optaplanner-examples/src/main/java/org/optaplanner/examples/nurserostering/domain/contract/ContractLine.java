package org.optaplanner.examples.nurserostering.domain.contract;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamInclude;

@XStreamAlias("ContractLine")
@XStreamInclude({
        BooleanContractLine.class,
        MinMaxContractLine.class
})
public abstract class ContractLine extends AbstractPersistable {

    private Contract contract;
    private ContractLineType contractLineType;

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
