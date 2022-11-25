package org.optaplanner.examples.nurserostering.domain.contract;

public class BooleanContractLine extends ContractLine {

    private boolean enabled;
    private int weight;

    public BooleanContractLine() {
    }

    public BooleanContractLine(long id, Contract contract, ContractLineType contractLineType, boolean enabled, int weight) {
        super(id, contract, contractLineType);
        this.enabled = enabled;
        this.weight = weight;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

}
