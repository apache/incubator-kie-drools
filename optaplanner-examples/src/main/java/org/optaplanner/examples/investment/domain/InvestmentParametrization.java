package org.optaplanner.examples.investment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Institutional weightings.
 */
@XStreamAlias("InvestmentParametrization")
public class InvestmentParametrization extends AbstractPersistable {

    private long standardDeviationMillisMaximum; // In millis (so multiplied by 1000)

    public long getStandardDeviationMillisMaximum() {
        return standardDeviationMillisMaximum;
    }

    public void setStandardDeviationMillisMaximum(long standardDeviationMillisMaximum) {
        this.standardDeviationMillisMaximum = standardDeviationMillisMaximum;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public long calculateSquaredStandardDeviationFemtosMaximum() {
        return standardDeviationMillisMaximum * standardDeviationMillisMaximum
                * 1000L * 1000L * 1000L;
    }

}
