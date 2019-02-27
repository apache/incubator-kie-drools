package org.kie.dmn.validation.dtanalysis.model;


public class DDTAInputClause {

    private final Interval domainMinMax;

    public DDTAInputClause(Interval domainMinMax) {
        this.domainMinMax = domainMinMax;
    }

    public Bound<?> getMin() {
        return domainMinMax.getLowerBound();
    }

    public Bound<?> getMax() {
        return domainMinMax.getUpperBound();
    }

    public Interval getDomainMinMax() {
        return domainMinMax;
    }

}
