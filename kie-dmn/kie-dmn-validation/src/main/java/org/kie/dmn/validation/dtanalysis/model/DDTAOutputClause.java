package org.kie.dmn.validation.dtanalysis.model;

import java.util.Collections;
import java.util.List;

public class DDTAOutputClause {

    private final Interval domainMinMax;
    private final List discreteValues;
    private final List outputOrder;

    public DDTAOutputClause(Interval domainMinMax) {
        this.domainMinMax = domainMinMax;
        this.discreteValues = Collections.emptyList();
        this.outputOrder = Collections.emptyList();
    }

    public DDTAOutputClause(Interval domainMinMax, List discreteValues, List outputOrder) {
        this.domainMinMax = domainMinMax;
        this.discreteValues = discreteValues;
        this.outputOrder = outputOrder;
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
        return discreteValues != null && !discreteValues.isEmpty();
    }

    public List getOutputOrder() {
        return Collections.unmodifiableList(outputOrder);
    }
}
