package org.kie.dmn.validation.dtanalysis.model;

import java.util.Collections;
import java.util.List;

public class DDTAInputClause {

    private final Interval domainMinMax;
    private final List discreteValues;

    public DDTAInputClause(Interval domainMinMax) {
        this.domainMinMax = domainMinMax;
        this.discreteValues = Collections.emptyList();
    }

    public DDTAInputClause(Interval domainMinMax, List discreteValues) {
        this.domainMinMax = domainMinMax;
        this.discreteValues = discreteValues;
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

    public List getDiscreteValues() {
        return Collections.unmodifiableList(discreteValues);
    }

    public boolean isDiscreteDomain() {
        return discreteValues != null;
    }

}
