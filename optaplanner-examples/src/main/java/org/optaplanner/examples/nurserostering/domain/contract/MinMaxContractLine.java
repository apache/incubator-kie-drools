package org.optaplanner.examples.nurserostering.domain.contract;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MinMaxContractLine extends ContractLine {

    private boolean minimumEnabled;
    private int minimumValue;
    private int minimumWeight;

    private boolean maximumEnabled;
    private int maximumValue;
    private int maximumWeight;

    public MinMaxContractLine() {
    }

    public MinMaxContractLine(long id, Contract contract, ContractLineType contractLineType, boolean minimumEnabled,
            boolean maximumEnabled) {
        super(id, contract, contractLineType);
        this.minimumEnabled = minimumEnabled;
        this.maximumEnabled = maximumEnabled;
    }

    public boolean isViolated(int count) {
        return getViolationAmount(count) != 0;
    }

    public int getViolationAmount(int count) {
        if (minimumEnabled && count < minimumValue) {
            return (minimumValue - count) * minimumWeight;
        } else if (maximumEnabled && count > maximumValue) {
            return (count - maximumValue) * maximumWeight;
        } else {
            return 0;
        }
    }

    public boolean isMinimumEnabled() {
        return minimumEnabled;
    }

    public void setMinimumEnabled(boolean minimumEnabled) {
        this.minimumEnabled = minimumEnabled;
    }

    public int getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(int minimumValue) {
        this.minimumValue = minimumValue;
    }

    public int getMinimumWeight() {
        return minimumWeight;
    }

    public void setMinimumWeight(int minimumWeight) {
        this.minimumWeight = minimumWeight;
    }

    public boolean isMaximumEnabled() {
        return maximumEnabled;
    }

    public void setMaximumEnabled(boolean maximumEnabled) {
        this.maximumEnabled = maximumEnabled;
    }

    public int getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(int maximumValue) {
        this.maximumValue = maximumValue;
    }

    public int getMaximumWeight() {
        return maximumWeight;
    }

    public void setMaximumWeight(int maximumWeight) {
        this.maximumWeight = maximumWeight;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return minimumEnabled || maximumEnabled;
    }

}
