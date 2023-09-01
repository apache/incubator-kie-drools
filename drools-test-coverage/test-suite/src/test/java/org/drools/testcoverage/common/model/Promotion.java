package org.drools.testcoverage.common.model;


public class Promotion {

    private String person;
    private String job;

    public Promotion(String person, String job) {
        super();
        this.person = person;
        this.job = job;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
