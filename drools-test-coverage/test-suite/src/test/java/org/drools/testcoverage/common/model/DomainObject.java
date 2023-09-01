package org.drools.testcoverage.common.model;

public class DomainObject {

    private String message;
    private int value;
    private double value2;
    private long id;
    private Interval interval;

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    public double getValue2() {
        return value2;
    }

    public void setValue2(final double value2) {
        this.value2 = value2;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(final long id ) {
        this.id = id;
    }

    /**
     * @return the interval
     */
    public Interval getInterval() {
        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval(final Interval interval ) {
        this.interval = interval;
    }

    
}
