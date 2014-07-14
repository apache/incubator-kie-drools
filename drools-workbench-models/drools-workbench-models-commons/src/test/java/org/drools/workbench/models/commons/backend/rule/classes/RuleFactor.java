package org.drools.workbench.models.commons.backend.rule.classes;

public class RuleFactor {

    private String name;

    private int defaultPriority;

    private int defaultWeightage;

    private int priorityImpact;

    private int weightageImpact;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public int getDefaultPriority() {
        return defaultPriority;
    }

    public void setDefaultPriority( int defaultPriority ) {
        this.defaultPriority = defaultPriority;
    }

    public int getDefaultWeightage() {
        return defaultWeightage;
    }

    public void setDefaultWeightage( int defaultWeightage ) {
        this.defaultWeightage = defaultWeightage;
    }

    public int getPriorityImpact() {
        return priorityImpact;
    }

    public void setPriorityImpact( int priorityImpact ) {
        this.priorityImpact = priorityImpact;
    }

    public int getWeightageImpact() {
        return weightageImpact;
    }

    public void setWeightageImpact( int weightageImpact ) {
        this.weightageImpact = weightageImpact;
    }
}