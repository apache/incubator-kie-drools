package org.drools.model.codegen.execmodel.domain;

public class StockFact {
    private final String company;
    private final long duration;

    public StockFact( String company ) {
        this( company, 0 );
    }

    public StockFact( String company, long duration ) {
        this.company = company;
        this.duration = duration;
    }

    public String getCompany() {
        return company;
    }

    public long getDuration() {
        return duration;
    }
}