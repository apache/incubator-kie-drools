package org.optaplanner.examples.nurserostering.domain.contract;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("BooleanContractLine")
public class BooleanContractLine extends ContractLine {

    private boolean enabled;
    private int weight;

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
