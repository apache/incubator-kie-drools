package org.drools.testcoverage.common.model;

public class MyFact {

    private String name = null;
    private Integer currentValue = null;
    private Integer previousValue = null;

    public MyFact(final String name, final Integer currentValue) {
        super();
        this.name = name;
        this.currentValue = currentValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Integer currentValue) {
        this.previousValue = this.currentValue;  // save previous value
        this.currentValue = currentValue;
    }
    public Integer getPreviousValue() {
        return previousValue;
    }

    @Override
    public String toString() {
        return "MyFact [currentValue=" + currentValue + ", previousValue="
                + previousValue + "]";
    }
}
