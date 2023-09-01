package org.kie.dmn.validation.dtanalysis.model;

import java.util.Collections;
import java.util.List;

public class DDTAInputClause implements Domain {

    private final Interval domainMinMax;
    private final List discreteValues;
    private final List<Comparable<?>> discreteDMNOrder;
    private final boolean allowNull;

    public DDTAInputClause(Interval domainMinMax, boolean allowNull) {
        this.domainMinMax = domainMinMax;
        this.discreteValues = Collections.emptyList();
        this.discreteDMNOrder = Collections.emptyList();
        this.allowNull = allowNull;
    }

    public DDTAInputClause(Interval domainMinMax, boolean allowNull, List discreteValues, List<Comparable<?>> discreteDMNOrder) {
        this.domainMinMax = domainMinMax;
        this.discreteValues = discreteValues;
        this.discreteDMNOrder = discreteDMNOrder;
        this.allowNull = allowNull;
    }

    @Override
    public Bound<?> getMin() {
        return domainMinMax.getLowerBound();
    }

    @Override
    public Bound<?> getMax() {
        return domainMinMax.getUpperBound();
    }

    @Override
    public Interval getDomainMinMax() {
        return domainMinMax;
    }

    @Override
    public List getDiscreteValues() {
        return Collections.unmodifiableList(discreteValues);
    }

    @Override
    public boolean isDiscreteDomain() {
        return discreteValues != null && !discreteValues.isEmpty();
    }

    /**
     * Used by MC/DC
     * NOT to be used by Gap analysis, as domain ordering is not necessarily respected while modeling.
     */
    public List<Comparable<?>> getDiscreteDMNOrder() {
        return discreteDMNOrder;
    }

    /**
     * the null was explicitly specified in lov
     */
    public boolean isAllowNull() {
        return allowNull;
    }
}
